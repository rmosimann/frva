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
  MeasureSequence measureSequence;
  String name;
  FrvaModel model;

  public FrvaTreeViewItem(FrvaModel model) {
    this.model = model;
  }

  public FrvaTreeViewItem(String name, MeasureSequence ms, FrvaModel model) {
    setValue(name);
    this.measureSequence = ms;
    this.name = name;
    this.model = model;

    super.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
          model.getCurrentSelectionList().add(measureSequence);
        } else {
          model.getCurrentSelectionList().removeAll(measureSequence);
        }
      }
    });
  }

  public String toString() {
    return name;
  }

  public MeasureSequence getMeasureSequence() {
    return this.measureSequence;
  }

  public void setValue(String name, MeasureSequence measureSequence) {
    setValue(name);
    this.name = name;
    this.measureSequence = measureSequence;
  }
}
