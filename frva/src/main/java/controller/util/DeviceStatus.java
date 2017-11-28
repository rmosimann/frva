package controller.util;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class DeviceStatus {
  double lat;
  double longitude;
  long gpsTime;
  long gpsDate;
  long integrationTime;
  long intervalTime;

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public long getGpsTime() {
    return gpsTime;
  }

  public void setGpsTime(long gpsTime) {
    this.gpsTime = gpsTime;
  }

  public long getGpsDate() {
    return gpsDate;
  }

  public void setGpsDate(long gpsDate) {
    this.gpsDate = gpsDate;
  }

  public void setIntegrationTime(long integrationTime) {
    this.integrationTime = integrationTime;
  }

  public void setIntervalTime(long intervalTime) {
    this.intervalTime = intervalTime;
  }
}
