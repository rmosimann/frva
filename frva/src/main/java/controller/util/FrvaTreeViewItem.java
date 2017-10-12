package controller.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import model.FrvaModel;
import model.data.MeasureSequence;

/**
 * Created by patrick.wigger on 12.10.17.
 */
public class FrvaTreeViewItem extends CheckBoxTreeItem {
  private MeasureSequence measureSequence;
  private String name;
  private FrvaModel model;

  public FrvaTreeViewItem(FrvaModel model) {
    this.model = model;

  }

  /**
   * Constructor of FrvaTreeViewItem.
   *
   */
  public FrvaTreeViewItem(String name, MeasureSequence ms, FrvaModel model) {
    super.setValue(name);
    this.measureSequence = ms;
    this.name = name;
    this.model = model;


    super.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        model.getCurrentSelectionList().add(measureSequence);
      } else {
        model.getCurrentSelectionList().removeAll(measureSequence);
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
