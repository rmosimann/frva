package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;
import model.FrvaModel;

public class MainController {
  private final FrvaModel model;
  @FXML
  TreeView<String> treeView;


  public MainController(FrvaModel model) {
    this.model = model;
  }
  @FXML
  public void initialize(){


    CheckBoxTreeItem<String> rootItem =
            new CheckBoxTreeItem<String>("View Source Files");
    rootItem.setExpanded(true);
    
    treeView.setEditable(true);

    treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
    for (int i = 0; i < 8; i++) {
      final CheckBoxTreeItem<String> checkBoxTreeItem =
              new CheckBoxTreeItem<String>("Sample" + (i+1));
      rootItem.getChildren().add(checkBoxTreeItem);
    }

    treeView.setRoot(rootItem);





  }


}
