package com.appiancorp.ps.ss.util;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

import com.appiancorp.ps.type.ChartThreshold;
import com.appiancorp.ps.type.DataSeries;
import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.type.NamedTypedValue;
import com.appiancorp.suiteapi.type.TypeService;

public class XChartUtil {

  public static Long createDocument(ContentService cs, Boolean createNewDocument, String newDocumentName,
    String newDocumentDesc, Long saveInFolder,
    Long existingDocument) throws Exception {
    if (createNewDocument) {
      Document doc = new Document();
      doc.setName(newDocumentName);
      doc.setDescription(newDocumentDesc);
      doc.setExtension("png");
      doc.setParent(saveInFolder);

      return cs.create(doc, ContentConstants.UNIQUE_NONE);
    } else {
      Document doc = (Document) cs.getVersion(existingDocument, ContentConstants.VERSION_CURRENT);
      doc.setFileSystemId(ContentConstants.ALLOCATE_FSID);
      if (newDocumentName != null && !newDocumentName.isEmpty()) {
        doc.setName(newDocumentName);
      }
      if (newDocumentDesc != null && !newDocumentDesc.isEmpty()) {
        doc.setDescription(newDocumentDesc);
      }

      return cs.createVersion(doc, ContentConstants.UNIQUE_NONE).getId()[0];
    }
  }

  public static void writeChartImage(ContentService cs, org.knowm.xchart.internal.chartpart.Chart chart, Long docId) throws Exception {
    String docFileName = cs.getInternalFilename(docId);
    File reportFile = new File(docFileName);
    Files.createDirectories(reportFile.toPath().getParent());
    BitmapEncoder.saveBitmapWithDPI(chart, docFileName, BitmapFormat.PNG, 300);
    cs.setSizeOfDocumentVersion(docId);
  }

  public static List<DataSeries> getDataSeriesFromInputs(TypeService ts, List<? extends NamedTypedValue> dynamicInputs) {
    Long dataSeriesType = ts.getTypeByQualifiedName(new QName("http://types.appiancorp.com/ps", "DataSeries")).getId();

    ArrayList<DataSeries> seriesList = new ArrayList<DataSeries>();
    for (NamedTypedValue ntv : dynamicInputs) {
      if (ntv.getValue() == null) {
        continue;
      }

      if (ntv.getInstanceType().equals(dataSeriesType)) {
        DataSeries series = getDataSeries(ntv.getValue());

        if (series.getValues() == null) {
          continue;
        }
        seriesList.add(series);
      }
    }
    return seriesList;
  }

  private static DataSeries getDataSeries(Object value) {
    Object[] objArr = (Object[]) value;
    DataSeries ds = new DataSeries();
    ds.setName((String) objArr[0]);
    ds.setValues((Double[]) objArr[1]);
    ds.setSettings((String) objArr[2]);
    return ds;
  }

  public static List<ChartThreshold> getThesholdsFromInputs(TypeService ts, List<? extends NamedTypedValue> dynamicInputs) {
    Long thresholdType = ts.getTypeByQualifiedName(new QName("http://types.appiancorp.com/ps", "ChartThreshold")).getId();
    List<ChartThreshold> thresholds = new ArrayList<ChartThreshold>();

    for (NamedTypedValue ntv : dynamicInputs) {
      if (ntv.getValue() == null) {
        continue;
      }

      if (ntv.getInstanceType().equals(thresholdType)) {
        ChartThreshold ct = getChartThreshold(ntv.getValue());

        if (ct.getValue() == null || ct.getValue() == 0) {
          continue;
        }

        thresholds.add(ct);
      }
    }
    return thresholds;
  }

  private static ChartThreshold getChartThreshold(Object value) {
    Object[] objArr = (Object[]) value;
    ChartThreshold ds = new ChartThreshold();
    ds.setName((String) objArr[0]);
    ds.setValue((Double) objArr[1]);
    ds.setSettings((String) objArr[2]);
    return ds;
  }

}
