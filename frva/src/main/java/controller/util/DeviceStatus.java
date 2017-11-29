package controller.util;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class DeviceStatus {

  StringProperty systemname = new SimpleStringProperty();
  StringProperty gpsInformation = new SimpleStringProperty();
  DoubleProperty lat = new SimpleDoubleProperty();
  DoubleProperty longitude = new SimpleDoubleProperty();
  LongProperty gpsTime = new SimpleLongProperty();
  LongProperty gpsDate = new SimpleLongProperty();
  LongProperty integrationTime = new SimpleLongProperty();
  LongProperty maxIntegrationTime = new SimpleLongProperty();
  LongProperty intervalTime = new SimpleLongProperty();

  private void redoGpsInformationString() {
    //Lat xx °N - Lon xx °E (time - Date)
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(lat.getValue() + "°N - ");
    stringBuilder.append(longitude.getValue() + "°E \n");
    stringBuilder.append("(" + gpsTime.getValue() + " - " + gpsDate.getValue() + ")");

    Platform.runLater(() -> {
      gpsInformation.setValue(stringBuilder.toString());
    });

  }


  public String getSystemname() {
    return systemname.get();
  }

  public StringProperty systemnameProperty() {
    return systemname;
  }

  public void setSystemname(String systemname) {
    this.systemname.set(systemname);
  }

  public double getLat() {
    return lat.get();
  }

  public DoubleProperty latProperty() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat.set(lat);
    redoGpsInformationString();
  }

  public double getLongitude() {
    return longitude.get();
  }

  public DoubleProperty longitudeProperty() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude.set(longitude);
    redoGpsInformationString();
  }

  public long getGpsTime() {
    return gpsTime.get();
  }

  public LongProperty gpsTimeProperty() {
    return gpsTime;
  }

  public void setGpsTime(long gpsTime) {
    this.gpsTime.set(gpsTime);
    redoGpsInformationString();
  }

  public long getGpsDate() {
    return gpsDate.get();
  }

  public LongProperty gpsDateProperty() {
    return gpsDate;
  }

  public void setGpsDate(long gpsDate) {
    this.gpsDate.set(gpsDate);
    redoGpsInformationString();
  }

  public long getIntegrationTime() {
    return integrationTime.get();
  }

  public LongProperty integrationTimeProperty() {
    return integrationTime;
  }

  public void setIntegrationTime(long integrationTime) {
    this.integrationTime.set(integrationTime);
  }

  public long getMaxIntegrationTime() {
    return maxIntegrationTime.get();
  }

  public LongProperty maxIntegrationTimeProperty() {
    return maxIntegrationTime;
  }

  public void setMaxIntegrationTime(long maxIntegrationTime) {
    this.maxIntegrationTime.set(maxIntegrationTime);
  }

  public long getIntervalTime() {
    return intervalTime.get();
  }

  public LongProperty intervalTimeProperty() {
    return intervalTime;
  }

  public void setIntervalTime(long intervalTime) {
    this.intervalTime.set(intervalTime);
  }

  public String getGpsInformation() {
    return gpsInformation.get();
  }

  public StringProperty gpsInformationProperty() {
    return gpsInformation;
  }

  public void setGpsInformation(String gpsInformation) {
    this.gpsInformation.set(gpsInformation);
  }
}
