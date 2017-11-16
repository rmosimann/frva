package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import model.FrvaModel;

public class SdCard {
  private final Logger logger = Logger.getLogger("FRVA");
  private List<DataFile> dataFiles;
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
    if (name == null) {
      String[] arr = sdCardPath.getPath().split(File.separator);
      this.name = arr[arr.length - 1];
    } else {
      this.name = name;
    }

    wavelengthCalibrationFile = readCalibrationFile(sdCardPath, "wl_", 1);
    sensorCalibrationFileWr = readCalibrationFile(sdCardPath, "radioWR_", 0);
    sensorCalibrationFileVeg = readCalibrationFile(sdCardPath, "radioVEG_", 0);

    try {
      dataFiles = lazyReadDatafiles(sdCardPath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads all the Datafiles belonging to this SDCARD in a lazy manner.
   *
   * @param sdCardPath Path where the SDCARD is located.
   * @return A List of all contained DataFiles.
   * @throws FileNotFoundException when path is not found.
   */
  public List<DataFile> lazyReadDatafiles(File sdCardPath) throws FileNotFoundException {
    List<DataFile> returnList = new ArrayList<>();
    String line;
    String currentFile = "";
    List<String[]> list = new ArrayList<>();

    //TODO: Serialize before reading in: Serialization has to happen on import
    if (!new File(sdCardPath + File.separator + "db.csv").exists()) {
      readDatafiles(sdCardPath);
      serialize();
    }

    try (BufferedReader reader = new BufferedReader(
        new FileReader(sdCardPath + File.separator + "db.csv"))) {
      while ((line = reader.readLine()) != null) {
        String[] data = line.split(";");

        if (!data[0].equals(currentFile)) {
          if (list.size() > 0) {
            returnList.add(new DataFile(this, new File(currentFile), list));
          }
          currentFile = data[0];
          list.clear();
        }

        String[] temp = new String[data.length - 1];
        for (int i = 0; i < temp.length; i++) {
          temp[i] = data[i + 1];
        }
        list.add(temp);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    // System.out.println("created last DataFile with " + list.size() + " Elements");

    if (list.size() > 0) {
      returnList.add(new DataFile(this, new File(currentFile), list));
    }

    return returnList;
  }

  /**
   * Puts all DataFiles in a SDCARD Folder into a list.
   *
   * @param sdCardPath the List with all DataFiles.
   */
  private void readDatafiles(File sdCardPath) {
    dataFiles = new ArrayList<>();

    File[] listOfDirectories = sdCardPath.listFiles(File::isDirectory);

    for (File directory : listOfDirectories) {
      File[] listOfDataFiles = directory.listFiles();
      for (File dataFile : listOfDataFiles) {
        dataFiles.add(new DataFile(this, dataFile));
      }
    }
  }


  private CalibrationFile readCalibrationFile(File sdCardPath, String filter, int skipLines) {
    File folder = sdCardPath;
    // System.out.println(sdCardPath.getAbsolutePath());
    //System.out.println(folder.listFiles().length);

    File[] listOfFiles = folder.listFiles((dir, name) -> name.contains(filter)
        && name.endsWith(".csv") && !name.equals("db.csv"));

    return new CalibrationFile(listOfFiles[0], skipLines);
  }


  public MeasureSequence readSingleMeasurementSequence(File containingFile,
                                                       String id, FrvaModel model) {

    DataFile df = new DataFile(this, containingFile, id);
    dataFiles.add(df);
    return df.getLastAddedMeasurement();
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
    System.out.println("SDCARD " + sdCardPath + " is empty!");
    return isEmpty;
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
      return "Empty Dataset";
    }
    return this.dataFiles.stream()
        .findAny()
        .get()
        .getMeasureSequences()
        .stream()
        .findAny()
        .get()
        .getSerial();
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


  public File getPath() {
    return this.sdCardPath;
  }


  @Override
  public int hashCode() {
    return wavelengthCalibrationFile.hashCode();
  }

  public void setPathToLibrary() {
    this.sdCardPath = new File(FrvaModel.LIBRARYPATH + File.separator + this.name);
    for (DataFile df : dataFiles) {
      df.setPathToLibrary();

    }
  }

  /**
   * Writes the Metadata of all MeasurementSequences in this SDCARD to the db.csv file.
   */
  public void serialize() {
    File file = new File(FrvaModel.LIBRARYPATH + File.separator + name + File.separator + "db.csv");
    try (Writer writer = new FileWriter(file)) {
      for (MeasureSequence ms : getMeasureSequences()) {
        writer.write(sdCardPath.getPath() + File.separator
            + ms.getDataFile().getFolderName() + File.separator
            + ms.getDataFile().getDataFileName() + ";"
            + ms.getMetadataAsString() + "\n");
        writer.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
