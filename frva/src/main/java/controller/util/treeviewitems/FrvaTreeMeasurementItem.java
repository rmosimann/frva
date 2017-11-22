package controller.util.treeviewitems;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
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

  /**
   * Constructor.
   *
   * @param name      name.
   * @param ms        measurementSequence.
   * @param model     the one and only model.
   * @param isPreview true when only used for preview.
   */
  public FrvaTreeMeasurementItem(String name, MeasureSequence ms,
                                 FrvaModel model, boolean isPreview) {
    super(name);
    this.measureSequence = ms;
    this.id = ms.getId();
    this.model = model;
    this.file = ms.getDataFile().getOriginalFile();
    if (!isPreview) {
      addListener();
    }
  }

  private void addListener() {
    measureSequence.deletedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable,
                          Boolean oldValue, Boolean newValue) {
        if (newValue) {
          getParent().getChildren().remove(FrvaTreeMeasurementItem.this);
        }
      }
    });
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


  /**
   * Getter for the Measurementsequence.
   *
   * @return the measurementsequence lazyloaded: read in from the file system.
   */
  public MeasureSequence getMeasureSequence() {
    if (this.measureSequence != null) {
      return measureSequence;
    }
    TreeItem item = this;
    while (!(item instanceof FrvaTreeSdCardItem)) {
      item = item.getParent();
    }
    SdCard containingSdCard = ((FrvaTreeSdCardItem) item).getSdCard();
    this.measureSequence = containingSdCard.readSingleMeasurementSequence(file, id, model);
    addListener();
    return this.measureSequence;
  }


  public String getId() {
    return this.id;
  }

  private String checkFile() {
    return file.getAbsolutePath();
  }

  @Override
  public void createChildren() {
  }

}
