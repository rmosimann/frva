package controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.DirectoryChooser;
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
  TreeView<String> treeView;

  @FXML
  private TabPane tabPane;

  @FXML
  private Button importSdCardButton;

  @FXML
  void importSdCard(ActionEvent event) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Open Resource File");
    File selectedFile = directoryChooser.showDialog(importSdCardButton.getScene().getWindow());
    try {
      model.addSdCard(new SdCard(selectedFile.toURI().toURL()));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    initializeTree();
  }

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
    newtab.setOnCloseRequest(event -> {
      model.removeSelectionMapping(newTabId);
      if (tabPane.getTabs().size() == 2) {
        addTab();
      }
    });
    model.addSelectionMapping(newTabId);
    tabPane.getTabs().add(tabPane.getTabs().size() - 1, newtab);

    //load view and controller
    try {
      FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemClassLoader()
          .getResource("view/tabContent.fxml"));
      loader.setController(new TabController(model, newTabId));
      newtab.setContent((Node) loader.load());
    } catch (IOException e) {
      e.printStackTrace();
    }

    tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
    model.setCurrentlySelectedTab(tabPane.getTabs().size() - 2);

    newTabId++;
  }


  private void initializeTree() {
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
