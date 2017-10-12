package controller;

import java.io.IOException;

import controller.util.FrvaTreeViewItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import model.FrvaModel;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;

import java.util.Iterator;


public class MainController {
  private final FrvaModel model;


  public MainController(FrvaModel model) {
    this.model = model;
  }

  @FXML
  TreeView<FrvaTreeViewItem> treeView;

  @FXML
  private TabPane tabPane;

  @FXML
  private void initialize() {
    initializeTabHandling();
    initializeTree();
  }

  private void initializeTabHandling() {
    Tab tab = new Tab("+");
    tab.closableProperty().set(false);
    tabPane.getTabs().add(tab);
    addTab();

    tabPane.getSelectionModel().selectedIndexProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (newValue.intValue() == tabPane.getTabs().size() - 1) {
            addTab();
          }
        });
  }

  private void addTab() {
    int numTabs = tabPane.getTabs().size();
    Tab newtab = new Tab("Untitled " + (numTabs + 1));
    newtab.closableProperty().setValue(true);
    newtab.setOnCloseRequest(event -> {
      if (tabPane.getTabs().size() == 2) {
        addTab();
      }
    });


    tabPane.getTabs().add(tabPane.getTabs().size() - 1, newtab);
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/tabContent.fxml"));
      loader.setController(new TabController(model));
      Node node = (Node) loader.load();
      newtab.setContent(node);
    } catch (IOException e) {
      e.printStackTrace();
    }
    tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
  }


  private void initializeTree() {

    CheckBoxTreeItem<FrvaTreeViewItem> root = new CheckBoxTreeItem<>(new FrvaTreeViewItem("Library", null));
    root.setExpanded(true);

    treeView.setCellFactory(CheckBoxTreeCell.<FrvaTreeViewItem>forTreeView());


    //Structurize Data with hours/days
    for (SdCard card : model.getLibrary()
        ) {
      CheckBoxTreeItem<FrvaTreeViewItem> sdCardItem = new CheckBoxTreeItem<FrvaTreeViewItem>(new FrvaTreeViewItem(card.getDeviceSerialNr(), null));
      root.getChildren().add(sdCardItem);
      for (DataFile dataFile : card.getDataFiles()
          ) {
        Iterator it = dataFile.getMeasureSequences().iterator();
        String hour = "";
        String date = "000000";
        int hourlyCount = 0;
        int dailyCount = 0;
        CheckBoxTreeItem<FrvaTreeViewItem> checkBoxTreeHourItem = new CheckBoxTreeItem<>();
        CheckBoxTreeItem<FrvaTreeViewItem> checkBoxTreeDateItem = new CheckBoxTreeItem<>();


        while (it.hasNext()) {
          MeasureSequence measureSequence = (MeasureSequence) it.next();
          String currentHour = measureSequence.getTime().substring(0, 2);
          String currentDate = measureSequence.getDate();

          if (!currentDate.equals(date)) {
            checkBoxTreeDateItem.setValue(new FrvaTreeViewItem(date + " (" + dailyCount + ")", null));
            dailyCount = 0;
            date = currentDate;
            hour = "";
            checkBoxTreeDateItem = new CheckBoxTreeItem<>();
            sdCardItem.getChildren().add(checkBoxTreeDateItem);
          }

          if (!currentHour.equals(hour)) {
            checkBoxTreeHourItem.setValue(new FrvaTreeViewItem(hour + ":00-" + currentHour + ":00 " + "(" + hourlyCount + ")", null));
            hourlyCount = 0;
            hour = currentHour;
            checkBoxTreeHourItem = new CheckBoxTreeItem<>();
            checkBoxTreeDateItem.getChildren().add(checkBoxTreeHourItem);
          }

          CheckBoxTreeItem<FrvaTreeViewItem> checkBoxTreeMeasurementItem = new CheckBoxTreeItem<>(new FrvaTreeViewItem("ID" + measureSequence.getId() + " - " + measureSequence.getTime(), measureSequence));
          hourlyCount++;
          dailyCount++;
          checkBoxTreeHourItem.getChildren().add(checkBoxTreeMeasurementItem);
        }
        checkBoxTreeHourItem.setValue(new FrvaTreeViewItem(hour + ":00-" + (Integer.parseInt(hour) + 1) + ":00" + " (" + hourlyCount + ")", null));
        checkBoxTreeDateItem.setValue(new FrvaTreeViewItem(date + " (" + dailyCount + ")", null));

      }

    }
    treeView.setRoot(root);

  }



}
