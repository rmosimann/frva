package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CalibrationFile {
  private final File originalFile;
  private Vector<Double> wl_F1;
  private Vector<Double> wl_F2;
  private Vector<Double> up_coef_F1;
  private Vector<Double> up_coef_F2;
  private Vector<Double> dw_coef_F1;
  private Vector<Double> dw_coef_F2;
  private List<String> metadata;

  /**
   * Constructor.
   *
   * @param input     Array of strings containing the calibration.
   * @param skipLines Amount of lines to skip in the beginning.
   */
  public CalibrationFile(File input, int skipLines) {
    this.originalFile = input;
    this.wl_F1 = new Vector<>();
    this.wl_F2 = new Vector<>();
    this.up_coef_F1 = new Vector<>();
    this.up_coef_F2 = new Vector<>();
    this.dw_coef_F1 = new Vector<>();
    this.dw_coef_F2 = new Vector<>();
    this.metadata = new ArrayList<>();

    List<String[]> fileContent = new ArrayList<>();
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(input));) {
      br.readLine();
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          fileContent.add(line.split(";"));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (String[] splitLine : fileContent
        ) {
      wl_F1.add(Double.parseDouble(splitLine[0]));
      up_coef_F1.add(Double.parseDouble(splitLine[1]));
      dw_coef_F1.add(Double.parseDouble(splitLine[2]));
      wl_F2.add(Double.parseDouble(splitLine[3]));
      up_coef_F2.add(Double.parseDouble(splitLine[4]));
      dw_coef_F2.add(Double.parseDouble(splitLine[5]));
      if (splitLine.length>6) {
        metadata.add(splitLine[6]);
      }
    }
  }


  public File getCalibrationFile() {
    return this.originalFile;
  }

  public double[] getWl_F1() {
    return getAsArray(wl_F1);
  }

  public double[] getWl_F2() {
    return getAsArray(wl_F2);
  }

  public double[] getUp_coef_F1() {
    return getAsArray(up_coef_F1);
  }

  public double[] getUp_coef_F2() {
    return getAsArray(up_coef_F2);
  }

  public double[] getDw_coef_F1() {
    return getAsArray(dw_coef_F1);
  }

  public double[] getDw_coef_F2() {
    return getAsArray(dw_coef_F2);
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
    return o instanceof CalibrationFile && ((CalibrationFile) o).wl_F1.equals(this.wl_F1);
  }

}
