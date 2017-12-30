package model.data;

import controller.LiveViewController;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class LiveMeasureSequence extends MeasureSequence {
  private static final Logger logger = Logger.getLogger("FRVA");


  private final Map<MeasureSequence.SequenceKeyName, double[]> data = new HashMap<>();
  private LiveViewController listener;
  private boolean complete = false;

  public LiveMeasureSequence(LiveViewController listener) {
    super();
    this.listener = listener;
  }

  /**
   * Adds data to that measurement.
   *
   * @param keyName a SequenceKeyName
   * @param content the array with the data.
   */
  public void addData(MeasureSequence.SequenceKeyName keyName, double[] content) {
    data.put(keyName, content);
    updated();
  }

  private void updated() {
    if (listener != null) {
      listener.redrawGraph(this);
    }
  }

  @Override
  public String toString() {
    if (getMetadata() != null) {
      return getId();
    }
    return "Measuring...";
  }

  /**
   * Creates csv-format from a measurementSequenz.
   *
   * @return a string containing the data.
   */
  @Override
  public String getCsv() {
    StringBuilder sb = new StringBuilder();
    Arrays.stream(getMetadata()).forEach(a -> sb.append(a + ";"));


    Map<SequenceKeyName, double[]> measurements = data;
    sb.deleteCharAt(sb.length() - 1);
    sb.append("WR" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.WR)).forEach(a -> sb.append((int) a + ";"));
    sb.append(";");

    sb.append("\n" + "VEG" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.VEG)).forEach(a -> sb.append((int) a + ";"));
    sb.append(";");

    sb.append("\n" + "WR2" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.WR2)).forEach(a -> sb.append((int) a + ";"));
    sb.append(";");

    sb.append("\n" + "DC_WR" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.DC_WR)).forEach(a -> sb.append((int) a + ";"));
    sb.append(";");

    sb.append("\n" + "DC_VEG" + ";");
    Arrays.stream(measurements.get(SequenceKeyName.DC_VEG)).forEach(a -> sb.append((int) a + ";"));
    //sb.deleteCharAt(sb.length() - 1);
    sb.append(";");

    sb.append("\n");

    return sb.toString().replaceAll(" ", "").toString();

  }

  @Override
  public Map<SequenceKeyName, double[]> getData() {
    return data;
  }

  @Override
  public Map<SequenceKeyName, double[]> getRadiance() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public Map<SequenceKeyName, double[]> getReflectance() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public ReflectionIndices getIndices() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public double[] getWavlengthCalibration() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public DataFile getDataFile() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public SdCard getContainingSdCard() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }


  /**
   * Sets the LiveMeasureSequcence to complete and writes Data to File.
   *
   * @param complete        if true.
   * @param calibrationFile The calibration file which belongs to this measurement.
   * @param liveSdCardPath  The path where the Measurement should be written.
   */
  public void setComplete(boolean complete, CalibrationFile calibrationFile, File liveSdCardPath) {
    this.complete = complete;
    listener.refreshList();
    listener = null;
    logger.info("measurement complete");
    FileInOut.writeLiveMeasurements(this, calibrationFile, liveSdCardPath);

  }

  public boolean isComplete() {
    return complete;
  }
}
