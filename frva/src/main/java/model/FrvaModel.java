package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.bluetooth.RemoteDevice;
import model.data.DataFile;
import model.data.FileInOut;
import model.data.MeasureSequence;
import model.data.SdCard;


public class FrvaModel {


  public static final String LIBRARYPATH = System.getProperty("user.home") + File.separator
      + "FRVA" + File.separator;
  private final ObservableList liveMeasurements = FXCollections.observableArrayList();
  private File currentLiveSdCardPath;
  private final Logger logger = Logger.getLogger("FRVA");
  private final String applicationName = "FRVA";
  private final List<SdCard> library = new ArrayList<>();
  private final IntegerProperty currentlySelectedTab = new SimpleIntegerProperty();
  private final ObjectProperty<Node> activeView = new SimpleObjectProperty<>();
  private final Map<Integer, ObservableList<MeasureSequence>> selectionMap = new HashMap<>();
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
    loadLibrary();
  }

  /**
   * Loads existing Library or creates new one, when LibraryPath is empty.
   */
  private void loadLibrary() {
    logger.info("Library path is set to " + LIBRARYPATH);

    File folder = new File(LIBRARYPATH);
    if (!folder.exists()) {
      setUpLibrary(folder);
    }

    for (File sdfolder : folder.listFiles()) {
      if (sdfolder.isDirectory()) {
        library.add(new SdCard(sdfolder, sdfolder.getName()));
      }
    }
  }

  /**
   * Adds entry for a new tab to selection List.
   *
   * @param tabId ID that is specified in the created tab.
   */
  public void addSelectionMapping(int tabId) {
    selectionMap.put(tabId, FXCollections.observableArrayList());
  }

  /**
   * Removes entry for a closed tab in the selection List.
   *
   * @param tabId ID that is specified in the tab.
   */
  public void removeSelectionMapping(int tabId) {
    selectionMap.remove(tabId);
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
      logger.info("Deleting MesureSequnce: " + ms.getId());
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

  public IntegerProperty getCurrentlySelectedTabProperty() {
    return currentlySelectedTab;
  }

  public void setCurrentlySelectedTab(int currentlySelectedTab) {
    this.currentlySelectedTab.set(currentlySelectedTab);
  }

  public ObservableList<MeasureSequence> getObservableList(int mapKey) {
    return selectionMap.get(mapKey);
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

  public File getCurrentLiveSdCardPath() {
    return currentLiveSdCardPath;
  }

  public void setCurrentLiveSdCardPath(String sdCardName) {
    this.currentLiveSdCardPath = new File(LIBRARYPATH + File.separator
        + "Rec " + sdCardName);
  }
}
