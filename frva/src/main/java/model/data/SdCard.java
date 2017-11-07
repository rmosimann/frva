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
  private final List<DataFile> dataFiles;
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
    this.model=model;
    wavelengthCalibrationFile = readCalibrationFile(sdCardPath, "wl_", 1);
    sensorCalibrationFileWr = readCalibrationFile(sdCardPath, "radioWR_", 0);
    sensorCalibrationFileVeg = readCalibrationFile(sdCardPath, "radioVEG_", 0);

    dataFiles = readDatafiles(sdCardPath);


    if (name == null) {
      String[] arr = sdCardPath.getPath().split(File.separator);
      this.name = arr[arr.length - 1];
    } else {
      this.name = name;
    }
  }

  private List<DataFile> readDatafiles(File sdCardPath) {
    List<DataFile> dataFiles = new ArrayList<>();
    File folder = sdCardPath;

    File[] listOfDirectories = folder.listFiles(File::isDirectory);

    for (File directory : listOfDirectories) {
      File[] listOfDataFiles = directory.listFiles();
      for (File dataFile : listOfDataFiles) {
        dataFiles.add(new DataFile(this, dataFile));
      }
    }
    return dataFiles;
  }

  private CalibrationFile readCalibrationFile(File sdCardPath, String filter, int skipLines) {
    File folder = sdCardPath;
    File[] listOfFiles = folder.listFiles((dir, name) -> name.contains(filter)
        && name.endsWith(".csv"));

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

  public List<DataFile> getDataFiles() {
    return dataFiles;
  }

  /**
   * Getter for the devices Serial-Number.
   *
   * @return SerialNumber as String.
   */
  public String getDeviceSerialNr() {
    if (this.dataFiles == null || this.dataFiles.isEmpty()) {
      throw new IllegalArgumentException();
    }
    return this.dataFiles.stream()
        .findAny().get().getMeasureSequences().stream().findAny().get().getSerial();
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
    List<MeasureSequence> list = new ArrayList<>();
    for (DataFile dataFile : dataFiles) {
      list.addAll(dataFile.getMeasureSequences());
    }
    return list;
  }

  /**
   * Checks if SDCARD is empty, empty DataFiles are removed before.
   *
   * @return true when empty.
   */
  public boolean isEmpty() {
    if (dataFiles.isEmpty()) {
      return true;
    }
    boolean isEmpty = true;
    for (DataFile dfile : dataFiles) {
      if (!dfile.isEmpty()) {
        isEmpty = false;
      }
    }
    return isEmpty;
  }

  public File getPath() {
    return this.sdCardPath;
  }


  public MeasureSequence readSingleMeasurementSequence(File containingFile, String id, FrvaModel model) {

    DataFile df = new DataFile(this, containingFile, id);
    dataFiles.add(df);
    return df.getLastAddedMeasurement();

  }

/*
  public List<MeasureSequence> readInFiles() {
    long startTime=System.currentTimeMillis();
   // System.out.println("started");
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
                for (int i = 1; i < 5; i++) {
                  fileContent.add(i, br.readLine());
                  br.readLine();
                }
              }

              //TODO: probably unneeded measureSequences.add(new MeasureSequence(this, datafile, model, fileContent));
              for (String str : fileContent) {
               //System.out.println(str.substring(0, 20));
              }

              if (fileContent.size() == 5) {
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

    System.out.println("Took:" + (System.currentTimeMillis()-startTime));
  //  System.out.println("added now " + measureSequences.size() + " Measuresequences");
    return measureSequences;
  }
  */

  @Override
  public boolean equals(Object object) {
    return (object instanceof SdCard
        && ((SdCard) object).getWavelengthCalibrationFile().equals(wavelengthCalibrationFile));

  }

  @Override
  public int hashCode() {
    return wavelengthCalibrationFile.hashCode();
  }

  public void setPathToLibrary() {
    this.sdCardPath = new File(FrvaModel.LIBRARYPATH + File.separator + this.name);
    for(DataFile df:dataFiles){
      df.setPathToLibrary();

    }
  }
}
