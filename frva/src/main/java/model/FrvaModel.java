package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javax.bluetooth.RemoteDevice;
import model.data.DataFile;
import model.data.FileInOut;
import model.data.LiveMeasureSequence;
import model.data.MeasureSequence;
import model.data.SdCard;


public class FrvaModel {

  public static String LIBRARYPATH = System.getProperty("user.home") + File.separator
      + "FRVA" + File.separator;
  private final ObservableList liveMeasurements = FXCollections.observableArrayList();
  private File currentLiveSdCardPath;
  private final Logger logger = Logger.getLogger("FRVA");
  private final String applicationName = "FRVA";
  private final List<SdCard> library = new ArrayList<>();
  private final ObjectProperty<Tab> currentlySelectedTab = new SimpleObjectProperty<>();
  private final ObjectProperty<Node> activeView = new SimpleObjectProperty<>();
  private final Map<Tab, ObservableList<MeasureSequence>> selectionMap = new HashMap<>();
  private final ObservableList<MeasureSequence> liveSequences = FXCollections.observableArrayList();
  private final ObservableList<RemoteDevice> bltDevices = FXCollections.observableArrayList();
  private final Executor executor = Executors.newCachedThreadPool(runnable -> {
    Thread t = new Thread(runnable);
    t.setDaemon(true);
    return t;
  });


  /**
   * Constructor for a new Model.
   */
  public FrvaModel() {
    LIBRARYPATH = readInPreferences();
    loadLibrary();
  }

  private String readInPreferences() {
    Preferences pref = Preferences.userNodeForPackage(FrvaModel.class);
    String defaultLibPath = System.getProperty("user.home") + File.separator
        + "FRVA" + File.separator;
    String libraryPath = pref.get("librarypath", defaultLibPath);
    return libraryPath;
  }

  /**
   * Sets Preferences to the specified Librarypath and sets Library to new Path.
   *
   * @param newLibPath path where the Library should be located at.
   */
  public void changeLibraryPath(String newLibPath) {
    if (newLibPath != null) {
      boolean isEmpty = true;
      if (new File(newLibPath).list().length > 0) {
        Alert notEmptyAlert = new Alert(Alert.AlertType.CONFIRMATION);
        notEmptyAlert.getDialogPane().setMinHeight(200);
        notEmptyAlert.setTitle("Location not empty");
        notEmptyAlert.setHeaderText("The chosen location is not empty");
        notEmptyAlert.setContentText("Non FloX/RoX data may be deleted if you chose to continue.");
        Optional<ButtonType> result2 = notEmptyAlert.showAndWait();
        isEmpty = result2.isPresent() && result2.get() == ButtonType.OK;
      }

      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.getDialogPane().setMinHeight(200);
      alert.setTitle("Warning");
      alert.setHeaderText("For the change to take effect you need to restart the application");
      alert.setContentText("Press OK to quit the application now and set the Library path to \""
          + newLibPath + "\"\n or press cancel to keep the library path \"" + LIBRARYPATH + "\"");
      Optional<ButtonType> result = alert.showAndWait();
      boolean restartOk = result.isPresent() && result.get() == ButtonType.OK;


      if (restartOk && isEmpty) {
        LIBRARYPATH = newLibPath;
        Preferences pref = Preferences.userNodeForPackage(FrvaModel.class);
        pref.put("librarypath", newLibPath);
        System.exit(0);
      }
    }
  }


  /**
   * Loads existing Library or creates new one, when LibraryPath is empty.
   */

  private void loadLibrary() {
    library.clear();
    logger.info("Library path is set to " + LIBRARYPATH);

    File folder = new File(LIBRARYPATH);
    if (!folder.exists()) {
      setUpLibrary(folder);
    }

    for (File sdfolder : folder.listFiles()) {
      File calibFile = new File(sdfolder.getPath() + File.separator + "cal.csv");
      if (sdfolder.isDirectory() && calibFile.exists()) {
        library.add(new SdCard(sdfolder, sdfolder.getName()));
      }
    }
    FileInOut.checkForEmptyFiles();

  }

  /**
   * Adds entry for a new tab to selection List.
   *
   * @param tab that is specified in the created tab.
   */
  public void addSelectionMapping(Tab tab) {
    selectionMap.put(tab, FXCollections.observableArrayList());
  }

  /**
   * Removes entry for a closed tab in the selection List.
   *
   * @param tab ID that is specified in the tab.
   */
  public void removeSelectionMapping(Tab tab) {
    selectionMap.remove(tab);
  }


  /**
   * Delets a MeasurementSequence from the library and model.
   *
   * @param measureSequences List of MesurementSequences to deleteFile.
   */
  public void deleteMeasureSequences(List<MeasureSequence> measureSequences) {
    Vector<SdCard> changedSdcards = new Vector<>();
    Vector<DataFile> changedDataFiles = new Vector<>();

    for (MeasureSequence ms : measureSequences) {
      logger.info("Deleting MesureSequence: " + ms.getId());
      ms.getDataFile().getMeasureSequences().remove(ms);
      changedSdcards.add(ms.getDataFile().getSdCard());
      changedDataFiles.add(ms.getDataFile());
      ms.setDeleted(true);
    }

    changedDataFiles.forEach(dataFile -> {
      FileInOut.removeMeasureSequences(dataFile,
          measureSequences.stream()
              .filter(measureSequence -> {
                return measureSequence.getDataFile().equals(dataFile);
              })
              .collect(Collectors.toList())
      );
    });

    changedSdcards.forEach(sdCard -> {
      FileInOut.writeDatabaseFile(sdCard);
    });

    for (List<MeasureSequence> measureSequenceList : selectionMap.values()) {
      measureSequenceList.removeAll(measureSequences);
    }

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
   * Getter to read all MeasurementSequences in the Library.
   *
   * @return List of MeasurementSequences.
   */
  public List<MeasureSequence> getAllMeasureSequences() {
    List<MeasureSequence> list = new ArrayList<>();
    for (SdCard sdCard : library) {
      list.addAll(sdCard.getMeasureSequences());
    }
    return list;
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

  public ObjectProperty<Tab> getCurrentlySelectedTabProperty() {
    return currentlySelectedTab;
  }

  public void setCurrentlySelectedTab(Tab currentlySelectedTab) {
    this.currentlySelectedTab.set(currentlySelectedTab);
  }


  public ObservableList<MeasureSequence> getObservableList(Tab tab) {
    return selectionMap.get(tab);
  }


  public ObservableList<RemoteDevice> getBltDevices() {
    return bltDevices;
  }

  public Node getActiveView() {
    return activeView.get();
  }

  public ObjectProperty<Node> activeViewProperty() {
    return activeView;
  }

  public void setActiveView(Node activeView) {
    this.activeView.set(activeView);
  }

  public ObservableList<MeasureSequence> getLiveSequences() {
    return liveSequences;
  }

  /**
   * Adds a LiveMeasurementSequence to the list.
   *
   * @param measureSequence the MeasurementSequence to add.
   */
  public void addLiveSequence(LiveMeasureSequence measureSequence) {
    Platform.runLater(() -> {
      liveSequences.add(measureSequence);
    });
  }

  public File getCurrentLiveSdCardPath() {
    return currentLiveSdCardPath;
  }

  public void setCurrentLiveSdCardPath(String sdCardName) {
    this.currentLiveSdCardPath = new File(LIBRARYPATH + File.separator
        + "Rec " + sdCardName);
  }
}
