package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;

public class FrvaModel {
  public static final String LIBRARYPATH = System.getProperty("user.home") + File.separator + "FRVA" + File.separator;
  public final boolean lazyLoading = true;

  private final Logger logger = Logger.getLogger("FRVA");
  private final Set<SdCard> cache = new HashSet<>();
  private final String applicationName = "FRVA";
  private final List<SdCard> library = new ArrayList<>();
  private final IntegerProperty currentlySelectedTab = new SimpleIntegerProperty();
  private final Map<Integer, ObservableList<MeasureSequence>> selectionMap = new HashMap<>();
  private final Executor executor = Executors.newCachedThreadPool(runnable -> {
    Thread t = new Thread(runnable);
    t.setDaemon(true);
    return t;
  });


  /**
   * Constructor for a new Model.
   */
  public FrvaModel() {
    loadLibrary();
  }


  private void loadLibrary() {
    logger.info("Library path is set to " + LIBRARYPATH);

    File folder = new File(LIBRARYPATH);
    if (!folder.exists()) {
      setUpLibrary(folder);
    }

    for (File sdfolder : folder.listFiles()) {
      if (sdfolder.isDirectory()) {

        File sdcard = new File(LIBRARYPATH + sdfolder.getName());
        library.add(new SdCard(sdcard, sdfolder.getName(), this, lazyLoading));
      }
    }
  }


  public void addSelectionMapping(int tabId) {
    selectionMap.put(tabId, FXCollections.observableArrayList());
  }

  public void removeSelectionMapping(int tabId) {
    selectionMap.remove(tabId);
  }


  /**
   * Delets a MeasuremnetSequence from the library and selection.
   *
   * @param list List of MesurementSequences to deleteFile.
   */
  public void deleteMeasureSequences(List<MeasureSequence> list) {

    // Set<DataFile> set = new HashSet<>();

    for (MeasureSequence ms : list) {
      //Remove from DataFile
      ms.getDataFile().getMeasureSequences().remove(ms);
      //Collect DataFiles for later update
      //   set.add(ms.getDataFile());
      //Remove Metadata Entry from DB
      ms.getDataFile().getSdCard().removeMetadataEntry(ms.getId(), ms.getDataFile());
      //Remove Entry from csv

      File updatedFile = new File(ms.getDataFile().getOriginalFile().getAbsolutePath() + ".bak");


      //TODO Sluggish implementation as it reads through  file for every Measure Seq.
      try (Writer writer = new BufferedWriter(new FileWriter(updatedFile));
           BufferedReader reader = new BufferedReader(new FileReader(ms.getDataFile().getOriginalFile()))) {

        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println(line.length() > 15 ? "line: " + line.substring(0, 15) : "line: empty line");

          if (line.split(";")[0].equals(ms.getId())) {

            for (int i = 0; i < 9; i++) {
              System.out.println("skip line");
              line = reader.readLine();
            }
          } else {

            for (int i = 0; i < 9; i++) {

              System.out.println("add line to file");
              writer.write(line + "\n");
              line = reader.readLine();
            }
            writer.write(line + "\n");
          }
        }

        //Swop files
        ms.getDataFile().getOriginalFile().delete();
        updatedFile.renameTo(ms.getDataFile().getOriginalFile());


      } catch (IOException e) {
        e.printStackTrace();
      }

      System.out.println("set measurement as deleted");
      ms.setDeleted(true);
    }


    for (List<MeasureSequence> measureSequenceList : selectionMap.values()) {
      measureSequenceList.removeAll(list);
    }
    System.out.println("ticked measurements: " + getCurrentSelectionList().size());


    //cleanUpLibrary();

    //updateLibrary(set);


  }

  /**
   * Writes changes to the library.
   *
   * @param list a List of all manipulated Files.
   *             Old implementation with all MS read in. Prlbly not needed anymore.
   */
  public void updateLibrary(Set<DataFile> list) {


    for (DataFile d : list) {
      try (Writer writer = new BufferedWriter(new FileWriter(d.getOriginalFile()));
           BufferedReader reader = new BufferedReader(new FileReader(d.getOriginalFile()))) {

        logger.info("rewrite File " + d.getOriginalFileName());

        for (MeasureSequence ms : d.getMeasureSequences()) {
          writer.write(ms.getCsv());
          writer.flush();
        }

        writer.close();
      } catch (IOException e) {
        logger.info(e.getMessage());
      }
    }
  }

  /**
   * Writes Data from SDCARDs to Files, in original format.
   *
   * @param list       List of SDCARD to save.
   * @param exportPath the path where the SDCARD is exported to.
   */
  public List<SdCard> createFiles(List<MeasureSequence> list, Path exportPath) {
    List<SdCard> returnList = new ArrayList<>();
    SdCard sdCard = null;
    String currentFolder = null;
    String path = null;
    List<File> sdCardFolderList = new ArrayList<>();
    for (MeasureSequence measureSequence : list) {
      try {
        if (!measureSequence.getContainingSdCard().equals(sdCard)) {
          sdCard = measureSequence.getContainingSdCard();
          path = exportPath.toString() + File.separator + sdCard.getName();
          File card = new File(path);
          sdCardFolderList.add(card);

          if (card.exists()) {
            if (confirmOverriding(path)) {
              deleteFile(card);
              System.out.println("going to export " + getCurrentSelectionList().size() + " Measurement");
            } else {
              logger.info("Export cancelled");
              return returnList;
            }
          }
          //Create SD Card Folder
          if (card.mkdirs()) {
            logger.info("Created SD-Card: " + path);
            writeCalibrationFiles(sdCard, path);
            currentFolder = null;
          }
        }

        if (!measureSequence.getDataFile().getFolderName().equals(currentFolder)) {
          path += File.separator + measureSequence.getDataFile().getFolderName();
          File dayFolder = new File(path);
          if (!dayFolder.exists()) {
            dayFolder.mkdirs();
          }
          currentFolder = measureSequence.getDataFile().getFolderName();
          logger.info("Created day-folder: " + path);
        }

        File file = new File(path + File.separator
            + measureSequence.getDataFile().getDataFileName());
        Writer writer;

        if (!file.exists()) {
          writer = Files.newBufferedWriter(Paths.get(file.toURI()));
          logger.info("Created file: " + path + File.separator
              + measureSequence.getDataFile().getDataFileName());
        } else {
          writer = new FileWriter(file, true);
        }
        writer.write(measureSequence.getCsv());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        logger.info(e.getStackTrace().toString());
      }

    }
    for (File f : sdCardFolderList) {
      returnList.add(new SdCard(f, null, this, false));
    }
    return returnList;
  }


  private void writeCalibrationFiles(SdCard sdCard, String path) throws IOException {
    Files.copy(Paths.get(sdCard.getSensorCalibrationFileVeg().getCalibrationFile().toURI()),
        Paths.get(new File(path + File.separator
            + sdCard.getSensorCalibrationFileVeg().getCalibrationFile().getName()).toURI()));
    Files.copy(Paths.get(sdCard.getSensorCalibrationFileWr().getCalibrationFile().toURI()),
        Paths.get(new File(path + File.separator
            + sdCard.getSensorCalibrationFileWr().getCalibrationFile().getName()).toURI()));
    Files.copy(Paths.get(sdCard.getWavelengthCalibrationFile().getCalibrationFile().toURI()),
        Paths.get(new File(path + File.separator
            + sdCard.getWavelengthCalibrationFile().getCalibrationFile().getName()).toURI()));
    logger.info("Created Calibration Files");
  }

  private boolean confirmOverriding(String path) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Warning");
    alert.setHeaderText("Directory already exists");
    alert.setContentText("The chosen directory " + path
        + " already exists. All containing data will be overridden. \nDo you want to continue?");
    Optional<ButtonType> result = alert.showAndWait();
    return result.get() == ButtonType.OK;
  }

  private boolean confirmDelete(long amount) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Warning");
    alert.setHeaderText(amount + " Measurements are going to be deleted.");
    alert.setContentText("This action cannot be undone \nDo you want to continue?");
    Optional<ButtonType> result = alert.showAndWait();
    return result.get() == ButtonType.OK;
  }


  private boolean setUpLibrary(File path) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("No Library");
    alert.setHeaderText("No library has been found.");
    alert.setContentText("Library is going to be set up at " + LIBRARYPATH);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      return path.mkdirs();
    } else {
      Alert exit = new Alert(Alert.AlertType.INFORMATION);
      exit.setTitle("No Library");
      exit.setHeaderText("The application needs a library to run.");
      exit.setContentText("The application will now quit");
      Optional<ButtonType> answer = exit.showAndWait();
      System.exit(1);
    }
    return false;
  }


  /**
   * Removes empty SDCARDs from library.
   */
  public void cleanUpLibrary() {
    Iterator<SdCard> it = library.listIterator();
    while (it.hasNext()) {
      SdCard sdCard = it.next();
      if (sdCard.isEmpty()) {
        deleteFile(sdCard.getPath());
        it.remove();
      }
    }
  }

  /**
   * Deletes a specific file.
   *
   * @param file The File to delete.
   */
  public void deleteFile(File file) {
    if (file.exists() && file.isDirectory() && file.listFiles().length != 0) {
      for (File f : file.listFiles()) {
        deleteFile(f);
      }
    }
    file.delete();
    logger.info("deleteFile file " + file + file.exists());
  }

  public List<SdCard> getLibrary() {
    return library;
  }

  public Executor getExecutor() {
    return executor;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public ObservableList<MeasureSequence> getCurrentSelectionList() {
    return selectionMap.get(currentlySelectedTab.get());
  }

  public IntegerProperty getCurrentlySelectedTabProperty() {
    return currentlySelectedTab;
  }

  public void setCurrentlySelectedTab(int currentlySelectedTab) {
    this.currentlySelectedTab.set(currentlySelectedTab);
  }

  public ObservableList<MeasureSequence> getObservableList(int mapKey) {
    return selectionMap.get(mapKey);
  }

  /**
   * Getter to read all MeasurementSequences in the Library.
   *
   * @return List of MeasurementSequences.
   */
  public List<MeasureSequence> getLibraryAsMeasureSequences() {
    List<MeasureSequence> list = new ArrayList<>();
    for (SdCard sdCard : library) {
      list.addAll(sdCard.getMeasureSequences());
    }
    return list;
  }


}
