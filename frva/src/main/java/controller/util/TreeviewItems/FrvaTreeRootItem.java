package controller.util.TreeviewItems;

import java.io.File;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBoxTreeItem;
import model.FrvaModel;
import model.data.MeasureSequence;

/**
 * Created by patrick.wigger on 12.10.17.
 */
public class FrvaTreeRootItem extends FrvaTreeItem {


  public FrvaTreeRootItem(String name) {
    super(name);
  }

  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + "Root" + ";";
  }


  @Override
  public int getDepth() {
    return 0;
  }


  /**
   * Constructor for TreeView lazyLoaded.
   */

/*
  public FrvaTreeRootItem(String[] arr, FrvaModel model, boolean isPreview) {

    this.model = model;
    this.id = arr[0];
    this.depth = Integer.parseInt(arr[1]);
    this.setValue(arr[2]);
    this.file = new File(arr[3]);

    if (!isPreview) {
      addListener();
    }
  }





  /**
   * Constructor of FrvaTreeViewItem.
   *
   * @param name  name of the item.
   * @param ms    measurementsequence, if null it is a category item.
   * @param model the one and only model.
   *
  public FrvaTreeRootItem(String name, MeasureSequence ms, FrvaModel model, File file,
                          boolean isPreview) {
    this.file = file;
    setName(name);
    this.measureSequence = ms;
    setName(name);
    this.model = model;
    this.type = t;
    this.depth = evaluateDepth(t);


    if (!isPreview) {
      addListener();
    }

  }

  public String toString() {
    return super.getValue().toString();
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










  public int getDepth() {
    return 0;
  }

*/

}
