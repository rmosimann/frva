package controller;

import controller.util.FrvaTreeViewItem;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import model.FrvaModel;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;


public class MainController {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private int newTabId = 0;


  public MainController(FrvaModel model) {
    this.model = model;
    logger.info("Created MainController");
  }

  @FXML
  private TreeView<FrvaTreeViewItem> treeView;
  @FXML
  private Button selectAllButton;
  @FXML
  private Button selectNoneButton;
  @FXML
  private Button collapseAllButton;
  @FXML
  private Button expandAllButton;
  @FXML
  private TabPane tabPane;


  @FXML
  private void initialize() {
    initializeTabHandling();
    initializeTree();
    addEventHandlers();
  }

  private void addEventHandlers() {
    expandAllButton.setOnAction(event -> expandAll(treeView.getRoot()));
    collapseAllButton.setOnAction(event -> collapseAll(treeView.getRoot()));
    selectAllButton.setOnAction(event -> ((FrvaTreeViewItem) treeView.getRoot()).setSelected(true));
    selectNoneButton.setOnAction(event -> selectNone());
    activateMultiSelect();
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
          } else {
            model.setCurrentlySelectedTab((Integer) newValue);
          }
        });
  }


  /**
   * Adds a new Tab to the DataHandlingView.
   */
  private void addTab() {
    //Create Tab and set defaults
    Tab newtab = new Tab("Untitled " + (newTabId));
    newtab.closableProperty().setValue(true);
    newtab.setId(String.valueOf(newTabId));
    newtab.setOnCloseRequest(event -> {
      model.removeSelectionMapping(newTabId);
      if (tabPane.getTabs().size() == 2) {
        addTab();
      }
    });
    model.addSelectionMapping(newTabId);
    tabPane.getTabs().add(tabPane.getTabs().size() - 1, newtab);
    newTabId++;

    //load view and controller
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/tabContent.fxml"));
      loader.setController(new TabController(model));
      newtab.setContent(loader.load());
    } catch (IOException e) {
      e.printStackTrace();
    }

    tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
    model.setCurrentlySelectedTab(tabPane.getTabs().size() - 2);
  }

  private void initializeTree() {
    FrvaTreeViewItem root = new FrvaTreeViewItem("Library", null, model);
    treeView.setCellFactory(CheckBoxTreeCell.forTreeView());


    //Structurize Data with days/hours
    for (SdCard card : model.getLibrary()) {
      FrvaTreeViewItem sdCardItem = new FrvaTreeViewItem(card.getDeviceSerialNr(), null, model);
      root.getChildren().add(sdCardItem);
      for (DataFile dataFile : card.getDataFiles()) {
        Iterator it = dataFile.getMeasureSequences().iterator();
        String hour = "";
        String date = "000000";
        boolean continueToNextDay = false;
        int hourlyCount = 0;
        int dailyCount = 0;
        FrvaTreeViewItem checkBoxTreeHourItem = new FrvaTreeViewItem(model);
        FrvaTreeViewItem checkBoxTreeDateItem = new FrvaTreeViewItem(model);


        while (it.hasNext()) {
          MeasureSequence measureSequence = (MeasureSequence) it.next();
          String currentHour = measureSequence.getTime().substring(0, 2);
          String currentDate = measureSequence.getDate();

          if (!currentDate.equals(date)) {
            checkBoxTreeDateItem.setValue(date + " (" + dailyCount + ")");
            dailyCount = 0;
            date = currentDate;
            continueToNextDay = true;
            checkBoxTreeDateItem = new FrvaTreeViewItem(model);
            sdCardItem.getChildren().add(checkBoxTreeDateItem);
          }

          if (!currentHour.equals(hour) || continueToNextDay) {
            continueToNextDay = false;
            checkBoxTreeHourItem.setValue(hour + ":00-" + currentHour + ":00 "
                + "(" + hourlyCount + ")");
            hourlyCount = 0;
            hour = currentHour;
            checkBoxTreeHourItem = new FrvaTreeViewItem(model);
            checkBoxTreeDateItem.getChildren().add(checkBoxTreeHourItem);
          }

          FrvaTreeViewItem checkBoxTreeMeasurementItem = new FrvaTreeViewItem("ID"
              + measureSequence.getId() + " - " + measureSequence.getTime(), measureSequence,
              model);
          hourlyCount++;
          dailyCount++;
          checkBoxTreeHourItem.getChildren().add(checkBoxTreeMeasurementItem);
        }
        checkBoxTreeHourItem.setValue(hour + ":00-" + (Integer.parseInt(hour) + 1) + ":00"
            + " (" + hourlyCount + ")");
        checkBoxTreeDateItem.setValue(date + " (" + dailyCount + ")");
      }
    }
    treeView.setRoot(root);
    treeView.setShowRoot(false);
  }


  private void expandAll(TreeItem item) {
    if (!item.isLeaf()) {
      item.setExpanded(true);
      for (Object child : item.getChildren()) {
        expandAll((TreeItem) child);
      }
    }
  }


  private void collapseAll(TreeItem item) {
    if (!item.isLeaf()) {
      if (item == treeView.getRoot()) {
        item.setExpanded(true);
      } else {
        item.setExpanded(false);
      }
      for (Object child : item.getChildren()) {
        collapseAll((TreeItem) child);
      }
    }
  }


  private void selectNone() {
    if (treeView.getSelectionModel().getSelectedItems().size() > 1) {
      treeView.getSelectionModel().getSelectedItems().forEach(item ->
          ((FrvaTreeViewItem) item).setSelected(false));
    } else {
      ((FrvaTreeViewItem) treeView.getRoot()).setSelected(true);
      ((FrvaTreeViewItem) treeView.getRoot()).setSelected(false);
    }
    treeView.getSelectionModel().clearSelection();
  }


  private void activateMultiSelect() {
    treeView.setOnMouseClicked(event -> {
      treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      treeView.getSelectionModel().getSelectedItems().forEach(item ->
          ((FrvaTreeViewItem) item).setSelected(true));
    });
  }
}
