package com.appiancorp.ps.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Arrays;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataSeries", namespace = "http://types.appiancorp.com/ps", propOrder = {
  "name",
  "values",
  "settings"
})
public class DataSeries {
  @XmlElement(required = true, nillable = true)
  protected String name;
  @XmlElement(required = true, nillable = true)
  protected Double[] values;
  @XmlElement(required = false, nillable = true)
  protected String settings;

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public Double[] getValues() {
    return values;
  }

  public double[] getPrimitiveValues() {
    double[] primitives = null;
    if (values != null) {
      primitives = new double[values.length];
      for (int i = 0; i < values.length; i++) {
        primitives[i] = values[i].doubleValue();
      }
    }
    return primitives;
  }

  public Number getPieValue() {
    Number pieValue = Arrays.stream(getValues()).findFirst().get().doubleValue();
    return pieValue;
  }

  public void setValues(Double[] values) {
    this.values = values;
  }

  public String getSettings() {
    return settings;
  }

  public void setSettings(String settings) {
    this.settings = settings;
  }
}
