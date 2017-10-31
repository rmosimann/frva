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
  public enum Type { ROOT, DEVICE, SDCARD, FILE, DATE, HOUR, MEASRURESEQUENCE }

  private MeasureSequence measureSequence;
  private Type type;
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
  }


  /**
   * Constructor of FrvaTreeViewItem. Does add a listener to its checked State and adds its
   * measure sequence to the list of checked measure sequcences in the model.
   */
  public FrvaTreeViewItem(String name, MeasureSequence ms, FrvaModel model, Type t,
                          boolean isPreview) {
    setName(name);
    this.measureSequence = ms;
    setName(name);
    this.model = model;
    this.type = t;

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

}
