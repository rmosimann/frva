package controller.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBoxTreeItem;
import model.FrvaModel;
import model.data.MeasureSequence;

/**
 * Created by patrick.wigger on 12.10.17.
 */
public class FrvaTreeViewItem extends CheckBoxTreeItem {
  public enum Type { ROOT, DEVICE, SDCARD, YEAR, MONTH, DAY, HOUR, MEASURESEQUENCE }

  private MeasureSequence measureSequence;
  private Type type;
  private File file;
  private int depth;
  private FrvaModel model;
  private ChangeListener<Boolean> checkedlistener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> observable,
                        Boolean oldValue, Boolean newValue) {
      if (newValue) {
        if (measureSequence != null) {
          model.getCurrentSelectionList().add(measureSequence);
        }
      } else {
        model.getCurrentSelectionList().removeAll(measureSequence);
      }
    }
  };


  /**
   * Default-Constructor.
   */
  public FrvaTreeViewItem(Type t) {
    this.type = t;
    this.depth = evaluateDepth(t);
  }

  private int evaluateDepth(Type t) {
    switch (t) {
      case ROOT:
        return 0;
      case DEVICE:
        return 1;
      case SDCARD:
        return 2;
      case YEAR:
        return 3;
      case MONTH:
        return 4;
      case DAY:
        return 5;
      case HOUR:
        return 6;
      case MEASURESEQUENCE:
        return 7;
      default:
        return -1;
    }
  }

  /**
   * Constructor of FrvaTreeViewItem.
   * @param name  name of the item.
   * @param ms    measurementsequence, if null it is a category item.
   * @param model the one and only model.
   */
  public FrvaTreeViewItem(String name, MeasureSequence ms, FrvaModel model, Type t, File file,
                          boolean isPreview) {
    this.file = file;
    setName(name);
    this.measureSequence = ms;
    setName(name);
    this.model = model;
    this.type = t;
    this.depth = evaluateDepth(t);

    if (!isPreview) {
      super.selectedProperty().addListener(checkedlistener);


      model.getCurrentlySelectedTabProperty().addListener((observable, oldValue, newValue) -> {
        selectedProperty().removeListener(checkedlistener);

        if (model.getCurrentSelectionList().contains((MeasureSequence) measureSequence)) {
          setSelected(true);
        } else {
          setSelected(false);
        }

        selectedProperty().addListener(checkedlistener);

      });
    }
  }

  public String toString() {
    return super.getValue().toString();
  }

  public MeasureSequence getMeasureSequence() {
    return this.measureSequence;
  }

  public void setName(String name) {
    super.setValue(name);

  }

  public void setExpand(boolean value) {
    super.setExpanded(value);
  }


  public BooleanProperty getCheckedProperty() {
    return super.selectedProperty();
  }

  public String getDeviceId() {
    return getDeviceId(this);
  }

  private String getDeviceId(FrvaTreeViewItem item) {
    while (!item.isLeaf()) {
      item = (FrvaTreeViewItem) item.getChildren().stream().findAny().get();
    }
    return item.measureSequence.getDataFile().getSdCard().getDeviceSerialNr();
  }

  public String serialize() {

    return this.depth + ";" + this.getValue().toString() + ";" + checkFile() + ";";
  }

  private String checkFile() {
    return file==null ? "null":file.getAbsolutePath();
  }



}
