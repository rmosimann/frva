package model.data;

import com.sun.media.jfxmedia.logging.Logger;
import controller.util.treeviewitems.FrvaTreeRootItem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 07.12.17.
 */
public class FileInOut {
  private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("FRVA");


  /**
   * Creates a file with the metadatas of all measurements called db.csv
   *
   * @param sdCard of which the db.csv should be created.
   */
  public static void writeDatabaseFile(SdCard sdCard) {
    File file = new File(FrvaModel.LIBRARYPATH + File.separator + sdCard.getName()
        + File.separator + "db.csv");
    if (file.getParentFile() != null) {
      file.getParentFile().mkdirs();
    }
    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.info("writing " + sdCard.getMeasureSequences().size()
        + " Measurements to db.csv");

    try (Writer writer = new FileWriter(file)) {
      for (MeasureSequence ms : sdCard.getMeasureSequences()) {
        writer.write(sdCard.getSdCardFile().getPath() + File.separator
            + ms.getDataFile().getFolderName() + File.separator
            + ms.getDataFile().getDataFileName() + ";"
            + ms.getMetadataAsString() + "\n");
        writer.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads all the Datafiles belonging to this SDCARD in a lazy manner.
   *
   * @param sdCard which should be read in.
   * @return List of dataFiles.
   * @throws FileNotFoundException when path is not found.
   */
  public static List<DataFile> readDatafilesLazy(SdCard sdCard) throws FileNotFoundException {
    File sdCardFile = sdCard.getSdCardFile();
    List<DataFile> dataFiles = new ArrayList<>();

    String line;
    String currentFile = "";
    List<String[]> list = new ArrayList<>();

    if (!new File(sdCardFile + File.separator + "db.csv").exists()) {
      dataFiles = getDataFiles(sdCard);
      for (DataFile df : dataFiles) {
        sdCard.getDataFiles().add(df);
      }
      if (sdCard.isPathInLibrary()) {
        FileInOut.writeDatabaseFile(sdCard);
      }
    } else {
      try (BufferedReader reader = new BufferedReader(
          new FileReader(sdCardFile + File.separator + "db.csv"))) {
        while ((line = reader.readLine()) != null) {
          String[] data = line.split(";");
          if (!data[0].equals(currentFile)) {
            if (list.size() > 0) {
              dataFiles.add(new DataFile(sdCard, new File(currentFile), list));
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
      if (list.size() > 0) {
        dataFiles.add(new DataFile(sdCard, new File(currentFile), list));
      }
    }
    return dataFiles;
  }


  /**
   * Puts all DataFiles in a SDCARD Folder into a list.
   *
   * @param sdCard the List with all DataFiles.
   * @return List of DataFiles.
   */
  public static List<DataFile> getDataFiles(SdCard sdCard) {
    List<DataFile> returnList = new ArrayList<>();
    File[] listOfDirectories = sdCard.getSdCardFile().listFiles(File::isDirectory);

    for (File directory : listOfDirectories) {
      File[] listOfDataFiles = directory.listFiles();
      for (File dataFile : listOfDataFiles) {
        returnList.add(new DataFile(sdCard, dataFile));
      }
    }
    return returnList;
  }

  /**
   * Reads in the calibration File of an SD-Card.
   *
   * @param sdCard which is parent of the calib-file.
   * @param filter name of the calib file (normally cal.csv)
   * @return created calibration file.
   */
  public static CalibrationFile readCalibrationFile(SdCard sdCard, String filter) {

    File folder = sdCard.getSdCardFile();
    File[] listOfFiles = folder.listFiles((dir, name) -> name.equals(filter));
    return new CalibrationFile(listOfFiles[0]);
  }

  /**
   * Creates a List of MeasureSequences based on the actual data.
   *
   * @param dataFile of which MeasureSequences should be created.
   * @return a list of MeasureSequences
   */
  public static List<MeasureSequence> readInMetadataOfMeasureSequences(DataFile dataFile) {
    File filename = dataFile.getOriginalFile();
    String line = "";
    List<MeasureSequence> measureSequences = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          if (Character.isDigit(line.charAt(0))) {
            measureSequences.add(new MeasureSequence(line, dataFile));
            int i = 0;
            while ((line = br.readLine()) != null && i < dataFile.getMeasurementLength() - 1) {
              i++;
            }
          }
        }
      }
      // measureSequences.add(new MeasureSequence(line, dataFile));

    } catch (IOException e) {
      e.printStackTrace();
    }
    return measureSequences;
  }

  /**
   * Removes the measuresequence from the file on the Disk.
   *
   * @param dataFile         containing the measurement.
   * @param measureSequences to remove from file.
   */
  public static void removeMeasureSequences(DataFile dataFile, List<MeasureSequence>
      measureSequences) {
    File updatedFile = new File(dataFile.getOriginalFile().getAbsolutePath() + ".bak");

    try (Writer writer = new BufferedWriter(new FileWriter(updatedFile));
         BufferedReader reader = new BufferedReader(
             new FileReader(dataFile.getOriginalFile()))) {

      List<String> ids = measureSequences.stream()
          .map(measureSequence -> measureSequence.getId())
          .collect(Collectors.toList());

      String line;
      while ((line = reader.readLine()) != null) {
        if (ids.contains(line.split(";")[0])) {
          for (int i = 0; i < dataFile.getMeasurementLength(); i++) {
            line = reader.readLine();
          }
        } else {
          for (int i = 0; i < dataFile.getMeasurementLength(); i++) {
            writer.write(line + "\n");
            line = reader.readLine();
          }
          writer.write(line + "\n");
        }
      }

      dataFile.getOriginalFile().delete();
      updatedFile.renameTo(dataFile.getOriginalFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads in the data of the measurement.
   *
   * @param measureSequence to read in.
   * @return the measureSequence.
   */
  public static Map<MeasureSequence.SequenceKeyName, double[]> readInMeasurement(
      MeasureSequence measureSequence) {

    Map<MeasureSequence.SequenceKeyName, double[]> measurements = new HashMap<>();
    String line = "";
    boolean found = false;
    boolean done = false;
    try (BufferedReader br = new BufferedReader(new FileReader(measureSequence.getDataFile()
        .getOriginalFile()))) {
      while ((line = br.readLine()) != null && !found) {

        if (line.length() > 1 && Character.isDigit(line.charAt(0)) && (line.split(";")[0]
            .equals(measureSequence.getId()))) {
          found = true;

          while ((line = br.readLine()) != null && !done) {
            if (line.length() > 0) {
              if (Character.isDigit(line.charAt(0))) {
                done = true;
              } else {
                String[] temp = line.split(";");
                MeasureSequence.SequenceKeyName key = MeasureSequence.SequenceKeyName
                    .valueOf(temp[0].toUpperCase());

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
      e.printStackTrace();
    }
    return measurements;
  }


  /**
   * Writes Data from SDCARDs to Files, in original format.
   *
   * @param list       List of MeasurementSequences to save.
   * @param exportPath the path where the SDCARDs are exported to.
   * @return a list of the written SDCARDS.
   */
  public static List<SdCard> createFiles(List<MeasureSequence> list, Path exportPath) {
    List<SdCard> returnList = new ArrayList<>();
    SdCard sdCard = null;
    String currentFolder = null;
    String sdCardPath = null;
    String dayFolderPath = null;
    List<File> sdCardFolderList = new ArrayList<>();
    for (MeasureSequence measureSequence : list) {
      try {
        if (!measureSequence.getContainingSdCard().equals(sdCard)) {
          sdCard = measureSequence.getContainingSdCard();
          sdCardPath = exportPath.toString() + File.separator + sdCard.getName();
          File card = new File(sdCardPath);
          sdCardFolderList.add(card);

          if (card.exists()) {
            if (confirmOverriding(sdCardPath)) {
              deleteFile(card);
            } else {
              logger.info("Export cancelled");
              return returnList;
            }
          }
          //Create SD Card Folder
          if (card.mkdirs()) {
            logger.info("Created SD-Card: " + sdCardPath);
            writeCalibrationFile(sdCard, sdCardPath);
            currentFolder = null;
          }
        }

        //create DataFileFolder
        if (!measureSequence.getDataFile().getFolderName().equals(currentFolder)) {
          dayFolderPath = sdCardPath + File.separator
              + measureSequence.getDataFile().getFolderName();
          File dayFolder = new File(dayFolderPath);
          if (!dayFolder.exists()) {
            dayFolder.mkdirs();
          }
          currentFolder = measureSequence.getDataFile().getFolderName();
          logger.info("Created day-folder: " + dayFolderPath);
        }

        File dataFile = new File(dayFolderPath + File.separator
            + measureSequence.getDataFile().getDataFileName());
        Writer writer;

        if (!dataFile.exists()) {
          writer = Files.newBufferedWriter(Paths.get(dataFile.toURI()));
          logger.info("Created dataFile: " + sdCardPath + File.separator
              + measureSequence.getDataFile().getDataFileName());
        } else {
          writer = new FileWriter(dataFile, true);
        }
        writer.write(measureSequence.getCsv());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        logger.info(e.getStackTrace().toString());
      }

    }
    for (File f : sdCardFolderList) {
      returnList.add(new SdCard(f, null));
    }
    return returnList;
  }


  private static void writeCalibrationFile(SdCard sdCard, String path) throws IOException {
    Files.copy(Paths.get(sdCard.getCalibrationFile().getCalibrationFile().toURI()),
        Paths.get(new File(path + File.separator
            + sdCard.getCalibrationFile().getCalibrationFile().getName()).toURI()));
    logger.info("Created Calibration Files");
  }

  private static boolean confirmOverriding(String path) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Warning");
    alert.setHeaderText("Directory already exists");
    alert.setContentText("The chosen directory " + path
        + " already exists. All containing data will be overridden. \nDo you want to continue?");
    Optional<ButtonType> result = alert.showAndWait();
    return result.get() == ButtonType.OK;
  }

  /**
   * Deletes a specific file.
   *
   * @param file The File to delete.
   */
  public static void deleteFile(File file) {
    if (file.exists() && file.isDirectory() && file.listFiles().length != 0) {
      for (File f : file.listFiles()) {
        deleteFile(f);
      }
    }
    file.delete();
    logger.info("Deleted File:" + file);
  }


  /**
   * Writes a new recording to the library.
   *
   * @param measureSequence of measureSequences to add.
   * @param calibrationFile of the attached device.
   * @param currentSdCard   Path of the SDCard where the Data should be written to.
   */
  public static void writeLiveMeasurements(MeasureSequence measureSequence,
                                           CalibrationFile calibrationFile, File currentSdCard) {

    File calibFile = new File(currentSdCard.getAbsolutePath() + File.separator
        + "cal.csv");
    File dataFileFolder = new File(currentSdCard.getAbsolutePath()
        + File.separator + "LiveSDFolder");
    File dataFile = new File(dataFileFolder.getAbsolutePath() + File.separator
        + "liveSDFile" + ".csv");


    if (!currentSdCard.exists()) {
      currentSdCard.mkdir();
    }

    if (!calibFile.exists()) {
      try (FileWriter fileWriter = new FileWriter(calibFile, false)) {
        if (calibrationFile != null) {
          fileWriter.write(calibrationFile.calibrationAsString());
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    if (!dataFileFolder.exists()) {
      dataFileFolder.mkdir();
    }

    try (FileWriter fileWriter = new FileWriter(dataFile, true)) {
      if (measureSequence != null) {
        if (measureSequence instanceof LiveMeasureSequence) {
          fileWriter.write(measureSequence.getCsv());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Tries to find empty folders and files in library and deletes them.
   */
  public static void checkForEmptyFiles() {
    File lib = new File(FrvaModel.LIBRARYPATH);
    for (File sdCard : lib.listFiles()) {

      if (sdCard.isDirectory()) {
        for (File dataFolder : sdCard.listFiles()) {

          //Checks if the dataFile contains no measurements.
          if (dataFolder.isDirectory()) {
            for (File dataFile : dataFolder.listFiles()) {
              if (dataFile.getName().toLowerCase().endsWith(".csv") && dataFile.length() == 0) {
                dataFile.delete();
              }
            }

            //Checks if a File is in the Datafolder, deletes Folder if no File is present.

            if (dataFolder.listFiles(new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name) {
                return name.endsWith(".CSV") || name.endsWith(".csv");
              }
            }).length < 1) {
              logger.info("delete " + dataFolder.getPath());
              for (File file : dataFolder.listFiles()) {
                file.delete();
              }
              dataFolder.delete();
            }

          }
        }

        //Checks if SD card contains no DataFolders and deletes cal-File and db File.

        if (sdCard.listFiles(new FileFilter() {
          @Override
          public boolean accept(File pathname) {
            return pathname.isDirectory();
          }
        }).length == 0) {
          for (File file : sdCard.listFiles()) {
            logger.info("delete File: " + file.getPath());
            file.delete();
          }
          logger.info("delete Folder: " + sdCard.getPath());
          sdCard.delete();
        }

      }


    }

  }
}
