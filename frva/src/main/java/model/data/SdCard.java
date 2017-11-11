package model.data;

import com.sun.xml.internal.bind.v2.model.core.ID;
import controller.util.TreeviewItems.FrvaTreeSdCardItem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import model.FrvaModel;

public class SdCard {
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
  public SdCard(File sdCardPath, String name, FrvaModel model, boolean lazyLoaded) {
    // System.out.println("created new SD Card");
    this.sdCardPath = sdCardPath;
    // System.out.println("sdcardpadh " + sdCardPath.getPath());
    this.model = model;
    wavelengthCalibrationFile = readCalibrationFile(sdCardPath, "wl_", 1);
    sensorCalibrationFileWr = readCalibrationFile(sdCardPath, "radioWR_", 0);
    sensorCalibrationFileVeg = readCalibrationFile(sdCardPath, "radioVEG_", 0);

    //System.out.println("SD CArd has name " + name);
    if (name == null) {
      String[] arr = sdCardPath.getPath().split(File.separator);
      this.name = arr[arr.length - 1];
      // System.out.println("set name to "+name);
    } else {
      this.name = name;
    }


    if (!lazyLoaded) {
      readDatafiles(sdCardPath);
    } else {
      try {
        dataFiles = lazyReadDatafiles(sdCardPath);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }


  }

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

    try (BufferedReader reader = new BufferedReader(new FileReader(sdCardPath + File.separator + "db.csv"))) {

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

  private void readDatafiles(File sdCardPath) {
    dataFiles = new ArrayList<>();

    File[] listOfDirectories = sdCardPath.listFiles(File::isDirectory);
    System.out.println("list of dir is " + listOfDirectories.length);

    for (File directory : listOfDirectories) {
      File[] listOfDataFiles = directory.listFiles();
      System.out.println("Files: " + listOfDataFiles.length);
      for (File dataFile : listOfDataFiles) {
        //  System.out.println("added new Datafile");
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
    return this.dataFiles.stream().findAny().get().getMeasureSequences().stream().findAny().get().getSerial();


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
  public int hashCode() {
    return wavelengthCalibrationFile.hashCode();
  }

  public void setPathToLibrary() {
    this.sdCardPath = new File(FrvaModel.LIBRARYPATH + File.separator + this.name);
    for (DataFile df : dataFiles) {
      df.setPathToLibrary();

    }
  }

  public void serialize() {
    //  System.out.println("name is currently "+name);
    File file = new File(FrvaModel.LIBRARYPATH + File.separator + name + File.separator + "db.csv");
    try (Writer writer = new FileWriter(file)) {
      for (MeasureSequence ms : getMeasureSequences()) {
        writer.write(sdCardPath.getPath() + File.separator + ms.getDataFile().getFolderName() + File.separator + ms.getDataFile().getDataFileName() + ";" + ms.getMetadataAsString() + "\n");
        writer.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  /**
   * Deletes the Metadata from the Database.
   *
   * @param id of the MeasurementSequence
   * @param dataFile the measurementSequence is tin
   */

  public void removeMetadataEntry(String id, DataFile dataFile) {
    System.out.println("remove " + id + " from " + dataFile.getOriginalFile().getPath());
    File file = new File(FrvaModel.LIBRARYPATH + File.separator + name + File.separator + "db.csv");
    File newdb = new File(FrvaModel.LIBRARYPATH + File.separator + name + File.separator + "dbbak.csv");


    try (BufferedReader reader = new BufferedReader(new FileReader(file)); Writer writer= new FileWriter(newdb)
    ) {
      String line;
      while((line=reader.readLine())!=null){
        if(line.split(";")[1].equals(id)){
          if((line=reader.readLine())==null){break;};
        }
        writer.write(line+"\n");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    file.delete();
    newdb.renameTo(new File(FrvaModel.LIBRARYPATH + File.separator + name + File.separator + "db.csv"));
  }

  /*TODO: Sync or not?
  @Override
  public boolean equals(Object o){
//    System.out.println("compared SD cards: result "+ this.getWavelengthCalibrationFile().equals(((SdCard)o).wavelengthCalibrationFile));
   if(this == o){return true;}
    return o instanceof SdCard &&this.getWavelengthCalibrationFile().equals(((SdCard)o).wavelengthCalibrationFile) ;
  }*/
}
