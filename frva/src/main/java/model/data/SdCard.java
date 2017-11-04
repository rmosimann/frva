package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.FrvaModel;

public class SdCard {
  private final List<MeasureSequence> measureSequences;
  private File sdCardPath;
  private CalibrationFile wavelengthCalibrationFile;
  private CalibrationFile sensorCalibrationFileWr;
  private CalibrationFile sensorCalibrationFileVeg;
  private String name;
  private FrvaModel model;

  /**
   * Constructor.
   *
   * @param sdCardPath a Path where the data lays as expected.
   */
  public SdCard(File sdCardPath, String name, FrvaModel model) {
    this.sdCardPath = sdCardPath;
    this.model = model;
    this.measureSequences = new ArrayList<>();
    System.out.println("hello1 " + sdCardPath.getAbsolutePath());
    wavelengthCalibrationFile = readCalibrationFile(sdCardPath, "wl_", 1);
    sensorCalibrationFileWr = readCalibrationFile(sdCardPath, "radioWR_", 0);
    sensorCalibrationFileVeg = readCalibrationFile(sdCardPath, "radioVEG_", 0);
    if (name == null) {
      this.name = sdCardPath.getName();
    } else {
      this.name = name;
    }
  }


  private CalibrationFile readCalibrationFile(File sdCardPath, String filter, int skipLines) {
    File folder = sdCardPath;
    File[] listOfFiles = folder.listFiles((dir, name) -> name.contains(filter)
        && name.endsWith(".csv"));
    System.out.println("try to read in calib-file");
    for (File f : listOfFiles
        ) {
      System.out.println(f.getAbsolutePath());

    }
    return new CalibrationFile(listOfFiles[0], skipLines);
  }


  public CalibrationFile getWavelengthCalibrationFile() {
    return wavelengthCalibrationFile;
  }

  public CalibrationFile getSensorCalibrationFileWr() {
    return sensorCalibrationFileWr;
  }

  public CalibrationFile getSensorCalibrationFileVeg() {
    return sensorCalibrationFileVeg;
  }


  /**
   * Getter for the devices Serial-Number.
   *
   * @return SerialNumber as String.
   */
  public String getDeviceSerialNr() {
    //TODO: needs to be implemented
    return "D-FloX V0.7 JB-006-BM";
    /*
    if (this.measureSequences == null || this.measureSequences.isEmpty()) {
      throw new IllegalArgumentException();
    }
    return this.measureSequences.stream().findAny().get().getSerial();*/
  }

  public String getName() {
    return this.name;
  }

  /**
   * Getter to read all MeasurementSequences in this SDCARD.
   *
   * @return List of MeasurementSequences.
   */
  public List<MeasureSequence> getMeasureSequences() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation Dialog");
    alert.setHeaderText("you are going to read in ** measurement, this might take a while");
    alert.setContentText("Are you ok with this?");

    Optional<ButtonType> result = alert.showAndWait();
    if (ButtonType.OK != result.get()) {
      System.out.println("ok start now");
      return readInFiles();
    } else {
      return measureSequences;
    }

  }

  /**
   * Checks if SDCARD is empty, empty DataFiles are removed before.
   *
   * @return true when empty.
   */
  public boolean isEmpty() {
    return this.measureSequences.isEmpty();


  }

  public File getPath() {
    return this.sdCardPath;
  }


  public MeasureSequence readSingleMeasurementSequence(File containingFile, String id, FrvaModel model) {
    boolean found = false;
    ArrayList<String> fileContent = new ArrayList<>();
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(containingFile))) {
      while ((line = br.readLine()) != null) {

        System.out.println(line);
        if (line.length() > 1 && Character.isDigit(line.charAt(0))) {
          if (line.split(";")[0].equals(id)) {
            found = true;
            fileContent.add(line);
            br.readLine();
            //Read Measurement Sequence
            for (int i = 0; i < 3; i++) {
              fileContent.add(br.readLine());
              br.readLine();
            }
            for (String str : fileContent
                ) {
              System.out.println("in SDCARD readSinlgemeasureSeq");
              System.out.println(str != null ? str.substring(0, 10) : "null");
            }
            MeasureSequence ms = new MeasureSequence(this, containingFile, model, fileContent);
            this.measureSequences.add(ms);
            return ms;
          }
          //skip 9 lines
          for (int i = 0; i < 9; i++) {
            br.readLine();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    throw new NoSuchElementException("Element with ID " + id + " has not been found in file " + containingFile);

  }


  public List<MeasureSequence> readInFiles() {
    System.out.println("started");
    for (File f : sdCardPath.listFiles()) {
      // System.out.println("started1");

      if (f.isDirectory() && f.listFiles().length != 0) {
        // System.out.println("started2");

        for (File datafile : f.listFiles()) {

          //   System.out.println("started3");

          List<String> fileContent = new ArrayList<>();
          String line = "";
          try (BufferedReader br = new BufferedReader(new FileReader(datafile));) {
            while ((line = br.readLine()) != null) {
              //  System.out.println("started4");
              //System.out.println(line.length() > 10 ? line.substring(0, 10) : "empty Line");
              if (line.length() > 0 && Character.isDigit(line.charAt(0))) {
                fileContent.add(0, line);
                br.readLine();
                //Read Measurement Sequence
                for (int i = 1; i < 4; i++) {
                  fileContent.add(i, br.readLine());
                  br.readLine();
                }
              }

              //TODO: probably unneeded measureSequences.add(new MeasureSequence(this, datafile, model, fileContent));
              for (String str : fileContent) {
                System.out.println(str.substring(0, 20));
              }

              if (fileContent.size() == 4) {
                measureSequences.add(new MeasureSequence(this, datafile, model, fileContent));
              }
              fileContent.clear();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    System.out.println("added now " + measureSequences.size() + " Measuresequences");
    return measureSequences;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof SdCard
        && ((SdCard) object).getWavelengthCalibrationFile().equals(wavelengthCalibrationFile));

  }

  @Override
  public int hashCode() {
    return wavelengthCalibrationFile.hashCode();
  }
}
