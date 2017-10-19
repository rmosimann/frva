package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;


public class FrvaModel {
  private final Logger logger = Logger.getLogger("FRVA");
  private final String applicationName = "FRVA";
  private final List<SdCard> library = new ArrayList<>();


  public IntegerProperty currentlySelectedTabProperty() {
    return currentlySelectedTab;
  }

  private final IntegerProperty currentlySelectedTab = new SimpleIntegerProperty();
  private final Map<Integer, ObservableList<MeasureSequence>> selectionMap = new HashMap<>();

  /**
   * Constructor for a new Model.
   */
  public FrvaModel() {
    //TODO: loads exampledata,, when available remove when import is implemented
    URL sdcard = getClass().getResource("../SDCARD");
    if (sdcard != null) {
      library.add(new SdCard(sdcard));
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
    for (SdCard sdCard : library) {
      for (DataFile dataFile : sdCard.getDataFiles()) {
        dataFile.getMeasureSequences().removeAll(list);
      }
    }
    for (List<MeasureSequence> measureSequenceList : selectionMap.values()) {
      measureSequenceList.removeAll(list);
    }
  }

  /**
   * Writes Data from SDCARDs to Files, in original format.
   *
   * @param list List of SDCARD to save.
   */
  public void writeData(List<SdCard> list) {
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


}
