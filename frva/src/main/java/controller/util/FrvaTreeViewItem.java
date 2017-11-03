package controller.util;

import static controller.util.FrvaTreeViewItem.Type.DEVICE;
import static controller.util.FrvaTreeViewItem.Type.MEASURESEQUENCE;
import static controller.util.FrvaTreeViewItem.Type.MONTH;
import static controller.util.FrvaTreeViewItem.Type.NONE;
import static controller.util.FrvaTreeViewItem.Type.ROOT;
import static controller.util.FrvaTreeViewItem.Type.YEAR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBoxTreeItem;
import model.FrvaModel;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 12.10.17.
 */
public class FrvaTreeViewItem extends CheckBoxTreeItem {
  public enum Type {ROOT, DEVICE, SDCARD, YEAR, MONTH, DAY, HOUR, MEASURESEQUENCE, NONE}

  private MeasureSequence measureSequence;
  private String id;
  private Type type;
  private File file;
  private int depth;
  private FrvaModel model;
  private ChangeListener<Boolean> checkedlistener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> observable,
                        Boolean oldValue, Boolean newValue) {
      if (newValue) {
        if (type==MEASURESEQUENCE) {
          model.getCurrentSelectionList().add(getMeasureSequence());
          System.out.println("jee");
        }
      } else {
        model.getCurrentSelectionList().remove(getMeasureSequence());
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


  public FrvaTreeViewItem(String[] arr, boolean isPreview) {
    this.id = arr[0];
    this.depth = Integer.parseInt(arr[1]);
    this.type = getType(depth);
    this.setValue(arr[2]);
    this.file = new File(arr[3]);

    if (!isPreview) {
      addListener();
    }
  }


  private void addListener() {
    super.selectedProperty().addListener(checkedlistener);
/*
    model.getCurrentlySelectedTabProperty().addListener((observable, oldValue, newValue) -> {
      selectedProperty().removeListener(checkedlistener);

      if (model.getCurrentSelectionList().contains(getMeasureSequence())) {
        setSelected(true);
      } else {
        setSelected(false);
      }
      selectedProperty().addListener(checkedlistener);
    });*/
  }


  /**
   * Constructor of FrvaTreeViewItem.
   *
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
      addListener();
    }

  }

  public String toString() {
    return super.getValue().toString();
  }

  public MeasureSequence getMeasureSequence() {
    if (this.measureSequence != null) {
      return measureSequence;
    }
    ArrayList<String> fileContent = new ArrayList<>();
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(file));) {
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {

          if (Character.isDigit(line.charAt(0))) {
            String currentId = line.split(";")[0];
            if (currentId.equals(id)) {
              fileContent.add(line);
              fileContent.add(br.readLine());
              fileContent.add(br.readLine());
              fileContent.add(br.readLine());
              fileContent.add(br.readLine());
              SdCard sdCard = new SdCard(file.getParentFile().getParentFile(), "SDCard");
              DataFile dataFile = new DataFile(sdCard, file);
              MeasureSequence ms = new MeasureSequence(fileContent, dataFile);
              return ms;
            }
            fileContent.clear();
          }
          fileContent.add(line);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
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

    return getId() + ";" + this.depth + ";" + this.getValue().toString() + ";" + checkFile() + ";";
  }

  private String checkFile() {
    return file == null ? "null" : file.getAbsolutePath();
  }

  public String getId() {
    return measureSequence == null ? "-1" : measureSequence.getId();

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

  public int getDepth() {
    return this.depth;
  }

  private Type getType(int depth) {

    switch (depth) {
      case 0:
        return ROOT;
      case 1:
        return DEVICE;
      case 2:
        return Type.SDCARD;
      case 3:
        return YEAR;
      case 4:
        return MONTH;
      case 5:
        return Type.DAY;
      case 6:
        return Type.HOUR;
      case 7:
        return MEASURESEQUENCE;
      default:
        return NONE;
    }


  }
}
