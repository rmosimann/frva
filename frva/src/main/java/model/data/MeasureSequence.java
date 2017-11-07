package model.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
  private final Map<SequenceKeyName, double[]> measurements = new HashMap<>();
  private final String sequenceUuid;
  private final DataFile dataFile;
  private ReflectionIndices reflectionIndices;

  public enum SequenceKeyName {
    VEG,
    WR,
    DC_VEG,
    DC_WR,

    RADIANCE_VEG,
    RADIANCE_WR,

    REFLECTANCE;
  }


  /**
   * Constructor for a MeasurementSequence.
   *
   * @param input a StringArray containing the measurements
   * @param dataFile contains the path to the datafiles.
   */
  public MeasureSequence(String input, DataFile dataFile) {
    sequenceUuid = UUID.randomUUID().toString();
    metadata = input.split(";");
    this.dataFile = dataFile;
  }

  public String[] getMetadata() {
    return metadata;
  }


  public Map<SequenceKeyName, double[]> getMeasurements() {
    return measurements;
  }

  /**
   * Prints the content of the MeasureSequence to the console.
   */
  public void print() {
    Arrays.stream(metadata).forEach(a -> System.out.print(a + " "));

    for (Map.Entry<SequenceKeyName, double[]> entry : measurements.entrySet()) {
      System.out.println();
      System.out.print(entry.getKey());
      Arrays.stream(entry.getValue()).forEach(a -> System.out.print(a + " "));
    }
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
        L(VEG) = (DN(VEG) - DC(VEG)) * FLAMEradioVEG_2017-08-03
        L(WR) = (DN(WR) - DC(WR)) * FLAMEradioWR_2017-08-03
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: W/( m²sr nm) which can also be written as W m-2 sr-1 nm-1
     */

    double[] waveCalibration = dataFile.getSdCard().getWavelengthCalibrationFile().getCalibration();
    double[] vegCalibration = dataFile.getSdCard().getSensorCalibrationFileVeg().getCalibration();
    double[] wrCalibration = dataFile.getSdCard().getSensorCalibrationFileWr().getCalibration();

    double[] vegs = measurements.get(SequenceKeyName.VEG);
    double[] dcVegs = measurements.get(SequenceKeyName.DC_VEG);

    double[] wrs = measurements.get(SequenceKeyName.WR);
    double[] dcWrs = measurements.get(SequenceKeyName.DC_WR);

    double[] vegRadiance = new double[waveCalibration.length];
    double[] wrRadiance = new double[waveCalibration.length];

    for (int i = 0; i < waveCalibration.length; i++) {
      vegRadiance[i] = (vegs[i] - dcVegs[i]) * vegCalibration[i];
      wrRadiance[i] = (wrs[i] - dcWrs[i]) * wrCalibration[i];
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
      if (wrRadiance[i] != 0) {
        reflection[i] = vegRadiance[i] / wrRadiance[i];
      } else {
        reflection[0] = 0;
      }
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
    return dataFile.getSdCard().getWavelengthCalibrationFile().getCalibration();
  }

  public String getSequenceUuid() {
    return sequenceUuid;
  }


  public DataFile getDataFile() {
    return dataFile;
  }

  public boolean hasMeasurements() {
    return this.measurements != null;
  }
}