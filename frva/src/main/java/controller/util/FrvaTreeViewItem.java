package controller.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBoxTreeItem;
import model.FrvaModel;
import model.data.MeasureSequence;

/**
 * Created by patrick.wigger on 12.10.17.
 */
public class FrvaTreeViewItem extends CheckBoxTreeItem {
  private MeasureSequence measureSequence;
  private String name;
  private FrvaModel model;
  ChangeListener<Boolean> checkedlistener = new ChangeListener<Boolean>() {
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


  public FrvaTreeViewItem(FrvaModel model) {
    this.model = model;

  }

  /**
   * Constructor of FrvaTreeViewItem.
   */
  public FrvaTreeViewItem(String name, MeasureSequence ms, FrvaModel model) {
    setValue(name);
    this.measureSequence = ms;
    this.name = name;
    this.model = model;

    super.selectedProperty().addListener(checkedlistener);


    model.getCurrentlySelectedTabProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable,
                          Number oldValue, Number newValue) {
        selectedProperty().removeListener(checkedlistener);

        if (model.getCurrentSelectionList().contains((MeasureSequence) measureSequence)) {
          setSelected(true);
        } else {
          setSelected(false);
        }

        selectedProperty().addListener(checkedlistener);

      }
    });
  }

  public String toString() {
    return name;
  }

  public MeasureSequence getMeasureSequence() {
    return this.measureSequence;
  }

  public void setValue(String name) {
    super.setValue(name);
    this.name = name;
  }

  public void setExpand(boolean value) {
    super.setExpanded(value);
  }
}
