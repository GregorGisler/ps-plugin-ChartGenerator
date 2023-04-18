package com.appiancorp.ps.ss.util;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.internal.series.Series;
import org.knowm.xchart.style.AxesChartStyler;
import org.knowm.xchart.style.Styler.*;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.appiancorp.ps.type.ChartThreshold;
import com.appiancorp.ps.type.DataSeries;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class XChartCreator {
  private static final Logger LOG = Logger.getLogger(XChartCreator.class);

  public enum XChartSettings {
    TYPE,
    WIDTH,
    HEIGHT,
    TITLE,
    TITLEFONTSIZE,
    SHOWTITLE,
    XLABEL,
    XLABELANGLE,
    XLABELVALUES,
    YLABEL,
    AXISLABELFONTSIZE,
    AXISVALUEFONTSIZE,
    SHOWLEGEND,
    LEGENDPOSITION,
    LEGENDFONTSIZE,
    MARKER,
    YAXISMAX,
    SHOWANNOTATIONS,
    // ANNOTATIONSANGLE,
    ANNOTATIONSFONTSIZE,
    COLOR,
    DONUTTHICKNESS
  }

  public enum ChartTypes {
    LINE,
    COLUMN,
    PIE,
    DONUT
  }

  public enum XYSeriesMarkers {
    NONE,
    CIRCLE,
    DIAMOND,
    SQUARE,
    TRIANGLEDOWN,
    TRIANGLEUP,
    CROSS,
    PLUS,
    TRAPEZOID,
    OVAL,
    RECTANGLE
  }

  public enum LegendPositions {
    RIGHT,
    BOTTOM
  }

  // private Long thresholdType;

  public XChartCreator() {
  }

  public Chart create(JsonObject chartSettings, List<DataSeries> dataSeriesList, List<ChartThreshold> thresholds) throws Exception {
    Chart chart = null;

    // convert the json settings to a hashmap<XChartSettings, JsonElement>
    Set<Entry<String, JsonElement>> settingsSet = chartSettings.entrySet();
    Map<XChartSettings, JsonElement> settingsMap = new HashMap<XChartSettings, JsonElement>();
    for (Map.Entry<String, JsonElement> setting : settingsSet) {
      try {
        settingsMap.put(XChartSettings.valueOf(setting.getKey().toUpperCase()), setting.getValue());
      } catch (IllegalArgumentException e) {
        // unknown attribute in the JSON string - skip it
      }
    }

    validateRequiredSettings(settingsMap);

    JsonElement typeSetting = settingsMap.get(XChartSettings.TYPE);
    if (typeSetting != null) {
      ChartTypes chartType = null;
      try {
        chartType = ChartTypes.valueOf(typeSetting.getAsString().toUpperCase());
      } catch (IllegalArgumentException e) {
        // unknown chart type
        LOG.error("Unknown chart type: " + typeSetting.getAsString(), e);
        throw new IllegalArgumentException("Unknown chart type: " + typeSetting.getAsString());
      }

      switch (chartType) {
      case LINE:
        chart = createLineChart(settingsMap, dataSeriesList, thresholds);
        break;
      case COLUMN:
        chart = createColumnChart(settingsMap, dataSeriesList, thresholds);
        break;
      case PIE:
        chart = createPieChart(settingsMap, dataSeriesList, thresholds, false);
        break;
      case DONUT:
        chart = createPieChart(settingsMap, dataSeriesList, thresholds, true);
        break;
      default:
        break;
      }
    }

    return chart;
  }

  // Required settings: type, width, height
  private void validateRequiredSettings(Map<XChartSettings, JsonElement> settingsMap) throws Exception {
    if (!settingsMap.containsKey(XChartSettings.TYPE)) {
      throw new IllegalArgumentException("Mising required chart setting: type");
    }
    if (!settingsMap.containsKey(XChartSettings.WIDTH)) {
      throw new IllegalArgumentException("Mising required chart setting: width");
    }
    if (!settingsMap.containsKey(XChartSettings.HEIGHT)) {
      throw new IllegalArgumentException("Mising required chart setting: height");
    }
  }

  private PieChart createPieChart(Map<XChartSettings, JsonElement> settingsMap, List<DataSeries> dataSeriesList,
    List<ChartThreshold> thresholds, boolean donut) {
    PieChart pieChart = new PieChartBuilder()
      .width(settingsMap.get(XChartSettings.WIDTH).getAsInt())
      .height(settingsMap.get(XChartSettings.HEIGHT).getAsInt())
      .build();

    // Common settings to all charts
    setCommonSettings(pieChart, settingsMap);

    // specific settings
    pieChart.getStyler().setPlotBorderVisible(false);
    // donut chart specific settings
    if (donut) {
      pieChart.getStyler().setDefaultSeriesRenderStyle(PieSeries.PieSeriesRenderStyle.Donut);
      for (Map.Entry<XChartSettings, JsonElement> setting : settingsMap.entrySet()) {
        switch (setting.getKey()) {
        case DONUTTHICKNESS:
          pieChart.getStyler().setDonutThickness(setting.getValue().getAsDouble());
          pieChart.getStyler().setDonutThickness(0.6);
          break;
        default:
          break;
        }
      }
    }

    for (DataSeries dataSeries : dataSeriesList) {
      PieSeries series = pieChart.addSeries(dataSeries.getName(), dataSeries.getPieValue());
      setSeriesSettings(dataSeries, series);
    }

    return pieChart;
  }

  private CategoryChart createColumnChart(Map<XChartSettings, JsonElement> settingsMap, List<DataSeries> dataSeriesList,
    List<ChartThreshold> thresholds) {
    CategoryChart categoryChart = new CategoryChartBuilder()
      .width(settingsMap.get(XChartSettings.WIDTH).getAsInt())
      .height(settingsMap.get(XChartSettings.HEIGHT).getAsInt())
      .build();

    // Common settings to all charts
    setCommonSettings(categoryChart, settingsMap);

    // specific settings
    categoryChart.getStyler().setPlotGridHorizontalLinesVisible(false);
    categoryChart.getStyler().setPlotGridVerticalLinesVisible(false);

    // for(Map.Entry<XChartSettings, JsonElement> setting : settingsMap.entrySet()){
    // switch (setting.getKey()) {
    // default:
    // break;
    // }
    // }

    for (DataSeries dataSeries : dataSeriesList) {
      CategorySeries series = categoryChart.addSeries(dataSeries.getName(), null, dataSeries.getPrimitiveValues());
      // CategorySeriesRenderStyle style = series.getChartCategorySeriesRenderStyle();
      setSeriesSettings(dataSeries, series);
    }
    return categoryChart;
  }

  private XYChart createLineChart(Map<XChartSettings, JsonElement> settingsMap, List<DataSeries> dataSeriesList,
    List<ChartThreshold> thresholds) {
    XYChart lineChart = new XYChartBuilder()
      .width(settingsMap.get(XChartSettings.WIDTH).getAsInt())
      .height(settingsMap.get(XChartSettings.HEIGHT).getAsInt())
      .build();

    // Common settings to all charts
    setCommonSettings(lineChart, settingsMap);

    // Specific settings
    lineChart.getStyler().setPlotGridVerticalLinesVisible(false);

    // for(Map.Entry<XChartSettings, JsonElement> setting : settingsMap.entrySet()){
    // switch (setting.getKey()) {
    // default:
    // break;
    // }
    // }

    for (DataSeries dataSeries : dataSeriesList) {
      XYSeries xySeries = lineChart.addSeries(dataSeries.getName(), null, dataSeries.getPrimitiveValues());

      // default settings
      xySeries.setMarker(SeriesMarkers.NONE);

      setSeriesSettings(dataSeries, xySeries);
    }

    if (thresholds != null && thresholds.size() > 0) {
      // retrieve the length of the longest data serie

    }
    // TODO createThesholdLines

    return lineChart;
  }

  private void setSeriesSettings(DataSeries dataSeries, Series series) {
    // handle series specific settings
    if (dataSeries.getSettings() != null && dataSeries.getSettings().trim().length() > 0) {
      JsonObject serieSettings = new JsonParser().parse(dataSeries.getSettings()).getAsJsonObject();

      Set<Entry<String, JsonElement>> serieEntrySet = serieSettings.entrySet();
      for (Map.Entry<String, JsonElement> setting : serieEntrySet) {
        XChartSettings xsetting = null;
        try {
          xsetting = XChartSettings.valueOf(setting.getKey().toUpperCase());
        } catch (IllegalArgumentException e) {
          // unknown setting
          continue;
        }

        switch (xsetting) {
        case MARKER:
          setSeriesMarker(series, setting.getValue().getAsString());
          break;
        case COLOR:
          setSeriesColor(series, setting.getValue().getAsString());
          break;
        default:
          break;
        }

      }
    }
  }

  private void setCommonSettings(Chart chart, Map<XChartSettings, JsonElement> settingsMap) {
    chart.getStyler().setChartBackgroundColor(Color.WHITE);
    chart.getStyler().setLegendBorderColor(Color.WHITE);
    chart.getStyler().setPlotBorderColor(Color.WHITE);
    chart.getStyler().setPlotBorderColor(Color.WHITE);
    // TODO
    // chart.getStyler().setHasAnnotations(false);

    for (Map.Entry<XChartSettings, JsonElement> setting : settingsMap.entrySet()) {
      switch (setting.getKey()) {
      case TITLE:
        chart.setTitle(setting.getValue().getAsString());
        break;
      case SHOWTITLE:
        chart.getStyler().setChartTitleVisible(setting.getValue().getAsBoolean());
        break;
      case TITLEFONTSIZE:
        chart.getStyler().setChartTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, setting.getValue().getAsInt()));
        break;
      case SHOWLEGEND:
        chart.getStyler().setLegendVisible(setting.getValue().getAsBoolean());
        break;
      case LEGENDFONTSIZE:
        chart.getStyler().setLegendFont(new Font(Font.SANS_SERIF, Font.BOLD, setting.getValue().getAsInt()));
        chart.getStyler().setLegendSeriesLineLength(setting.getValue().getAsInt());
        break;
      case LEGENDPOSITION:
        if (setting.getValue().getAsString().toUpperCase().equals(LegendPositions.BOTTOM.toString())) {
          chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
          chart.getStyler().setLegendLayout(LegendLayout.Horizontal);
        } else {
          chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
          chart.getStyler().setLegendLayout(LegendLayout.Vertical);
        }
        break;
      case XLABEL:
        chart.setXAxisTitle(setting.getValue().getAsString());
        break;
      case XLABELVALUES:
        Map<Double, Object> labelValuesMap = new TreeMap<Double, Object>();
        JsonArray jArray = setting.getValue().getAsJsonArray();
        int labelValuesLength = jArray.size();
        // Start label value index at 1 if chart is XYChart
        int labelValueIndex = chart instanceof XYChart ? 1 : 0;
        for (int i = 0; i < labelValuesLength; i++) {
          if (jArray.get(i) != null && !jArray.get(i).isJsonNull() && !jArray.get(i).getAsString().isEmpty()) {
            labelValuesMap.put(new Double(labelValueIndex++), jArray.get(i).getAsString());
          }
        }
        // TODO
        // chart.setXAxisLabelOverrideMap(labelValuesMap);
        break;
      case YLABEL:
        chart.setYAxisTitle(setting.getValue().getAsString());
        break;
      case YAXISMAX:
        if (chart.getStyler() instanceof AxesChartStyler) {
          ((AxesChartStyler) chart.getStyler()).setYAxisMax(setting.getValue().getAsDouble());
        }
        break;
      case XLABELANGLE:
        if (chart.getStyler() instanceof AxesChartStyler) {
          ((AxesChartStyler) chart.getStyler()).setXAxisLabelRotation(setting.getValue().getAsInt());
        }
        break;
      case SHOWANNOTATIONS:
        // TODO
        // chart.getStyler().setHasAnnotations(setting.getValue().getAsBoolean());
        break;
      // TODO How to fix setAnnotationsRotations - not available in XChart Maven Library
      // case ANNOTATIONSANGLE :
      // chart.getStyler().setAnnotationsRotation(setting.getValue().getAsInt());
      // break;
      case ANNOTATIONSFONTSIZE:
        chart.getStyler().setAnnotationTextFont(new Font(Font.SANS_SERIF, Font.BOLD, setting.getValue().getAsInt()));
        break;
      case AXISLABELFONTSIZE:
        if (chart.getStyler() instanceof AxesChartStyler) {
          ((AxesChartStyler) chart.getStyler()).setAxisTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, setting.getValue().getAsInt()));
        }
        break;
      case AXISVALUEFONTSIZE:
        if (chart.getStyler() instanceof AxesChartStyler) {
          ((AxesChartStyler) chart.getStyler()).setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.BOLD, setting.getValue().getAsInt()));
        }
        break;
      default:
        break;
      }
    }
  }

  private void setSeriesMarker(Series series, String markerStr) {
    // Skip if the series is not a XYSeries
    if (!(series instanceof XYSeries)) {
      return;
    }
    XYSeriesMarkers marker = null;
    try {
      marker = XYSeriesMarkers.valueOf(markerStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      // marker not found
      marker = XYSeriesMarkers.NONE;
    }
    switch (marker) {
    case NONE:
      ((XYSeries) series).setMarker(SeriesMarkers.NONE);
      break;
    case TRIANGLEUP:
      ((XYSeries) series).setMarker(SeriesMarkers.TRIANGLE_UP);
      break;
    case TRIANGLEDOWN:
      ((XYSeries) series).setMarker(SeriesMarkers.TRIANGLE_DOWN);
      break;
    case CIRCLE:
      ((XYSeries) series).setMarker(SeriesMarkers.CIRCLE);
      break;
    case CROSS:
      ((XYSeries) series).setMarker(SeriesMarkers.CROSS);
      break;
    case DIAMOND:
      ((XYSeries) series).setMarker(SeriesMarkers.DIAMOND);
      break;
    case OVAL:
      ((XYSeries) series).setMarker(SeriesMarkers.OVAL);
      break;
    case PLUS:
      ((XYSeries) series).setMarker(SeriesMarkers.PLUS);
      break;
    case RECTANGLE:
      ((XYSeries) series).setMarker(SeriesMarkers.RECTANGLE);
      break;
    case SQUARE:
      ((XYSeries) series).setMarker(SeriesMarkers.SQUARE);
      break;
    case TRAPEZOID:
      ((XYSeries) series).setMarker(SeriesMarkers.TRAPEZOID);
      break;
    default:
      ((XYSeries) series).setMarker(SeriesMarkers.NONE);
      break;
    }
  }

  private void setSeriesColor(Series series, String hexColor) {
    try {
      Color color = Color.decode(Integer.parseUnsignedInt(hexColor, 16) + "");
      series.setFillColor(color);
      if (series instanceof XYSeries) {
        ((XYSeries) series).setLineColor(color);
        ((XYSeries) series).setMarkerColor(color);
      }
    } catch (NumberFormatException e) {
      // Failed to get the color from the hexadecimal value provided
      // Use deafult color
      LOG.error("Failed to decode the hex value " + hexColor + " to a valid color. Using default color.");
    }
  }

  // private ChartThreshold getChartThreshold(Object value) {
  // Object[] objArr = (Object[]) value;
  //
  // ChartThreshold ds = new ChartThreshold();
  // ds.setName((String) objArr[0]);
  // ds.setValue((Double) objArr[1]);
  // ds.setColor((String) objArr[2]);
  //
  // return ds;
  // }
}
