package model;

import controller.util.TreeviewItems.FrvaTreeItem;
import controller.util.TreeviewItems.FrvaTreeRootItem;
import java.io.File;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.data.MeasureSequence;
import model.data.SdCard;

public class FrvaModel {
  public static final String TREESTRUCTURE = "treeStructure.csv";
  public static final String LIBRARYPATH = System.getProperty("user.home") + File.separator + "FRVA" + File.separator;
  public static final String LIBRARYPATH_ABSOLUTE = "file:" + File.separator + File.separator + LIBRARYPATH;

  private final Logger logger = Logger.getLogger("FRVA");
  private final Set<SdCard> cache = new HashSet<>();
  private final String applicationName = "FRVA";
  private final List<SdCard> library = new ArrayList<>();
  private final IntegerProperty currentlySelectedTab = new SimpleIntegerProperty();
  private final Map<Integer, ObservableList<MeasureSequence>> selectionMap = new HashMap<>();
  private boolean loadLibraryOnStart = false;
  private final Executor executor = Executors.newCachedThreadPool(runnable -> {
    Thread t = new Thread(runnable);
    t.setDaemon(true);
    return t;
  });

  private String libraryPath;

  /**
   * Constructor for a new Model.
   */
  public FrvaModel() {
    loadLibrary();
  }

  private void loadLibrary() {


    logger.info("Library path is set to " + libraryPath);
    if (loadLibraryOnStart) {
      File folder = new File(libraryPath);
      if (!folder.exists()) {
        setUpLibrary(folder);
      }

    }
  }


  public TreeView readInSdCard(File sdCardLocation, TreeView<FrvaTreeRootItem> treeView, String name) {


    TreeItem root = treeView.getRoot();
    SdCard sdCard = new SdCard(sdCardLocation, name, this);
    return null;


  }

  public void addSelectionMapping(int tabId) {
    selectionMap.put(tabId, FXCollections.observableArrayList());
  }

  public void removeSelectionMapping(int tabId) {
    selectionMap.remove(tabId);
  }

  public Set<SdCard> getCache() {
    return cache;
  }

  public ObservableList<MeasureSequence> getCurrentSelectionList() {
    return selectionMap.get(currentlySelectedTab.get());
  }

  public String getApplicationName() {
    return applicationName;
  }

  public List<SdCard> getLibrary() {
    return library;
  }

  public void addSdCard(SdCard sdCard) {
    library.add(sdCard);
  }

  public void addMeasurementsToLibrary(List<MeasureSequence> listToAdd) {

  }

  public void setCurrentlySelectedTab(int currentlySelectedTab) {
    this.currentlySelectedTab.set(currentlySelectedTab);
  }

  public ObservableList<MeasureSequence> getObservableList(int mapKey) {
    return selectionMap.get(mapKey);
  }

  public IntegerProperty getCurrentlySelectedTabProperty() {
    return currentlySelectedTab;
  }

  public Executor getExecutor() {
    return executor;
  }


  /**
   * Delets a MeasuremnetSequence from the library and selection.
   *
   * @param list List of MesurementSequences to deleteFile.
   */
  public void deleteMeasureSequences(List<MeasureSequence> list) {
    /*
    if (confirmDelete(list.stream().filter(Objects::nonNull).count())) {
      Set<DataFile> set = new HashSet<>();
      for (SdCard sdCard : library) {
        for (DataFile dataFile : sdCard.getDataFiles()) {
          dataFile.getMeasureSequences().removeAll(list);
        }
      }
      for (List<MeasureSequence> measureSequenceList : selectionMap.values()) {
        measureSequenceList.removeAll(list);
        for (MeasureSequence ms : list) {
          if (ms != null) {
            set.add(ms.getDataFile());
          }
        }
      }

      updateLibrary(set);

    }
*/
  }

  /**
   * Writes changes to the library.
   *
   * @param list a List of all manipulated Files.
   */
  public void updateLibrary(Set<MeasureSequence> list) {
    /*Writer writer = null;

    for (DataFile d : list) {
      try {
        File file = new File(libraryPath + d.getSdCard().getName()
            + File.separator + d.getFolderName() + File.separator + d.getOriginalFileName());
        logger.info("rewrite File " + d.getOriginalFileName());
        writer = Files.newBufferedWriter(Paths.get(file.toURI()));
        for (MeasureSequence ms : d.getMeasureSequences()) {
          writer.write(ms.getCsv());
          writer.flush();
        }

        writer.close();
      } catch (IOException e) {
        logger.info(e.getMessage());
      }
    }

    cleanUpLibrary();*/
  }


  /**
   * Writes Data from SDCARDs to Files, in original format.
   *
   * @param list       List of SDCARD to save.
   * @param exportPath the path where the SDCARD is exported to.
   */
  public List<SdCard> writeData(List<MeasureSequence> list, Path exportPath) {
    List<SdCard> returnList = new ArrayList<>();
    SdCard sdCard = null;
    String currentFolder = null;
    String path = null;
    List<File> sdCardFolderList = new ArrayList<>();
    System.out.println(list.size());
    for (MeasureSequence measureSequence : list) {
      try {
        System.out.println(measureSequence);
        if (!measureSequence.getContainingSdCard().equals(sdCard)) {
          sdCard = measureSequence.getContainingSdCard();
          path = exportPath.toString() + File.separator + sdCard.getName();
          File card = new File(path);
          sdCardFolderList.add(card);

          if (card.exists()) {
            if (confirmOverriding(path, card)) {
              deleteFile(card);
            } else {
              logger.info("Export cancelled");
              return returnList;
            }
          }
          //Create SD Card Folder
          if (card.mkdirs()) {
            logger.info("Created SD-Card: " + path);
            //Create Calibration Files
            writeCalibrationFiles(sdCard, path);
            currentFolder = null;
          }

        }


        if (!measureSequence.getContainingFile().getParent().equals(currentFolder)) {
          path += File.separator + measureSequence.getContainingFile().getParent();
          File dayFolder = new File(path);
          if (!dayFolder.exists()) {
            dayFolder.mkdirs();
          }
          currentFolder = measureSequence.getContainingFile().getParent();
          logger.info("Created day-folder: " + path);
        }

        File file = new File(path + File.separator
            + measureSequence.getContainingFile().getName());
        Writer writer;

        if (!file.exists()) {
          writer = Files.newBufferedWriter(Paths.get(file.toURI()));
          logger.info("Created file: " + path + File.separator
              + measureSequence.getContainingFile().getName());
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
      System.out.println("in model"+ f.getAbsolutePath());

      returnList.add(new SdCard(f, null, this));

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

  private boolean confirmOverriding(String path, File card) {
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
    alert.setContentText("Library is going to be set up at " + libraryPath);
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
    logger.info("deleteFile file " + file);
  }


  /**
   * Returns the number of measuresequences in the library.
   */
  public int getLibrarySize() {
    return 4;
  }


}
