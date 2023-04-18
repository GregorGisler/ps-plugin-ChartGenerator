package com.appiancorp.ps.ss.util;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.*;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.AxesChartStyler;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;

import com.appiancorp.ps.type.DataSeries;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XChartCreatorTest {

  // Test settings for generating charts
  private static final int WIDTH = 500;
  private static final int HEIGHT = 300;
  private static final String TITLE = "TEST chart";
  private static final int TITLE_FONT_SIZE = 40;
  private static final int TITLE_FONT_SIZE_SMALL = 20;
  private static final boolean SHOW_TITLE = true;
  private static final String YLABEL = "Y Axis Label";
  private static final String XLABEL = "X Axis Label";
  private static final int XLABEL_ANGLE = 45;
  private static final String XLABEL_VALUES = "[\"1/5\", \"2/5\",\"3/5\", \"4/5\", \"5/5\"]";
  private static final String LEGEND_POSITION_BOTTOM = "BOTTom";
  private static final int AXIS_LABEL_FONT_SIZE = 15;
  private static final int AXIS_VALUE_FONT_SIZE = 6;
  private static final int LEGEND_FONT_SIZE = 30;
  private static final int LEGEND_FONT_SIZE_SMALL = 10;
  private static final boolean SHOW_LEGEND = true;
  private static final boolean SHOW_ANNOTATION = true;
  private static final int ANNOTATION_ANGLE = 90;
  private static final int ANNOTATION_FONT_SIZE = 12;
  private static final Double YAXIS_MAX_LABEL_VALUE = 50d;
  private static final Double DONUT_THICKNESS = 0.6;

  @Test
  public void testCreateLineChart() throws Exception {
    String settings = new StringBuilder()
      .append("{")
      .append("\"tyPe\":\"liNe\", ")
      .append("\"wIdth\":")
      .append(WIDTH)
      .append(",")
      .append("\"height\":")
      .append(HEIGHT)
      .append(",")
      .append("\"TITLE\":\"")
      .append(TITLE)
      .append("\",")
      .append("\"showTitle\":\"")
      .append(SHOW_TITLE)
      .append("\",")
      .append("\"TITLEfontsize\":\"")
      .append(TITLE_FONT_SIZE)
      .append("\",")
      .append("\"ylabel\":\"")
      .append(YLABEL)
      .append("\",")
      .append("\"xlabel\":\"")
      .append(XLABEL)
      .append("\",")
      .append("\"xlabelvalues\":")
      .append(XLABEL_VALUES)
      .append(", ")
      .append("\"legendPosition\":\"")
      .append(LEGEND_POSITION_BOTTOM)
      .append("\", ")
      .append("\"xlabelangle\":")
      .append(XLABEL_ANGLE)
      .append(", ")
      .append("\"axislabelFontSize\":")
      .append(AXIS_LABEL_FONT_SIZE)
      .append(", ")
      .append("\"axisValueFontSize\":")
      .append(AXIS_VALUE_FONT_SIZE)
      .append(", ")
      .append("\"legendFontSize\":")
      .append(LEGEND_FONT_SIZE)
      .append(", ")
      .append("\"SHOWlegend\":")
      .append(true)
      .append("}")
      .toString();

    // System.out.println(settings);
    JsonObject jSettings = new JsonParser().parse(settings).getAsJsonObject();

    List<DataSeries> dataSeries = new ArrayList<DataSeries>();

    DataSeries serie1 = new DataSeries();
    serie1.setName("Serie 1");
    serie1.setValues(new Double[] { 1d, 2d, 3d, 4d, 5d });
    dataSeries.add(serie1);

    DataSeries serie2 = new DataSeries();
    serie2.setName("Serie 2");
    serie2.setValues(new Double[] { 2d, 3d, 5d, 10d, 1d });
    serie2.setSettings("{\"marker\":\"circle\", \"aaa\":\"ad\", \"color\":\"3fcc3f\"}");
    dataSeries.add(serie2);

    XChartCreator chartCreator = new XChartCreator();
    Chart chart = chartCreator.create(jSettings, dataSeries, null);

    assertTrue(chart instanceof XYChart);
    assertEquals(WIDTH, chart.getWidth());
    assertEquals(HEIGHT, chart.getHeight());
    assertEquals(TITLE, chart.getTitle());
    assertEquals(SHOW_TITLE, chart.getStyler().isChartTitleVisible());
    assertEquals(TITLE_FONT_SIZE, chart.getStyler().getChartTitleFont().getSize());
    assertEquals(YLABEL, chart.getYAxisTitle());
    assertEquals(XLABEL, chart.getXAxisTitle());
    assertEquals(LegendPosition.OutsideS, chart.getStyler().getLegendPosition());
    assertEquals(LegendLayout.Horizontal, chart.getStyler().getLegendLayout());
    assertEquals(XLABEL_ANGLE, ((AxesChartStyler) chart.getStyler()).getXAxisLabelRotation());
    assertEquals(AXIS_LABEL_FONT_SIZE, ((AxesChartStyler) chart.getStyler()).getAxisTitleFont().getSize());
    assertEquals(AXIS_VALUE_FONT_SIZE, ((AxesChartStyler) chart.getStyler()).getAxisTickLabelsFont().getSize());
    assertEquals(LEGEND_FONT_SIZE, chart.getStyler().getLegendFont().getSize());
    assertEquals(LEGEND_FONT_SIZE, chart.getStyler().getLegendSeriesLineLength());
    assertEquals(SHOW_LEGEND, chart.getStyler().isLegendVisible());
    String testChart = "testLineChartwithjson";
    String filePath = "src/test/resources/";
    File targetFile = new File(filePath.concat(testChart));
    if (targetFile.exists()) {
      targetFile.delete();
    }
    ;
    BitmapEncoder.saveBitmapWithDPI(chart, filePath.concat(testChart), BitmapFormat.PNG, 300);
    assertTrue(Files.exists(Paths.get(filePath, testChart + ".png")));

    // documemtation for new parameters

    // TODO add the data series and xlabels test

  }

  @Test
  public void testCategoryChart() throws Exception {
    String settings = new StringBuilder()
      .append("{")
      .append("\"tyPe\":\"Column\", ")
      .append("\"wIdth\":")
      .append(WIDTH)
      .append(",")
      .append("\"height\":")
      .append(HEIGHT)
      .append(",")
      .append("\"TITLE\":\"")
      .append(TITLE)
      .append("\",")
      .append("\"showTitle\":\"")
      .append(SHOW_TITLE)
      .append("\",")
      .append("\"TITLEfontsize\":\"")
      .append(TITLE_FONT_SIZE)
      .append("\",")
      .append("\"ylabel\":\"")
      .append(YLABEL)
      .append("\",")
      .append("\"xlabel\":\"")
      .append(XLABEL)
      .append("\",")
      .append("\"xlabelvalues\":")
      .append(XLABEL_VALUES)
      .append(", ")
      .append("\"legendPosition\":\"")
      .append(LEGEND_POSITION_BOTTOM)
      .append("\", ")
      .append("\"xlabelangle\":")
      .append(XLABEL_ANGLE)
      .append(", ")
      .append("\"axislabelFontSize\":")
      .append(AXIS_LABEL_FONT_SIZE)
      .append(", ")
      .append("\"axisValueFontSize\":")
      .append(AXIS_VALUE_FONT_SIZE)
      .append(", ")
      .append("\"legendFontSize\":")
      .append(LEGEND_FONT_SIZE)
      .append(", ")
      .append("\"SHOWlegend\":")
      .append(true)
      .append(", ")
      .append("\"yaxismax\":")
      .append(YAXIS_MAX_LABEL_VALUE)
      .append(", ")
      .append("\"showAnnotations\":")
      .append(SHOW_ANNOTATION)
      .append(", ")
      .append("\"annotationsAngle\":")
      .append(ANNOTATION_ANGLE)
      .append(", ")
      .append("\"annotationsFontSIZE\":")
      .append(ANNOTATION_FONT_SIZE)
      .append("}")
      .toString();

    JsonObject jSettings = new JsonParser().parse(settings).getAsJsonObject();

    List<DataSeries> dataSeries = new ArrayList<DataSeries>();

    DataSeries serie1 = new DataSeries();
    serie1.setName("Serie 1");
    serie1.setValues(new Double[] { 1d, 5d, 10d, 15d, 1d });
    serie1.setSettings("{\"coloR\":\"d53535\"}");
    dataSeries.add(serie1);

    DataSeries serie2 = new DataSeries();
    serie2.setName("Serie 2");
    serie2.setValues(new Double[] { 2d, 3d, 5d, 10d, 35d });
    dataSeries.add(serie2);

    XChartCreator chartCreator = new XChartCreator();
    Chart chart = chartCreator.create(jSettings, dataSeries, null);

    assertTrue(chart instanceof CategoryChart);
    assertEquals(WIDTH, chart.getWidth());
    assertEquals(HEIGHT, chart.getHeight());
    assertEquals(TITLE, chart.getTitle());
    assertEquals(SHOW_TITLE, chart.getStyler().isChartTitleVisible());
    assertEquals(TITLE_FONT_SIZE, chart.getStyler().getChartTitleFont().getSize());
    assertEquals(YLABEL, chart.getYAxisTitle());
    assertEquals(XLABEL, chart.getXAxisTitle());
    assertEquals(LegendPosition.OutsideS, chart.getStyler().getLegendPosition());
    assertEquals(LegendLayout.Horizontal, chart.getStyler().getLegendLayout());
    assertEquals(XLABEL_ANGLE, ((AxesChartStyler) chart.getStyler()).getXAxisLabelRotation());
    assertEquals(AXIS_LABEL_FONT_SIZE, ((AxesChartStyler) chart.getStyler()).getAxisTitleFont().getSize());
    assertEquals(AXIS_VALUE_FONT_SIZE, ((AxesChartStyler) chart.getStyler()).getAxisTickLabelsFont().getSize());
    assertEquals(LEGEND_FONT_SIZE, chart.getStyler().getLegendFont().getSize());
    assertEquals(LEGEND_FONT_SIZE, chart.getStyler().getLegendSeriesLineLength());
    assertEquals(SHOW_LEGEND, chart.getStyler().isLegendVisible());
    assertEquals(YAXIS_MAX_LABEL_VALUE, ((AxesChartStyler) chart.getStyler()).getYAxisMax());
    // assertEquals(SHOW_ANNOTATION, chart.getStyler().hasAnnotations().booleanValue());
    // assertEquals(ANNOTATION_ANGLE, chart.getStyler().getAnnotationsRotation());
    assertEquals(ANNOTATION_FONT_SIZE, chart.getStyler().getAnnotationTextFont().getSize());
    String testChart = "testCategoryChartwithjson";
    String filePath = "src/test/resources/";
    File targetFile = new File(filePath.concat(testChart));
    if (targetFile.exists()) {
      targetFile.delete();
    }
    ;
    BitmapEncoder.saveBitmapWithDPI(chart, filePath.concat(testChart), BitmapFormat.PNG, 300);
    assertTrue(Files.exists(Paths.get(filePath, testChart + ".png")));
  }

  @Test
  public void testPieChart() throws Exception {
    String settings = new StringBuilder()
      .append("{")
      .append("\"tyPe\":\"Pie\", ")
      .append("\"wIdth\":")
      .append(WIDTH)
      .append(",")
      .append("\"height\":")
      .append(HEIGHT)
      .append(",")
      .append("\"TITLE\":\"")
      .append(TITLE)
      .append("\",")
      .append("\"showTitle\":\"")
      .append(SHOW_TITLE)
      .append("\",")
      .append("\"TITLEfontsize\":\"")
      .append(TITLE_FONT_SIZE_SMALL)
      .append("\",")
      .append("\"legendPosition\":\"")
      .append(LEGEND_POSITION_BOTTOM)
      .append("\", ")
      .append("\"legendFontSize\":")
      .append(LEGEND_FONT_SIZE_SMALL)
      .append(", ")
      .append("\"SHOWlegend\":")
      .append(true)
      .append(", ")
      .append("\"yaxismax\":")
      .append(YAXIS_MAX_LABEL_VALUE)
      .append(", ")
      .append("\"showAnnotations\":")
      .append(SHOW_ANNOTATION)
      .append(", ")
      .append("\"annotationsAngle\":")
      .append(ANNOTATION_ANGLE)
      .append(", ")
      .append("\"annotationsFontSIZE\":")
      .append(ANNOTATION_FONT_SIZE)
      .append("}")
      .toString();

    JsonObject jSettings = new JsonParser().parse(settings).getAsJsonObject();

    List<DataSeries> dataSeries = new ArrayList<DataSeries>();

    DataSeries serie1 = new DataSeries();
    serie1.setName("Gold");
    serie1.setValues(new Double[] { 12d });
    serie1.setSettings("{\"coloR\":\"d3a46a\"}");
    dataSeries.add(serie1);

    DataSeries serie2 = new DataSeries();
    serie2.setName("Silver");
    serie2.setValues(new Double[] { 24d });
    serie2.setSettings("{\"coloR\":\"c0c0c0\"}");
    dataSeries.add(serie2);

    DataSeries serie3 = new DataSeries();
    serie3.setName("Platinum");
    serie3.setValues(new Double[] { 36d });
    serie3.setSettings("{\"coloR\":\"e5e1e6\"}");
    dataSeries.add(serie3);

    DataSeries serie4 = new DataSeries();
    serie4.setName("Copper");
    serie4.setValues(new Double[] { 11d });
    serie4.setSettings("{\"coloR\":\"e5e4e2\"}");
    dataSeries.add(serie4);

    DataSeries serie5 = new DataSeries();
    serie5.setName("Zinc");
    serie5.setValues(new Double[] { 17d });
    serie5.setSettings("{\"coloR\":\"bac4c8 \"}");
    dataSeries.add(serie5);

    XChartCreator chartCreator = new XChartCreator();
    Chart chart = chartCreator.create(jSettings, dataSeries, null);

    assertTrue(chart instanceof PieChart);
    assertEquals(WIDTH, chart.getWidth());
    assertEquals(HEIGHT, chart.getHeight());
    assertEquals(TITLE, chart.getTitle());
    assertEquals(SHOW_TITLE, chart.getStyler().isChartTitleVisible());
    assertEquals(TITLE_FONT_SIZE_SMALL, chart.getStyler().getChartTitleFont().getSize());
    assertEquals(LegendPosition.OutsideS, chart.getStyler().getLegendPosition());
    assertEquals(LegendLayout.Horizontal, chart.getStyler().getLegendLayout());
    assertEquals(LEGEND_FONT_SIZE_SMALL, chart.getStyler().getLegendFont().getSize());
    assertEquals(LEGEND_FONT_SIZE_SMALL, chart.getStyler().getLegendSeriesLineLength());
    assertEquals(SHOW_LEGEND, chart.getStyler().isLegendVisible());
    assertEquals(ANNOTATION_FONT_SIZE, chart.getStyler().getAnnotationTextFont().getSize());
    String testChart = "testPieChartwithjson";
    String filePath = "src/test/resources/";
    File targetFile = new File(filePath.concat(testChart));
    if (targetFile.exists()) {
      targetFile.delete();
    }
    ;
    BitmapEncoder.saveBitmapWithDPI(chart, filePath.concat(testChart), BitmapFormat.PNG, 300);
    assertTrue(Files.exists(Paths.get(filePath, testChart + ".png")));
  }

  @Test
  public void testDonutChart() throws Exception {
    String settings = new StringBuilder()
      .append("{")
      .append("\"tyPe\":\"Donut\", ")
      .append("\"wIdth\":")
      .append(WIDTH)
      .append(",")
      .append("\"height\":")
      .append(HEIGHT)
      .append(",")
      .append("\"TITLE\":\"")
      .append(TITLE)
      .append("\",")
      .append("\"showTitle\":\"")
      .append(SHOW_TITLE)
      .append("\",")
      .append("\"TITLEfontsize\":\"")
      .append(TITLE_FONT_SIZE_SMALL)
      .append("\",")
      .append("\"legendPosition\":\"")
      .append(LEGEND_POSITION_BOTTOM)
      .append("\", ")
      .append("\"legendFontSize\":")
      .append(LEGEND_FONT_SIZE_SMALL)
      .append(", ")
      .append("\"SHOWlegend\":")
      .append(true)
      .append(", ")
      .append("\"yaxismax\":")
      .append(YAXIS_MAX_LABEL_VALUE)
      .append(", ")
      .append("\"showAnnotations\":")
      .append(SHOW_ANNOTATION)
      .append(", ")
      .append("\"annotationsAngle\":")
      .append(ANNOTATION_ANGLE)
      .append(", ")
      .append("\"annotationsFontSIZE\":")
      .append(ANNOTATION_FONT_SIZE)
      .append(", ")
      .append("\"donutThickness\":")
      .append(DONUT_THICKNESS)
      .append("}")
      .toString();

    JsonObject jSettings = new JsonParser().parse(settings).getAsJsonObject();

    List<DataSeries> dataSeries = new ArrayList<DataSeries>();

    DataSeries serie1 = new DataSeries();
    serie1.setName("A");
    serie1.setValues(new Double[] { 12d });
    serie1.setSettings("{\"coloR\":\"dfbbb1\"}");
    dataSeries.add(serie1);

    DataSeries serie2 = new DataSeries();
    serie2.setName("B");
    serie2.setValues(new Double[] { 24d });
    serie2.setSettings("{\"coloR\":\"f56476\"}");
    dataSeries.add(serie2);

    DataSeries serie3 = new DataSeries();
    serie3.setName("C");
    serie3.setValues(new Double[] { 36d });
    serie3.setSettings("{\"coloR\":\"e43f6f\"}");
    dataSeries.add(serie3);

    DataSeries serie4 = new DataSeries();
    serie4.setName("D");
    serie4.setValues(new Double[] { 11d });
    serie4.setSettings("{\"coloR\":\"be3e82\"}");
    dataSeries.add(serie4);

    DataSeries serie5 = new DataSeries();
    serie5.setName("E");
    serie5.setValues(new Double[] { 17d });
    serie5.setSettings("{\"coloR\":\"5e4352\"}");
    dataSeries.add(serie5);

    XChartCreator chartCreator = new XChartCreator();
    Chart chart = chartCreator.create(jSettings, dataSeries, null);
    Assertions.assertTrue(chart instanceof PieChart);
    Assertions.assertEquals(500, chart.getWidth());
    Assertions.assertEquals(300, chart.getHeight());
    Assertions.assertEquals("TEST chart", chart.getTitle());
    Assertions.assertEquals(true, chart.getStyler().isChartTitleVisible());
    Assertions.assertEquals(20, chart.getStyler().getChartTitleFont().getSize());
    Assertions.assertEquals(LegendPosition.OutsideS, chart.getStyler().getLegendPosition());
    Assertions.assertEquals(LegendLayout.Horizontal, chart.getStyler().getLegendLayout());
    Assertions.assertEquals(10, chart.getStyler().getLegendFont().getSize());
    Assertions.assertEquals(10, chart.getStyler().getLegendSeriesLineLength());
    Assertions.assertEquals(true, chart.getStyler().isLegendVisible());
    Assertions.assertEquals(12, chart.getStyler().getAnnotationTextFont().getSize());
    PieChart donutChart = (PieChart) chart;
    Assertions.assertEquals(DONUT_THICKNESS, donutChart.getStyler().getDonutThickness());

    String testChart = "testDonutChartwithjson";
    String filePath = "src/test/resources/";
    File targetFile = new File(filePath.concat(testChart));
    if (targetFile.exists()) {
      targetFile.delete();
    }
    ;
    BitmapEncoder.saveBitmapWithDPI(chart, filePath.concat(testChart), BitmapFormat.PNG, 300);

    assertTrue(Files.exists(Paths.get(filePath, testChart + ".png")));
  }

  @Test
  public void testJsonParser() {
    String json = "{\"name\":\"sylvain\", \"list\":[1,2,3]}";
    JsonElement e = new JsonParser().parse(json);
    JsonObject o = e.getAsJsonObject();
    String name = o.get("name").getAsString();
    Assertions.assertEquals("sylvain", name);
  }
}
