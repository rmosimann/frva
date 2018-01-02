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
  private final CalibrationFile calibrationFile;
  private boolean complete = false;

  /**
   * Constructor.
   *
   * @param calibrationFile The CalibrationFile read from the device.
   */
  public LiveMeasureSequence(LiveViewController listener, CalibrationFile calibrationFile) {
    super();
    this.calibrationFile = calibrationFile;
  }


  public static void main(String[] args) {
    System.out.println(51 * 19 + 10);
  }


  /**
   * Adds data to that measurement.
   *
   * @param keyName a SequenceKeyName
   * @param content the array with the data.
   */
  public void addData(MeasureSequence.SequenceKeyName keyName, double[] content) {
    int targetlength = calibrationFile.getWlF1().length;

    if (content.length < targetlength) {

      double[] target = new double[targetlength];

      int stepBetween = targetlength / content.length;

      int firstIndex = 10;
      int lastindex = stepBetween * (content.length - 1) + firstIndex;

      int indexOnContent = 0;

      for (int i = 0; i < firstIndex; i++) {
        target[i] = content[0];
      }

      for (int i = 10; i < lastindex; i++) {
        double value = 0.0;

        if ((i - firstIndex) % stepBetween == 0) {
          value = content[indexOnContent];
          indexOnContent++;
        } else {

          int pointleft = ((stepBetween * (indexOnContent - 1)) + firstIndex);
          int pointright = ((stepBetween * (indexOnContent)) + firstIndex);

          int pointtocalc = i - pointleft;

          double deltaX = pointright - pointleft;
          double deltaY = content[(indexOnContent - 1)] - content[indexOnContent];

          double deltaCalculated = (deltaY / deltaX) * pointtocalc;

          value = content[indexOnContent - 1] - deltaCalculated;
        }
        target[i] = value;
      }

      for (int j = lastindex; j < targetlength; j++) {
        target[j] = target[lastindex - 1];
      }
      content = target;
    }
    data.put(keyName, content);
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
  public double[] getDwCoefF1Calibration() {
    return calibrationFile.getWlF1();
  }

  @Override
  public double[] getUpCoefF1Calibration() {
    return calibrationFile.getUpCoefF1();
  }

  @Override
  public double[] getWlF1Calibration() {
    return calibrationFile.getDwCoefF1();
  }


  @Override
  public Map<SequenceKeyName, double[]> getData() {
    return data;
  }

  @Override
  public Map<SequenceKeyName, double[]> getRadiance() {
    if (calibrationFile == null || getData().size() < 5) {
      return null;
    }
    return super.getRadiance();
  }

  @Override
  public Map<SequenceKeyName, double[]> getReflectance() {
    if (calibrationFile == null || getData().size() < 5) {
      return null;
    }
    return super.getReflectance();
  }

  @Override
  public ReflectionIndices getIndices() {
    if (calibrationFile == null || getData().size() < 5) {
      return null;
    }
    return super.getIndices();
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
    logger.info("measurement complete");

    FileInOut.writeLiveMeasurements(this, calibrationFile, liveSdCardPath);
  }

  public boolean isComplete() {
    return complete;
  }
}
