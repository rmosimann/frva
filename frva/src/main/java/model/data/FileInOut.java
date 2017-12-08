package model.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 07.12.17.
 */
public class FileInOut {

  /**
   * Creates a file with the metadatas of all measurements called db.csv
   *
   * @param sdCard of which the db.csv should be created.
   */
  public static void writeDB(SdCard sdCard) {
    File file = new File(FrvaModel.LIBRARYPATH + File.separator + sdCard.getName() + File.separator + "db.csv");
    if (file.getParentFile() != null) {
      file.getParentFile().mkdirs();
    }
    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("writing " + sdCard.getMeasureSequences().size() + " Measurements to db.csv");

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
        FileInOut.writeDB(sdCard);
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
   * @param sdCard    which is parent of the calib-file.
   * @param filter    name of the calib file (normally cal.csv)
   * @param skipLines how many lines should be skipped (header).
   * @return created calibration file.
   */
  public static CalibrationFile readCalibrationFile(SdCard sdCard, String filter, int skipLines) {
    File folder = sdCard.getSdCardFile();
    File[] listOfFiles = folder.listFiles((dir, name) -> name.contains(filter)
        && name.endsWith(".csv") && !name.equals("db.csv"));
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


  public static void removeMeasureSequences(DataFile dataFile, List<MeasureSequence> measureSequences) {
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

  public static Map<MeasureSequence.SequenceKeyName, double[]> readInMeasurement
      (MeasureSequence measureSequence) {

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
                MeasureSequence.SequenceKeyName key = MeasureSequence.SequenceKeyName.valueOf(temp[0].toUpperCase());

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
}
