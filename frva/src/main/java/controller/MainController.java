package controller;

import controller.util.ImportWizard;
import controller.util.treeviewitems.FrvaTreeDeviceItem;
import controller.util.treeviewitems.FrvaTreeItem;
import controller.util.treeviewitems.FrvaTreeMeasurementItem;
import controller.util.treeviewitems.FrvaTreeRootItem;
import controller.util.treeviewitems.FrvaTreeSdCardItem;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import model.FrvaModel;
import model.data.FileInOut;
import model.data.MeasureSequence;
import model.data.SdCard;
import org.controlsfx.control.CheckTreeView;


public class MainController {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private int newTabId = 0;
  private ListChangeListener treeViewListener;

  @FXML
  private CheckTreeView treeView;
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
  private Button importSdCardButton;
  @FXML
  private Button deleteSelectedItemsButton;
  @FXML
  private Button exportButton;

  public MainController(FrvaModel model) {
    this.model = model;
    logger.info("Created MainController");
  }


  @FXML
  private void initialize() {
    initializeTabHandling();
    loadTreeStructure();
    addEventHandlers();
  }


  private void addEventHandlers() {
    expandAllButton.setOnAction(event -> expandAll(treeView.getRoot()));
    collapseAllButton.setOnAction(event -> collapseAll(treeView.getRoot()));
    selectAllButton.setOnAction(event -> ((FrvaTreeItem) treeView.getRoot()).setSelected(true));
    selectNoneButton.setOnAction(event -> unselectTickedItems());
    activateMultiSelect();
    deleteSelectedItemsButton.setOnAction(event -> deleteSelectedItems());
    exportButton.setOnAction(event -> exportData());
    importSdCardButton.setOnAction(event -> importWizard());

  }

  private void importWizard() {
    ImportWizard importWizard = new ImportWizard(importSdCardButton.getScene().getWindow(), model);
    List<MeasureSequence> list = importWizard.startImport();
    List<SdCard> importedSdCards = FileInOut
        .createFiles(list, new File(FrvaModel.LIBRARYPATH).toPath());
    for (SdCard sdCard : importedSdCards) {
      FileInOut.writeDatabaseFile(sdCard);
      model.getLibrary().add(sdCard);
    }
    ((FrvaTreeRootItem) treeView.getRoot()).createChildren(importedSdCards, false);
  }


  /**
   * Exports Data to a specific folder.
   */
  public void exportData() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select export path");
    File selectedFile = directoryChooser.showDialog(exportButton.getScene().getWindow());
    if (selectedFile != null) {
      FileInOut.createFiles(model.getCurrentSelectionList(), selectedFile.toPath());
    }
    //TODO get this working on Linux
    //    if (Desktop.isDesktopSupported()) {
    //      try {
    //        Desktop.getDesktop().open(selectedFile);
    //      } catch (IOException e) {
    //        logger.info(e.getMessage());
    //      }
    //    }
  }


  private void deleteSelectedItems() {
    List<FrvaTreeItem> list = treeView.getCheckModel().getCheckedItems();
    if (confirmDelete(list.stream().filter(p -> p instanceof FrvaTreeMeasurementItem).count())) {
      List<MeasureSequence> measurements = new ArrayList<>();
      for (FrvaTreeItem item : list) {
        if (item instanceof FrvaTreeMeasurementItem) {
          measurements.add(((FrvaTreeMeasurementItem) item).getMeasureSequence());
          item.removeMeasureSequence();
        }

      }
      unselectTickedItems(treeView.getRoot());
      model.deleteMeasureSequences(measurements);
    }


  }


  private boolean confirmDelete(long amount) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Warning");
    alert.setHeaderText(amount + " Measurements are going to be deleted.");
    alert.setContentText("This action cannot be undone \nDo you want to continue?");
    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() ? result.get() == ButtonType.OK : false;
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
    model.getCurrentlySelectedTabProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                          Number newValue) {
        treeView.getCheckModel().getCheckedItems().removeListener(treeViewListener);
        unselectTickedItems(treeView.getRoot());
        for (MeasureSequence ms : model.getCurrentSelectionList()) {
          checkTreeItemWithMeasurement((FrvaTreeItem) treeView.getRoot(), ms);

        }
        treeView.getCheckModel().getCheckedItems().addListener(treeViewListener);
      }
    });
  }

  /**
   * Adds a new Tab to the DataHandlingView.
   */
  private void addTab() {
    //Create Tab and set defaults
    Tab newtab = createEditableTab("Untitled " + (newTabId));
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
      loader.setController(new TabController(model, newTabId, this));
      newtab.setContent(loader.load());
    } catch (IOException e) {
      e.printStackTrace();
    }

    tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
    model.setCurrentlySelectedTab(tabPane.getTabs().size() - 2);

    newTabId++;
  }

  private void loadTreeStructure() {
    treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
    FrvaTreeRootItem root = new FrvaTreeRootItem("empty Library");
    root.createChildren(model.getLibrary(), false);
    treeView.setRoot(root);

    model.getCurrentlySelectedTabProperty().addListener(
        (observable, oldValue, newValue) -> treeView.getSelectionModel().clearSelection());
    treeViewListener = new ListChangeListener() {
      @Override
      public void onChanged(Change c) {
        while (c.next()) {
          if (c.wasAdded()) {
            c.getAddedSubList().forEach(new Consumer() {
              @Override
              public void accept(Object o) {
                if (o instanceof FrvaTreeMeasurementItem) {
                  model.getCurrentSelectionList()
                      .add(((FrvaTreeMeasurementItem) o).getMeasureSequence());
                }
              }
            });
          } else {
            c.getRemoved().forEach(new Consumer() {
              @Override
              public void accept(Object o) {
                if (o instanceof FrvaTreeMeasurementItem) {
                  model.getCurrentSelectionList()
                      .remove(((FrvaTreeMeasurementItem) o).getMeasureSequence());
                }
              }
            });
          }
        }
      }
    };
    //TODO Deregister Listener?
    treeView.getCheckModel().getCheckedItems().addListener(treeViewListener);
    treeView.getRoot().getChildren().addListener(new ListChangeListener() {
      @Override
      public void onChanged(Change c) {
        c.next();
        if (c.getList().size() == 0) {
          treeView.setShowRoot(true);
        } else {
          treeView.setShowRoot(false);
        }
      }
    });
    if (treeView.getRoot().getChildren().size() != 0) {
      treeView.setShowRoot(false);
    }


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


  /**
   * Deselects all Measurements in the TreeView.
   */
  public void unselectTickedItems() {
    if (treeView.getSelectionModel().getSelectedItems().size() > 1) {
      treeView.getSelectionModel().getSelectedItems().forEach(item ->
          ((FrvaTreeItem) item).setSelected(false));
    } else {
      unselectTickedItems(treeView.getRoot());
    }
    treeView.getSelectionModel().clearSelection();
  }

  private void unselectTickedItems(TreeItem<FrvaTreeItem> item) {
    if (!item.isLeaf()) {
      for (Object child : item.getChildren()) {
        unselectTickedItems((TreeItem) child);
        ((FrvaTreeItem) child).setSelected(false);
        ((FrvaTreeItem) child).setIndeterminate(false);
      }
    }
  }

  private void activateMultiSelect() {
    treeView.setOnMouseClicked(event -> {
      treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      treeView.getSelectionModel().getSelectedItems().forEach(item ->
          ((FrvaTreeItem) item).setSelected(true));
    });
  }


  private void checkTreeItemWithMeasurement(FrvaTreeItem item, MeasureSequence ms) {
    if (item instanceof FrvaTreeMeasurementItem) {
      if (((FrvaTreeMeasurementItem) item).getMeasureSequence().equals(ms)) {
        ((FrvaTreeMeasurementItem) item).setSelected(true);
      }
    } else {
      for (Object child : item.getChildren()) {
        ;
        checkTreeItemWithMeasurement(((FrvaTreeItem) child), ms);
      }
    }

  }

  /**
   * Reads in new LiveMeasurement in Treeview.
   */

  public void refreshTreeView() {
    if (model.getCurrentLiveSdCardPath() != null) {
      model.getCurrentLiveSdCardPath();
      File dbFile = new File(model
          .getCurrentLiveSdCardPath().getPath() + File.separator + "db.csv");
      if (dbFile.exists()) {
        dbFile.delete();
      }

      SdCard sdCard = new SdCard(model.getCurrentLiveSdCardPath(), null);
      List<SdCard> list = new ArrayList<>();
      list.add(sdCard);

      removeTreeItem(treeView.getRoot());

      ((FrvaTreeRootItem) treeView.getRoot()).createChildren(list, true);
    }
  }


  private void removeTreeItem(TreeItem item) {
    final FrvaTreeSdCardItem[] returnValue = {null};
    item.getChildren().forEach(new Consumer() {
      @Override
      public void accept(Object device) {
        ((FrvaTreeDeviceItem) device).getChildren().forEach(new Consumer() {
          @Override
          public void accept(Object sdCard) {
            System.out.println(((FrvaTreeSdCardItem) sdCard).getSdCard().getSdCardFile().getPath()
            );
            System.out.println(model.getCurrentLiveSdCardPath().getPath());
            if (((FrvaTreeSdCardItem) sdCard).getSdCard().getSdCardFile().getPath()
                .equals(model.getCurrentLiveSdCardPath().getPath())) {
              returnValue[0] = (FrvaTreeSdCardItem) sdCard;
            }
          }
        });
      }
    });
    if (returnValue[0] != null) {
      returnValue[0].setSelected(false);
      returnValue[0].getParent().getChildren().remove(returnValue[0]);
    }
  }


  private Tab createEditableTab(String text) {
    final Label label = new Label(text);
    final Tab tab = new Tab();
    tab.setGraphic(label);
    final TextField textField = new TextField();
    label.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (event.getClickCount() == 2) {
          textField.setText(label.getText());
          tab.setGraphic(textField);
          textField.selectAll();
          textField.requestFocus();
        }
      }
    });

    textField.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        label.setText(textField.getText());
        tab.setGraphic(label);
      }
    });

    textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable,
                          Boolean oldValue, Boolean newValue) {
        if (!newValue) {
          label.setText(textField.getText());
          tab.setGraphic(label);
        }
      }
    });
    return tab;
  }
}
