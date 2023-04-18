package com.appiancorp.ps.ss;

import java.util.List;

import org.apache.log4j.Logger;
import org.knowm.xchart.internal.chartpart.Chart;

import com.appiancorp.ps.ss.util.XChartCreator;
import com.appiancorp.ps.ss.util.XChartUtil;
import com.appiancorp.ps.type.ChartThreshold;
import com.appiancorp.ps.type.DataSeries;
import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.DocumentDataType;
import com.appiancorp.suiteapi.knowledge.FolderDataType;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.MessageContainer;
import com.appiancorp.suiteapi.process.framework.Order;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.framework.SmartServiceContext;
import com.appiancorp.suiteapi.process.palette.DocumentGeneration;
import com.appiancorp.suiteapi.type.NamedTypedValue;
import com.appiancorp.suiteapi.type.TypeService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@DocumentGeneration
@Order({
  "ChartSettings", "CreateNewDocument", "NewDocumentName", "NewDocumentDesc", "SaveInFolder", "ExistingDocument"
})
public class CreateXChartSmartService extends AppianSmartService {
  private static final Logger LOG = Logger.getLogger(CreateXChartSmartService.class);

  private TypeService ts;
  private ContentService cs;

  private List<? extends NamedTypedValue> dynamicInputs;
  private String chartSettings;
  private Boolean createNewDocument;
  private String newDocumentName;
  private String newDocumentDesc;
  private Long saveInFolder;
  private Long existingDocument;
  private Long generatedChart;

  private XChartCreator chartCreator;

  public CreateXChartSmartService(SmartServiceContext ctx, TypeService ts, ContentService cs) {
    super();
    this.ts = ts;
    this.cs = cs;
  }

  @Override
  public void run() throws SmartServiceException {
    try {
      JsonObject settingsJObj;
      if (chartSettings != null && chartSettings.trim().length() > 0) {
        JsonElement settingsJElem = new JsonParser().parse(chartSettings);

        if (settingsJElem.isJsonArray()) {
          throw new IllegalArgumentException(
            "Invalid format for the chart settings. This parameter must not be a Json array but a Json object");
        }
        settingsJObj = settingsJElem.getAsJsonObject();
      } else {
        settingsJObj = new JsonObject();
      }

      List<DataSeries> dataSeries = XChartUtil.getDataSeriesFromInputs(ts, dynamicInputs);
      List<ChartThreshold> thresholds = XChartUtil.getThesholdsFromInputs(ts, dynamicInputs);

      // generate the chart
      chartCreator = new XChartCreator();
      Chart chart = chartCreator.create(settingsJObj, dataSeries, thresholds);

      // Write the chart on disk in the Appian document
      if (chart != null) {
        // Create the Appian document for the chart
        Long doc = XChartUtil.createDocument(cs, createNewDocument, newDocumentName, newDocumentDesc, saveInFolder, existingDocument);
        XChartUtil.writeChartImage(cs, chart, doc);
        generatedChart = doc;
      } else {
        generatedChart = -1L;
      }

    } catch (Exception e) {
      LOG.error("Error creating a chart", e);
      throw new SmartServiceException.Builder(CreateXChartSmartService.class, e).build();
    }
  }

  public void validate(MessageContainer messages) {
    if (createNewDocument) {
      if (newDocumentName == null || newDocumentName.isEmpty()) {
        messages.addError("NewDocumentName", "newdocumentname.missing");
      } else if (saveInFolder == null) {
        messages.addError("SaveInFolder", "saveinfolder.missing");
      }
    } else {
      if (existingDocument == null) {
        messages.addError("ExistingDocument", "existingdocument.missing");
      }
    }
  }

  @Override
  public void setDynamicInputs(List<? extends NamedTypedValue> inputs) {
    this.dynamicInputs = inputs;
  }

  @Input(required = Required.ALWAYS)
  @Name("chartSettings")
  public void setChartSettings(String val) {
    this.chartSettings = val;
  }

  @Input(required = Required.ALWAYS)
  @Name("CreateNewDocument")
  public void setCreateNewDocument(Boolean val) {
    this.createNewDocument = val;
  }

  @Input(required = Required.OPTIONAL)
  @Name("NewDocumentName")
  public void setNewDocumentName(String val) {
    this.newDocumentName = val;
  }

  @Input(required = Required.OPTIONAL)
  @Name("NewDocumentDesc")
  public void setNewDocumentDesc(String val) {
    this.newDocumentDesc = val;
  }

  @Input(required = Required.OPTIONAL)
  @Name("SaveInFolder")
  @FolderDataType
  public void setSaveInFolder(Long val) {
    this.saveInFolder = val;
  }

  @Input(required = Required.OPTIONAL)
  @Name("ExistingDocument")
  @DocumentDataType
  public void setExistingDocument(Long val) {
    this.existingDocument = val;
  }

  @Name("GeneratedChart")
  @DocumentDataType
  public Long getGeneratedChart() {
    return generatedChart;
  }
}
