package model.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private final SdCard sdCard;
  private final Map<String, double[]> measurements = new HashMap<>();

  /**
   * Constructor for a MeasurementSequence.
   *
   * @param input a StringArray containing the measurements
   */
  public MeasureSequence(List<String> input, SdCard sdCard) {
    metadata = input.get(0).split(";");
    this.sdCard = sdCard;

    for (int i = 1; i < input.size(); i++) {
      String[] tmp = input.get(i).split(";");
      measurements.put(tmp[0], Arrays.stream(Arrays.copyOfRange(tmp, 1, tmp.length))
          .mapToDouble(Double::parseDouble)
          .toArray());
    }
  }

  public String[] getMetadata() {
    return metadata;
  }


  public Map<String, double[]> getMeasurements() {
    return measurements;
  }

  /**
   * Prints the content of the MeasureSequence to the console.
   */
  public void print() {
    Arrays.stream(metadata).forEach(a -> System.out.print(a + " "));

    for (Map.Entry<String, double[]> entry : measurements.entrySet()) {
      System.out.println();
      System.out.print(entry.getKey());
      Arrays.stream(entry.getValue()).forEach(a -> System.out.print(a + " "));
    }
  }

  public String getId() {
    return metadata[0];
  }

  public Date getDate() {
    return new Date(Long.parseLong(metadata[1]));
  }

  /**
   * Getter for the Timestamp o the MeasurementSequence.
   *
   * @return the Timestamp as String.
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

  public String getSerial() {
    return metadata[18];
  }


  /**
   * Calculates the Radiance of this MeasurementSequence.
   *
   * @return A Map with the Keys VEG and WR.
   */
  public Map<String, double[]> getRadiance() {
    /*
    Radiance L
      Data:
        L(VEG) = (DN(VEG) - DC(VEG)) * FLAMEradioVEG_2017-08-03
        L(WR) = (DN(WR) - DC(WR)) * FLAMEradioWR_2017-08-03
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: W/( m²sr nm) which can also be written as W m-2 sr-1 nm-1
     */

    double[] waveCalibration = sdCard.getWavelengthCalibrationFile().getCalibration();
    double[] vegCalibration = sdCard.getSensorCalibrationFileVeg().getCalibration();
    double[] wrCalibration = sdCard.getSensorCalibrationFileWr().getCalibration();

    double[] vegs = measurements.get("VEG");
    double[] dcVegs = measurements.get("DC_VEG");

    double[] wrs = measurements.get("WR");
    double[] dcWrs = measurements.get("DC_WR");

    double[] vegRadiance = new double[waveCalibration.length];
    double[] wrRadiance = new double[waveCalibration.length];

    for (int i = 0; i < waveCalibration.length; i++) {
      vegRadiance[i] = (vegs[i] - dcVegs[i]) * vegCalibration[i];
      wrRadiance[i] = (wrs[i] - dcWrs[i]) * wrCalibration[i];
    }

    Map<String, double[]> radianceMap = new HashMap<>();
    radianceMap.put("VEG", vegRadiance);
    radianceMap.put("WR", wrRadiance);

    return radianceMap;
  }


  /**
   * Calculates the Reflectance of this MeasurementSequence.
   *
   * @return A DoubleArray.
   */
  public Map<String, double[]> getReflection() {
    /*
    Reflectance R
      Data:   R(VEG) = L(VEG) / L(WR)
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: ReflectanceFactor (none)
     */
    Map<String, double[]> radianceMap = this.getRadiance();

    double[] vegRadiance = radianceMap.get("VEG");
    double[] wrRadiance = radianceMap.get("WR");

    double[] reflection = new double[vegRadiance.length];

    for (int i = 0; i < reflection.length; i++) {
      reflection[i] = vegRadiance[i] / wrRadiance[i];
    }

    Map<String, double[]> reflectionMap = new HashMap<>();
    reflectionMap.put("Reflection", reflection);

    return reflectionMap;
  }

  public double[] getWavlengthCalibration() {
    return sdCard.getWavelengthCalibrationFile().getCalibration();
  }
}
