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
import java.util.List;

public class MainController {
  private final FrvaModel model;
  @FXML
  TreeView<String> treeView;


  public MainController(FrvaModel model) {
    this.model = model;
  }
  @FXML
  public void initialize(){

    initializeTree();

  }



  private void initializeTree(){
    //Add testSDCard
    SdCard sdCard=new SdCard(getClass().getResource("/SDCARD"));
    model.addSdCard(sdCard);

    CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>("Library");
    root.setExpanded(true);

    treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

    for (SdCard card : model.getLibrary()
         ) {
      for (DataFile dataFile : card.getDataFiles()
              ) {
        for (MeasureSequence measureSequence : dataFile.getMeasureSequences()) {
          final CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<String>("ID"+measureSequence.getId()+" - "+measureSequence.getTime());
          root.getChildren().add(checkBoxTreeItem);
        }

      }

    }
    treeView.setRoot(root);
  }
}
