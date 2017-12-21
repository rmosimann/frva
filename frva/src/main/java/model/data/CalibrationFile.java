package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CalibrationFile {
  private File originalFile;
  private List<String> rawData;
  private String header;
  private Vector<Double> wlF1;
  private Vector<Double> wlF2;
  private Vector<Double> upCoefF1;
  private Vector<Double> upCoefF2;
  private Vector<Double> dwCoefF1;
  private Vector<Double> dwCoefF2;
  private List<String> metadata;

  /**
   * Constructor.
   *
   * @param input Array of strings containing the calibration.
   */
  public CalibrationFile(File input) {

    this.originalFile = input;

    List<String> fileContent = new ArrayList<>();
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(input))) {
      header=br.readLine();
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          fileContent.add(line);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    //System.out.println("orig");
    for (String str : fileContent) {
      //System.out.println(str);
    }
    initialize(fileContent);
  }

  /**
   * Creates a Calibration File for LiveView.
   *
   * @param data Splitted lines of Calibration
   */
  public CalibrationFile(List<String> data) {
    header = data.remove(0);
    initialize(data);
  }

  /**
   * Initializes the Calibration file, Parses Calib file.
   *
   * @param data Calib file as a List of lines.
   */

  public void initialize(List<String> data) {

    rawData = data;

    List<String[]> temp = new ArrayList<>();
    for (String str : data) {
      temp.add(str.split(";"));
    }

    this.wlF1 = new Vector<>();
    this.wlF2 = new Vector<>();
    this.upCoefF1 = new Vector<>();
    this.upCoefF2 = new Vector<>();
    this.dwCoefF1 = new Vector<>();
    this.dwCoefF2 = new Vector<>();
    this.metadata = new ArrayList<>();

    for (String[] splitLine : temp) {
      wlF1.add(Double.parseDouble(splitLine[0]));

      upCoefF1.add(Double.parseDouble(splitLine[1]));

      dwCoefF1.add(Double.parseDouble(splitLine[2]));

      wlF2.add(Double.parseDouble(splitLine[3]));

      upCoefF2.add(Double.parseDouble(splitLine[4]));

      dwCoefF2.add(Double.parseDouble(splitLine[5]));
      if (splitLine.length > 6) {
        metadata.add(splitLine[6]);
      }
    }
    calibrationAsString();
  }

  /**
   * Rebuilds Calibration File and returns it as a String.
   *
   * @return Calibration file as String.
   */
  public String calibrationAsString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(header);
    for (String str : rawData) {
      stringBuilder.append(str + "\n");
    }
    // System.out.println(stringBuilder.toString());
    return stringBuilder.toString();
  }


  public File getCalibrationFile() {
    return this.originalFile;
  }

  public double[] getWlF1() {
    return getAsArray(wlF1);
  }

  public double[] getWlF2() {
    return getAsArray(wlF2);
  }

  public double[] getUpCoefF1() {
    return getAsArray(upCoefF1);
  }

  public double[] getUpCoefF2() {
    return getAsArray(upCoefF2);
  }

  public double[] getDwCoefF1() {
    return getAsArray(dwCoefF1);
  }

  public double[] getDwCoefF2() {
    return getAsArray(dwCoefF2);
  }

  public List<String> getMetadata() {
    return metadata;
  }

  private double[] getAsArray(List<Double> original) {
    return original.stream().mapToDouble(Double::doubleValue).toArray();
  }

  /**
   * Equals for the calibraion files, compares deeply.
   *
   * @param o Object to compare.
   * @return true when equal.
   */
  @Override
  public boolean equals(Object o) {
    return o instanceof CalibrationFile && ((CalibrationFile) o).wlF1.equals(this.wlF1);
  }

}
