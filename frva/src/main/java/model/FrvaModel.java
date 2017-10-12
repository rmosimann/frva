package model;

import java.util.ArrayList;
import java.util.List;

import model.data.SdCard;

public class FrvaModel {
  private final String applicationName = "FRVA";

  private final List<SdCard> library = new ArrayList<>();

  public FrvaModel() {
    library.add(new SdCard(getClass().getResource("../SDCARD")));
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

}
