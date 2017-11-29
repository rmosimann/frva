package controller.util;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
  StringProperty fldPixels = new SimpleStringProperty();
  DoubleProperty lat = new SimpleDoubleProperty();
  DoubleProperty longitude = new SimpleDoubleProperty();
  LongProperty gpsTime = new SimpleLongProperty();
  LongProperty gpsDate = new SimpleLongProperty();
  LongProperty integrationTime = new SimpleLongProperty();
  LongProperty maxIntegrationTime = new SimpleLongProperty();
  LongProperty intervalTime = new SimpleLongProperty();
  LongProperty serialResolution = new SimpleLongProperty();
  LongProperty ledOnEachCycle = new SimpleLongProperty();
  LongProperty ledPower = new SimpleLongProperty();
  LongProperty qeAverages = new SimpleLongProperty();
  LongProperty flameAverages = new SimpleLongProperty();
  BooleanProperty serialStream = new SimpleBooleanProperty();
  BooleanProperty serialDataTransfer = new SimpleBooleanProperty();


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


  public long getSerialResolution() {
    return serialResolution.get();
  }

  public LongProperty serialResolutionProperty() {
    return serialResolution;
  }

  public void setSerialResolution(long serialResolution) {
    this.serialResolution.set(serialResolution);
  }

  public long getLedOnEachCycle() {
    return ledOnEachCycle.get();
  }

  public LongProperty ledOnEachCycleProperty() {
    return ledOnEachCycle;
  }

  public void setLedOnEachCycle(long ledOnEachCycle) {
    this.ledOnEachCycle.set(ledOnEachCycle);
  }

  public long getLedPower() {
    return ledPower.get();
  }

  public LongProperty ledPowerProperty() {
    return ledPower;
  }

  public void setLedPower(long ledPower) {
    this.ledPower.set(ledPower);
  }

  public long getQeAverages() {
    return qeAverages.get();
  }

  public LongProperty qeAveragesProperty() {
    return qeAverages;
  }

  public void setQeAverages(long qeAverages) {
    this.qeAverages.set(qeAverages);
  }

  public long getFlameAverages() {
    return flameAverages.get();
  }

  public LongProperty flameAveragesProperty() {
    return flameAverages;
  }

  public void setFlameAverages(long flameAverages) {
    this.flameAverages.set(flameAverages);
  }

  public boolean isSerialStream() {
    return serialStream.get();
  }

  public BooleanProperty serialStreamProperty() {
    return serialStream;
  }

  public void setSerialStream(boolean serialStream) {
    this.serialStream.set(serialStream);
  }

  public boolean isSerialDataTransfer() {
    return serialDataTransfer.get();
  }

  public BooleanProperty serialDataTransferProperty() {
    return serialDataTransfer;
  }

  public void setSerialDataTransfer(boolean serialDataTransfer) {
    this.serialDataTransfer.set(serialDataTransfer);
  }

  public String getFldPixels() {
    return fldPixels.get();
  }

  public StringProperty fldPixelsProperty() {
    return fldPixels;
  }

  public void setFldPixels(String fldPixels) {
    this.fldPixels.set(fldPixels);
  }
}
