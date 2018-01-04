package controller.util.treeviewitems;

import java.io.File;
import model.data.MeasureSequence;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeMeasurementItem extends FrvaTreeItem {

  private MeasureSequence measureSequence;
  private String id;
  private File file;

  /**
   * Constructor.
   *
   * @param name      name.
   * @param ms        measurementSequence.
   */
  public FrvaTreeMeasurementItem(String name, MeasureSequence ms) {
    super(name);
    this.measureSequence = ms;
    this.id = ms.getId();
    this.file = ms.getDataFile().getOriginalFile();

  }



  /**
   * Getter for the Measurementsequence.
   *
   * @return the measurementsequence lazyloaded: read in from the file system.
   */
  public MeasureSequence getMeasureSequence() {
    if (this.measureSequence != null) {
      return measureSequence;
    } else {
      return null;
    }
  }


  public String getId() {
    return this.id;
  }

  private String checkFile() {
    return file.getAbsolutePath();
  }


}
