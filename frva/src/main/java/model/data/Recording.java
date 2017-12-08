package model.data;

import java.io.File;

/**
 * Created by patrick.wigger on 07.12.17.
 */
public class Recording extends SdCard {
  /**
   * Constructor.
   *
   * @param sdCardPath a Path where the data lays as expected.
   * @param name       the Name of that SDCARD.
   */
  public Recording(File sdCardPath, String name) {
    super(sdCardPath, name);
  }



}
