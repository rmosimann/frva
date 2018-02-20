/*
 *     This file is part of FRVA
 *     Copyright (C) 2018 Andreas Hüni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package controller.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.data.CalibrationFile;

/**
 * The DeviceStatus maintains the information about the actual connected device.
 */
public class DeviceStatus {

  private final StringProperty systemname = new SimpleStringProperty();
  private final StringProperty gpsInformation = new SimpleStringProperty();
  private final StringProperty fldPixels = new SimpleStringProperty();
  private final DoubleProperty lat = new SimpleDoubleProperty();
  private final DoubleProperty longitude = new SimpleDoubleProperty();
  private final LongProperty gpsTime = new SimpleLongProperty();
  private final LongProperty gpsDate = new SimpleLongProperty();
  private final LongProperty integrationTimeWr = new SimpleLongProperty();
  private final LongProperty integrationTimeVeg = new SimpleLongProperty();
  private final LongProperty integrationTimeConfigured = new SimpleLongProperty();
  private final LongProperty maxIntegrationTime = new SimpleLongProperty();
  private final LongProperty intervalTime = new SimpleLongProperty();
  private final LongProperty serialResolution = new SimpleLongProperty();
  private final LongProperty ledOnEachCycle = new SimpleLongProperty();
  private final LongProperty ledPower = new SimpleLongProperty();
  private final LongProperty qeAverages = new SimpleLongProperty();
  private final LongProperty flameAverages = new SimpleLongProperty();
  private final BooleanProperty serialStream = new SimpleBooleanProperty();
  private final BooleanProperty serialDataTransfer = new SimpleBooleanProperty();
  private CalibrationFile calibrationFile;


  private void redoGpsInformationString() {
    //Lat xx °N - Lon xx °E (time - Date)
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(lat.getValue() + "°N - ");
    stringBuilder.append(longitude.getValue() + "°E \n");
    stringBuilder.append("(" + gpsTime.getValue() + " - " + gpsDate.getValue() + ")");

    gpsInformation.setValue(stringBuilder.toString());

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

  public long getIntegrationTimeConfigured() {
    return integrationTimeConfigured.get();
  }

  public LongProperty integrationTimeConfiguredProperty() {
    return integrationTimeConfigured;
  }

  public void setIntegrationTimeConfigured(long integrationTimeConfigured) {
    this.integrationTimeConfigured.set(integrationTimeConfigured);
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


  public void setCalibrationFile(CalibrationFile calibrationFile) {

    this.calibrationFile = calibrationFile;
  }

  public CalibrationFile getCalibrationFile() {
    return calibrationFile;
  }


  public long getIntegrationTimeWr() {
    return integrationTimeWr.get();
  }

  public LongProperty integrationTimeWrProperty() {
    return integrationTimeWr;
  }

  public void setIntegrationTimeWr(long integrationTimeWr) {
    this.integrationTimeWr.set(integrationTimeWr);
  }

  public long getIntegrationTimeVeg() {
    return integrationTimeVeg.get();
  }

  public LongProperty integrationTimeVegProperty() {
    return integrationTimeVeg;
  }

  public void setIntegrationTimeVeg(long integrationTimeVeg) {
    this.integrationTimeVeg.set(integrationTimeVeg);
  }

  /**
   * Clears all fields of the object.
   */
  public void clear() {
    systemname.set("");
    gpsInformation.set("");
    fldPixels.set("");
    lat.set(0.0);
    longitude.set(0.0);
    gpsTime.set(0);
    gpsDate.set(0);
    integrationTimeConfigured.set(0);
    integrationTimeVeg.set(0);
    integrationTimeWr.set(0);
    maxIntegrationTime.set(0);
    intervalTime.set(0);
    serialResolution.set(0);
    ledOnEachCycle.set(0);
    ledPower.set(0);
    qeAverages.set(0);
    flameAverages.set(0);

    serialStream.set(false);
    serialDataTransfer.set(false);

    calibrationFile = null;

  }
}
