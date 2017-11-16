package controller.util.TreeviewItems;

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

  public FrvaTreeMeasurementItem(String name, File file, String id, FrvaModel model, boolean isPreview) {
    super(name);
    this.file = file;
    addListener();
    this.model = model;
    this.id = id;
    if (!isPreview) {
      addListener();

    }

  }

  public FrvaTreeMeasurementItem(String name, MeasureSequence ms, String id, File file, FrvaModel model, boolean isPreview) {
    super(name);
    this.measureSequence = ms;

    this.id = id;
    this.model = model;
    this.file = file;
    if (!isPreview) {
      addListener();
    }

  }

  private void addListener() {
    measureSequence.deletedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
          getParent().getChildren().remove(this);
          System.out.println(newValue + " removed myself from tree");
        }
      }
    });
  }

  private ChangeListener<Boolean> checkedlistener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> observable,
                        Boolean oldValue, Boolean newValue) {

      if (newValue) {
        // System.out.println("added one measuremnt to model");
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
    // System.out.println("here hello 1234 " + file.getPath());
    TreeItem item = this;
    while (!(item instanceof FrvaTreeSdCardItem)) {
      item = item.getParent();
    }
    SdCard containingSdCard = ((FrvaTreeSdCardItem) item).getSdCard();
    this.measureSequence = containingSdCard.readSingleMeasurementSequence(file, id, model);
    addListener();
    return this.measureSequence;
  }

  /*
    private void addListener() {


      super.selectedProperty().addListener(checkedlistener);

      model.getCurrentlySelectedTabProperty().addListener((observable, oldValue, newValue) -> {
        selectedProperty().removeListener(checkedlistener);
        if (model.getCurrentSelectionList().contains(getMeasureSequence())) {
          setSelected(true);
        } else {
          setSelected(false);
        }
        selectedProperty().addListener(checkedlistener);
      });
    }
  */
  public String getId() {
    return this.id;

  }

  private String checkFile() {
    return file.getAbsolutePath();
  }

  public String serialize() {

    return getId() + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + checkFile() + ";";
  }

  @Override
  public void setPathToLibrary() {
    // this.measureSequence.setPathToLibrary();
    this.file = new File(FrvaModel.LIBRARYPATH
        + File.separator + measureSequence.getContainingSdCard().getName()
        + File.separator + file.getParentFile().getName() + File.separator + file.getName());
  }

  @Override
  public void createChildren() {

  }

}
