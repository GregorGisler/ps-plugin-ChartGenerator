package com.appiancorp.ps.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChartThreshold", namespace = "http://types.appiancorp.com/ps", propOrder = {
  "name",
  "value",
  "settings"
})
public class ChartThreshold {
  @XmlElement(required = true, nillable = true)
  protected String name;
  @XmlElement(required = true, nillable = true)
  protected Double value;
  @XmlElement(required = false, nillable = true)
  protected String settings;

  public String getName() {
    return name;
  }

  public Double getValue() {
    return value;
  }

  public void setName(String value) {
    this.name = value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public String getSettings() {
    return settings;
  }

  public void setSettings(String settings) {
    this.settings = settings;
  }

}
