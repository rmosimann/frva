package controller;

import controller.util.ImportWizard;
import controller.util.TreeViewFactory;
import controller.util.treeviewitems.FrvaTreeItem;
import controller.util.treeviewitems.FrvaTreeMeasurementItem;
import controller.util.treeviewitems.FrvaTreeRootItem;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.DirectoryChooser;
import model.FrvaModel;
import model.data.MeasureSequence;
import model.data.SdCard;
import org.controlsfx.control.CheckTreeView;


public class MainController {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private int newTabId = 0;

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
    //onChangeTab();
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
    List<SdCard> importedSdCards = model
        .createFiles(list, new File(FrvaModel.LIBRARYPATH).toPath());
    for (SdCard sdCard : importedSdCards) {
      sdCard.serialize();
      model.getLibrary().add(sdCard);
    }
    loadTreeStructure();
  }


  /**
   * Exports Data to a specific folder.
   */
  public void exportData() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select export path");
    File selectedFile = directoryChooser.showDialog(exportButton.getScene().getWindow());
    if (selectedFile != null) {
      model.createFiles(model.getCurrentSelectionList(), selectedFile.toPath());
    }
    //TODO get this working on Linux
    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().open(new File(model.LIBRARYPATH));
      } catch (IOException e) {
        logger.info(e.getMessage());
      }
    }
  }


  private void deleteSelectedItems() {
    List<FrvaTreeItem> list = treeView.getCheckModel().getCheckedItems();
    if (confirmDelete(list.size())) {
      List<MeasureSequence> measurements = new ArrayList<>();
      for (FrvaTreeItem item : list) {
        if (item instanceof FrvaTreeMeasurementItem) {
          measurements.add(((FrvaTreeMeasurementItem) item).getMeasureSequence());
        }
      }

      model.deleteMeasureSequences(measurements);
      treeView.getCheckModel().clearChecks();
    }
  }


  private boolean confirmDelete(long amount) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Warning");
    alert.setHeaderText(amount + " Measurements are going to be deleted.");
    alert.setContentText("This action cannot be undone \nDo you want to continue?");
    Optional<ButtonType> result = alert.showAndWait();
    return result.get() == ButtonType.OK;
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
      newtab.setContent(loader.load());
    } catch (IOException e) {
      e.printStackTrace();
    }

    tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
    model.setCurrentlySelectedTab(tabPane.getTabs().size() - 2);

    newTabId++;
  }

  private void loadTreeStructure() {
    treeView.setRoot(new FrvaTreeRootItem("Library"));
    treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
    for (SdCard sdCard : model.getLibrary()) {
      TreeViewFactory.extendTreeView(sdCard, treeView, model, false);
    }
    model.getCurrentlySelectedTabProperty().addListener(
        (observable, oldValue, newValue) -> treeView.getSelectionModel().clearSelection());

    treeView.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
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
    });

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


  private void unselectTickedItems() {
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

  private List<FrvaTreeItem> removeTickedMeasurements(TreeItem item,
                                                      List<FrvaTreeItem> list) {
    if (!item.isLeaf()) {
      for (Object o : item.getChildren()) {
        FrvaTreeItem element = (FrvaTreeItem) o;
        removeTickedMeasurements(element, list);
        if (element.isSelected()) {
          list.add(element);
        }
      }
    }
    return list;
  }
}
