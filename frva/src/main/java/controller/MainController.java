package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;
import model.FrvaModel;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainController {
  private final FrvaModel model;
  @FXML
  TreeView<String> treeView;


  public MainController(FrvaModel model) {
    this.model = model;
  }

  @FXML
  public void initialize() {

    initializeTree();

  }


  private void initializeTree() {
    //Add testSDCard
    SdCard sdCard = new SdCard(getClass().getResource("/SDCARD"));
    model.addSdCard(sdCard);

    CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>("Library");
    root.setExpanded(true);

    treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());


    //Structurize Data with hours
    for (SdCard card : model.getLibrary()
        ) {
      CheckBoxTreeItem<String> sdCardItem = new CheckBoxTreeItem<>(card.getDeviceSerialNr());
      root.getChildren().add(sdCardItem);
      for (DataFile dataFile : card.getDataFiles()
          ) {
        Iterator it = dataFile.getMeasureSequences().iterator();
        String hour = "";
        int count = 0;
        CheckBoxTreeItem<String> checkBoxTreeHourItem = new CheckBoxTreeItem<>();
        while (it.hasNext()) {
          MeasureSequence measureSequence = (MeasureSequence) it.next();
          String currentHour = measureSequence.getTime().substring(0, 2);
          if (!currentHour.equals(hour)) {
            checkBoxTreeHourItem.setValue(hour + ":00-" + currentHour + ":00 " + "(" + count + ")");
            count = 0;
            hour = currentHour;
            checkBoxTreeHourItem = new CheckBoxTreeItem<String>();
            sdCardItem.getChildren().add(checkBoxTreeHourItem);
          }
          final CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<String>("ID" + measureSequence.getId() + " - " + measureSequence.getTime());
          count++;
          checkBoxTreeHourItem.getChildren().add(checkBoxTreeItem);
        }
        checkBoxTreeHourItem.setValue(hour + ":00-" + (Integer.parseInt(hour) + 1) + ":00" + "(" + count + ")");
      }

    }
    treeView.setRoot(root);
  }
}
