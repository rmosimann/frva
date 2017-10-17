package model;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
}
