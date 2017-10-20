package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.PopupWindow;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;

import javax.security.auth.callback.ConfirmationCallback;
import javax.xml.crypto.Data;


public class FrvaModel {
  private final Logger logger = Logger.getLogger("FRVA");
  private final String applicationName = "FRVA";
  private final List<SdCard> library = new ArrayList<>();

  public IntegerProperty currentlySelectedTabProperty() {
    return currentlySelectedTab;
  }

  private final IntegerProperty currentlySelectedTab = new SimpleIntegerProperty();
  private final Map<Integer, ObservableList<MeasureSequence>> selectionMap = new HashMap<>();

  private String libraryPath;

  /**
   * Constructor for a new Model.
   */
  public FrvaModel() {
    libraryPath = "file:" + File.separator + File.separator + System.getProperty("user.home") + File.separator + "FRVA" + File.separator + "SDCARD";


    //TODO: loads exampledata, when available remove when import is implemented

    System.out.println(libraryPath);
    try {
      URL sdcard = new URI(libraryPath).toURL();
      if (sdcard != null) {
        library.add(new SdCard(sdcard));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void addSelectionMapping(int tabId) {
    selectionMap.put(tabId, FXCollections.observableArrayList());
  }

  public void removeSelectionMapping(int tabId) {
    selectionMap.remove(tabId);
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

  public void setCurrentlySelectedTab(int currentlySelectedTab) {
    this.currentlySelectedTab.set(currentlySelectedTab);
  }

  public ObservableList<MeasureSequence> getObservableList(int mapKey) {
    return selectionMap.get(mapKey);
  }

  public IntegerProperty getCurrentlySelectedTabProperty() {
    return currentlySelectedTab;
  }


  /**
   * Delets a MeasuremnetSequence from the library and selection.
   *
   * @param list List of MesurementSequences to delete.
   */
  public void deleteMeasureSequences(List<MeasureSequence> list) {
    Set<DataFile> set = new HashSet<>();
    for (SdCard sdCard : library) {
      for (DataFile dataFile : sdCard.getDataFiles()) {
        dataFile.getMeasureSequences().removeAll(list);
      }
    }
    for (List<MeasureSequence> measureSequenceList : selectionMap.values()) {
      measureSequenceList.removeAll(list);
      for (MeasureSequence ms : list
          ) {
        set.add(ms.getDataFile());
      }
    }
    List<MeasureSequence> listMeasureSequence = new ArrayList<>();
    for (DataFile dataFile : set) {
      listMeasureSequence.addAll(dataFile.getMeasureSequences());
    }
    updateLibrary(listMeasureSequence);
  }

  public void updateLibrary(List<MeasureSequence> list) {


    MeasureSequence measureSequence = null;
    String path = libraryPath;
    String currentFile = null;
    BufferedWriter currentWriter = null;

    for (MeasureSequence ms : list) {
      if (!measureSequence.getDataFile().getOriginalFileName().equals(currentFile)) {
        path += File.separator + measureSequence.getDataFile().getOriginalFileName();
        File file = new File(path);
        currentFile = measureSequence.getDataFile().getDataFileName();
      }

      try {
        if (currentWriter != null) {
          currentWriter.close();


          //  currentWriter = Files.newBufferedWriter(Paths.get(file.toURI()));
          logger.info("Created file: " + path);

          currentWriter.write(measureSequence.getCsv());
          currentWriter.flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * Writes Data from SDCARDs to Files, in original format.
   *
   * @param list List of SDCARD to save.
   */

  public void writeData(List<SdCard> list) {
    //Probably obsolete method
    BufferedWriter writer;
    try {
      for (SdCard sdCard : list) {
        String path = "lib" + File.separator + sdCard.getName();

        logger.info("Saving File to: " + path);

        //Create SD Card Folder
        File card = new File(path);
        if (card.exists() || card.mkdirs()) {
          //Create Calibration Files
          Files.copy(Paths.get(sdCard.getSensorCalibrationFileVeg().getCalibrationFile().toURI()),
              Paths.get(new File(path + File.separator
                  + sdCard.getSensorCalibrationFileVeg().getCalibrationFile().getName()).toURI()));
          Files.copy(Paths.get(sdCard.getSensorCalibrationFileWr().getCalibrationFile().toURI()),
              Paths.get(new File(path + File.separator
                  + sdCard.getSensorCalibrationFileWr().getCalibrationFile().getName()).toURI()));
          Files.copy(Paths.get(sdCard.getWavelengthCalibrationFile().getCalibrationFile().toURI()),
              Paths.get(new File(path + File.separator
                  + sdCard.getWavelengthCalibrationFile().getCalibrationFile().getName()).toURI()));

          for (DataFile dataFile : sdCard.getDataFiles()) {
            path += File.separator + dataFile.getFolderName();
            File folder = new File(path);
            //Create day-folder
            if (folder.exists() || folder.mkdirs()) {
              File file = new File(path + File.separator + dataFile.getOriginalFileName());
              writer = Files.newBufferedWriter(Paths.get(file.toURI()));
              for (MeasureSequence measureSequence : dataFile.getMeasureSequences()) {
                writer.write(measureSequence.getCsv());
              }
              writer.flush();
            }
          }
        }
      }


    } catch (IOException exception) {
      logger.log(new LogRecord(Level.INFO, exception.getMessage()));
    }


  }

  /**
   * Writes Data from SDCARDs to Files, in original format.
   *
   * @param list List of SDCARD to save.
   */
  public void writeData(List<MeasureSequence> list, Path exportPath) {
    SdCard sdCard = null;
    String currentFolder = null;
    String path = null;
    System.out.println(list.size());
    for (MeasureSequence measureSequence : list) {
      try {
        if (!measureSequence.getDataFile().getSdCard().equals(sdCard)) {
          sdCard = measureSequence.getDataFile().getSdCard();
          path = exportPath.toString() + File.separator + sdCard.getName();
          File card = new File(path);

          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Warning");
          alert.setHeaderText("Directory already exists");
          alert.setContentText("The chosen directory " + path + " already exists. All containing data will be overridden. \nDo you want to continue");
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            delete(card);


            //Create SD Card Folder
            if (card.mkdirs()) {
              logger.info("Created SD-Card: " + path);
              //Create Calibration Files
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
              currentFolder = null;
            }
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

        File file = new File(path + File.separator + measureSequence.getDataFile().getOriginalFileName());
        Writer writer;

        if (!file.exists()) {
          writer = Files.newBufferedWriter(Paths.get(file.toURI()));
          logger.info("Created file: " + path + File.separator + measureSequence.getDataFile().getOriginalFileName());
        } else {
          writer = new FileWriter(file, true);
          System.out.println("append");
        }
        writer.write(measureSequence.getCsv());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        logger.info(e.getStackTrace().toString());

      }

    }


  }

  public List<MeasureSequence> getLibraryAsMeasureSequences() {

    List<MeasureSequence> list = new ArrayList<>();
    for (SdCard sdCard : library
        ) {
      list.addAll(sdCard.getMeasureSequences());
    }
    return list;
  }


  public void cleanUp() {
    Iterator<SdCard> it = library.listIterator();
    while (it.hasNext()) {
      SdCard sdCard = it.next();
      if (sdCard.isEmpty()) {
        it.remove();
      }
    }
  }

  public void delete(File file) {
    if (file.exists() && file.isDirectory() && file.listFiles().length != 0) {
      for (File f : file.listFiles()
          ) {
        delete(f);
      }
    }

    System.out.println(file.delete());
    logger.info("delete file " + file);
  }
}
