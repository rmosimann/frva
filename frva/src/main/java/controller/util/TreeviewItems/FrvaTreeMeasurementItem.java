package controller.util.TreeviewItems;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import model.FrvaModel;
import model.data.MeasureSequence;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeMeasurementItem extends FrvaTreeItem {


  private MeasureSequence measureSequence;
  private String id;
  private File file;
  private FrvaModel model;

  public FrvaTreeMeasurementItem(String name, File file) {
    super(name);
    this.file = file;
  }

  private ChangeListener<Boolean> checkedlistener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> observable,
                        Boolean oldValue, Boolean newValue) {
      if (newValue) {
        model.getCurrentSelectionList().add(getMeasureSequence());
      } else {
        model.getCurrentSelectionList().removeAll(getMeasureSequence());
      }
    }
  };

  @Override
  public int getDepth() {
    return 7;
  }


  /**
   * @returns the measurementsequence lazyloaded: read in from the file system if it has not already been read in.
   */
  public MeasureSequence getMeasureSequence() {
    if (this.measureSequence != null) {
      return measureSequence;
    }
    return new MeasureSequence(file, id, model);
  }


  private void addListener() {


    super.selectedProperty().addListener(checkedlistener);

    model.getCurrentlySelectedTabProperty().addListener((observable, oldValue, newValue) -> {
      selectedProperty().removeListener(checkedlistener);

      if (model.getCurrentSelectionList().stream().anyMatch(p -> p.getId().equals(id))) {
        setSelected(true);
      } else {
        setSelected(false);
      }
      selectedProperty().addListener(checkedlistener);
    });
  }

  public String getId() {
    return measureSequence == null ? "-1" : measureSequence.getId();

  }

  private String checkFile() {
    return file == null ? "null" : file.getAbsolutePath();
  }

  public String serialize() {

    return getId() + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + checkFile() + ";";
  }

}
