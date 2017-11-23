package model.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MeasureSequence {

  /*
  Metadata explained:
    0 Counter
    1 Date? YYMMDD
    2 hhmmss (internal clock)
    3 Mode (auto/manual/app)
    4 Integration time microseconds IT WR
    5 Integration time microsceconds IT VEG
    6 Time for one measurement miliseconds
    More see https://docs.google.com/document/d/1kyKZe7tlKG4Wva3zGr00dLTMva1NG_ins3nsaOIfGDA/edit#
  */
  private final String[] metadata;
  private final String sequenceUuid;
  private final DataFile dataFile;
  private ReflectionIndices reflectionIndices;
  private BooleanProperty deleted;


  public enum SequenceKeyName {
    VEG,
    WR,
    WR2,
    DC_VEG,
    DC_WR,
    RADIANCE_VEG,
    RADIANCE_WR,
    REFLECTANCE
  }


  /**
   * Constructor for an empty MeasurementSequence. Only Metadata is stored
   *
   * @param metadata String containing the metadata
   * @param dataFile contains the path to the datafiles.
   */
  public MeasureSequence(String metadata, DataFile dataFile) {
    sequenceUuid = UUID.randomUUID().toString();
    this.metadata = metadata.split(";");
    this.dataFile = dataFile;
    this.deleted = new SimpleBooleanProperty(false);
  }

  /**
   * Constructor, same as above.
   *
   * @param metadata String containing the metadata.
   * @param dataFile contains the path to the datafiles.
   */
  public MeasureSequence(String[] metadata, DataFile dataFile) {
    sequenceUuid = UUID.randomUUID().toString();
    this.metadata = metadata;
    this.dataFile = dataFile;
    this.deleted = new SimpleBooleanProperty(false);
  }

  /**
   * Reads in the measurement-data from the file system.
   * @return the read in measurements in a map.
   */

  public Map<SequenceKeyName, double[]> getData() {
    Map<SequenceKeyName, double[]> measurements = new HashMap<>();
    String line = "";
    boolean found = false;
    boolean done = false;
    try (BufferedReader br = new BufferedReader(new FileReader(dataFile.getOriginalFile()))) {
      while ((line = br.readLine()) != null && !found) {

        if (line.length() > 1 && Character.isDigit(line.charAt(0)) && (line.split(";")[0]
            .equals(this.getId()))) {
          found = true;


          while ((line = br.readLine()) != null && !done) {
            if (line.length() > 0) {
              if (Character.isDigit(line.charAt(0))) {
                done = true;
              } else {
                String[] temp = line.split(";");
                SequenceKeyName key = SequenceKeyName.valueOf(temp[0].toUpperCase());
                System.out.println(temp[0].toUpperCase());

                measurements.put(key, Arrays.stream(Arrays.copyOfRange(temp, 1, temp.length))
                    .mapToDouble(Double::parseDouble)
                    .toArray());
              }
            }
          }
        }

      }
      //TODO: better skip


    } catch (IOException e) {
      System.out.println(e);
    }
    return measurements.isEmpty() ? null : measurements;
  }


  /**
   * Creates csv-format from a measurementSequenz.
   *
   * @return a string containing the data.
   */
  public String getCsv() {
    StringBuilder sb = new StringBuilder();
    Arrays.stream(metadata).forEach(a -> sb.append(a + ";"));
    for (int i = 0; i < 988; i++) {
      sb.append(";");
    }

    Map<SequenceKeyName, double[]> measurements = getData();

    sb.append("\n\n" + "WR" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.WR)).forEach(a -> sb.append((int) a + ";"));
    sb.deleteCharAt(sb.length() - 1);

    sb.append("\n\n" + "VEG" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.VEG)).forEach(a -> sb.append((int) a + ";"));
    sb.deleteCharAt(sb.length() - 1);


    sb.append("\n\n" + "DC_WR" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.DC_WR)).forEach(a -> sb.append((int) a + ";"));
    sb.deleteCharAt(sb.length() - 1);


    sb.append("\n\n" + "DC_VEG" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.DC_VEG)).forEach(a -> sb.append((int) a + ";"));
    sb.deleteCharAt(sb.length() - 1);

    sb.append("\n\n");
    return sb.toString();
  }

  public String getId() {
    return metadata[0];
  }

  /**
   * Getter for the Time of the MeasurementSequence.
   *
   * @return the Time as String of Type HH:MM.
   */
  public String getTime() {
    String timestamp = metadata[2];

    if (timestamp.length() == 5) {
      return "0" + timestamp.substring(0, 1)
          + ":" + timestamp.substring(1, 3) + ":" + timestamp.substring(3, 5);
    }
    if (timestamp.length() == 6) {
      return timestamp.substring(0, 2)
          + ":" + timestamp.substring(2, 4) + ":" + timestamp.substring(4, 6);
    }
    throw new IllegalArgumentException();
  }

  /**
   * Getter for the Hour (Timestamp) o the MeasurementSequence.
   *
   * @return the Hour as int.
   */
  public int getHour() {
    String timestamp = metadata[2];

    if (timestamp.length() == 5) {
      return Integer.parseInt(timestamp.substring(0, 1));
    }
    if (timestamp.length() == 6) {
      return Integer.parseInt(timestamp.substring(0, 2));
    }
    throw new IllegalArgumentException();
  }

  /**
   * Getter for the Serial of the containing SD-Card.
   *
   * @return Serial as String.
   */

  public String getSerial() {
    return metadata[18];
  }

  /**
   * Getter for the Date of the Sequence.
   *
   * @return Date as String of Type YY-MM-DD.
   */
  public String getDate() {
    return metadata[1].substring(0, 2) + "-" + metadata[1].substring(2, 4) + "-"
        + metadata[1].substring(4, 6);
  }

  /**
   * Calculates the Radiance of this MeasurementSequence.
   *
   * @return A Map with the Keys VEG and WR.
   */
  public Map<SequenceKeyName, double[]> getRadiance() {
    /*
    Radiance L
      Data:
        L(VEG) = ((DN(VEG) - DC(VEG)) * FLAMEradioVEG_2017-08-03) IntegrationTimeVEG
        L(WR) = ((DN(WR) - DC(WR)) * FLAMEradioWR_2017-08-03) IntegrationTimeWR
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: W/( m²sr nm) which can also be written as W m-2 sr-1 nm-1
     */

    double[] waveCalibration = dataFile.getSdCard().getCalibrationFile().getWlF1();
    double[] vegCalibration = dataFile.getSdCard().getCalibrationFile().getDwCoefF1();
    double[] wrCalibration = dataFile.getSdCard().getCalibrationFile().getUpCoefF1();

    Map<SequenceKeyName, double[]> measurements = getData();

    double[] vegs = measurements.get(SequenceKeyName.VEG);
    double[] dcVegs = measurements.get(SequenceKeyName.DC_VEG);

    double[] wrs = measurements.get(SequenceKeyName.WR);
    double[] dcWrs = measurements.get(SequenceKeyName.DC_WR);

    double[] vegRadiance = new double[waveCalibration.length];
    double[] wrRadiance = new double[waveCalibration.length];

    for (int i = 0; i < waveCalibration.length; i++) {
      wrRadiance[i] = (
          (wrs[i] - dcWrs[i]) * wrCalibration[i]) / Double.parseDouble(metadata[5]);
      vegRadiance[i] = (
          (vegs[i] - dcVegs[i]) * vegCalibration[i]) / Double.parseDouble(metadata[7]);
    }

    Map<SequenceKeyName, double[]> radianceMap = new HashMap<>();
    radianceMap.put(SequenceKeyName.RADIANCE_VEG, vegRadiance);
    radianceMap.put(SequenceKeyName.RADIANCE_WR, wrRadiance);

    return radianceMap;
  }


  /**
   * Calculates the Reflectance of this MeasurementSequence.
   *
   * @return A DoubleArray.
   */
  public Map<SequenceKeyName, double[]> getReflectance() {
    /*
    Reflectance R
      Data:   R(VEG) = L(VEG) / L(WR)
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: ReflectanceFactor (none)
     */
    Map<SequenceKeyName, double[]> radianceMap = this.getRadiance();

    double[] vegRadiance = radianceMap.get(SequenceKeyName.RADIANCE_VEG);
    double[] wrRadiance = radianceMap.get(SequenceKeyName.RADIANCE_WR);

    double[] reflection = new double[vegRadiance.length];

    for (int i = 0; i < reflection.length; i++) {
      reflection[i] = vegRadiance[i] / wrRadiance[i];
    }

    Map<SequenceKeyName, double[]> reflectionMap = new HashMap<>();
    reflectionMap.put(SequenceKeyName.REFLECTANCE, reflection);

    reflectionIndices = new ReflectionIndices(reflection, getWavlengthCalibration());

    return reflectionMap;

  }

  /**
   * Calculates the indices.
   * Based on the reflectance factors R:
   * TCARI : 3 × ((R700 – R760) – 0.2 × (R700 – R550) × (R700/R670))
   * PRI: (R531 -R570 )/(R531 +R570 )
   * NDVI: (R920 - R696) / (R920 + R696)
   *
   * @return ReflectionIndices.
   */
  public ReflectionIndices getIndices() {
    if (reflectionIndices == null) {
      Map<SequenceKeyName, double[]> reflectance = getReflectance();

      reflectionIndices = new ReflectionIndices(reflectance.get(SequenceKeyName.REFLECTANCE),
          getWavlengthCalibration());

    }
    return reflectionIndices;
  }


  public double[] getWavlengthCalibration() {
    return dataFile.getSdCard().getCalibrationFile().getWlF1();
  }

  public String getSequenceUuid() {
    return sequenceUuid;
  }


  public DataFile getDataFile() {
    return dataFile;
  }

  /**
   * Returns Year created as a String.
   *
   * @return year created.
   */
  public String getYear() {
    return "20" + this.getDate().substring(0, 2);
  }

  /**
   * Getter for Month of measurement.
   *
   * @return month as String.
   */
  public String getMonth() {
    switch (this.getDate().substring(3, 5)) {
      case "01":
        return "JAN";
      case "02":
        return "FEB";
      case "03":
        return "MAR";
      case "04":
        return "APR";
      case "05":
        return "MAY";
      case "06":
        return "JUN";
      case "07":
        return "JUL";
      case "08":
        return "AUG";
      case "09":
        return "SEP";
      case "10":
        return "OCT";
      case "11":
        return "NOV";
      case "12":
        return "DEC";
      default:
        return "ERROR";
    }
  }


  /**
   * Getter for the metadata.
   *
   * @return metadaa as String.
   */
  public String getMetadataAsString() {
    StringBuilder sb = new StringBuilder();
    for (String s : metadata) {
      sb.append(s);
      sb.append(";");
    }
    return sb.toString();
  }

  public SdCard getContainingSdCard() {
    return this.dataFile.getSdCard();
  }

  public void setDeleted(boolean deleted) {
    this.deleted.set(deleted);
  }

  public boolean isDeleted() {
    return deleted.get();
  }

  public BooleanProperty deletedProperty() {
    return deleted;
  }
}