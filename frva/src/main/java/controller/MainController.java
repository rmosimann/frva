package controller;

import controller.util.FrvaSerializer;
import controller.util.TreeviewItems.FrvaTreeItem;
import controller.util.TreeviewItems.FrvaTreeRootItem;
import controller.util.ImportWizard;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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
    loadTreeStructure(FrvaModel.LIBRARYPATH + File.separator + FrvaModel.TREESTRUCTURE);
    addEventHandlers();
    //onChangeTab();
  }

  private void addEventHandlers() {
    expandAllButton.setOnAction(event -> expandAll(treeView.getRoot()));
    collapseAllButton.setOnAction(event -> collapseAll(treeView.getRoot()));
    selectAllButton.setOnAction(event -> ((FrvaTreeRootItem) treeView.getRoot()).setSelected(true));
    selectNoneButton.setOnAction(event -> unselectTickedItems());
    activateMultiSelect();
    deleteSelectedItemsButton.setOnAction(event -> deleteSelectedItems());
    //exportButton.setOnAction(event -> exportData());
    exportButton.setOnAction(event -> FrvaSerializer.serialize(treeView));
    importSdCardButton.setOnAction(event -> importWizard());

  }

  private void importWizard() {

    ImportWizard importWizard = new ImportWizard(importSdCardButton.getScene().getWindow(), model);
    List<MeasureSequence> list = importWizard.startImport();


    List<SdCard> importedSdCards = model.writeData(list, new File(FrvaModel.LIBRARYPATH).toPath());
  //  for (SdCard sdCard : importedSdCards) {sdCard.setPathToLibrary();}
    FrvaSerializer.serializeImports(importWizard.getPreviewTreeView());
    loadTreeStructure(FrvaModel.LIBRARYPATH + File.separator + FrvaModel.TREESTRUCTURE);
  }

  private void addElementsToTreeView(List<SdCard> importedSdCards) {



  }


  /**
   * Exports Data to a specific folder.
   */
  public void exportData() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select export path");
    File selectedFile = directoryChooser.showDialog(exportButton.getScene().getWindow());
    if (selectedFile != null) {
      // model.writeData(model.getCurrentSelectionList(), selectedFile.toPath());
    }
    //TODO get this working on Linux
    //    if (Desktop.isDesktopSupported()) {
    //      try {
    //        Desktop.getDesktop().open(new File(model.getLibraryPath()));
    //      } catch (IOException e) {
    //        logger.info(e.getMessage());
    //      }
    //    }
  }


  private void deleteSelectedItems() {

    /*

    List<FrvaTreeViewItem> list = removeTickedMeasurements(treeView.getRoot(), new ArrayList<>());
    List<MeasureSequence> measureSequenceList = list
        .stream().map(FrvaTreeViewItem::getMeasureSequence).collect(Collectors.toList());
    model.deleteMeasureSequences(measureSequenceList);

    for (FrvaTreeViewItem item : list) {
      item.getParent().getChildren().remove(item);

    }
    unselectTickedItems();
    */

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

  private void loadTreeStructure(String filepath) {
    treeView.setRoot(new FrvaTreeRootItem("Library"));
    treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
    FrvaSerializer.deserializeDB(treeView, filepath, model);
    model.getCurrentlySelectedTabProperty().addListener(
        (observable, oldValue, newValue) -> treeView.getSelectionModel().clearSelection());


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
          ((FrvaTreeRootItem) item).setSelected(false));
    } else {
      unselectTickedItems(treeView.getRoot());
    }
    treeView.getSelectionModel().clearSelection();
  }

  private void unselectTickedItems(TreeItem<FrvaTreeRootItem> item) {
    if (!item.isLeaf()) {
      for (Object child : item.getChildren()) {
        unselectTickedItems((TreeItem) child);
        ((FrvaTreeRootItem) child).setSelected(false);
        ((FrvaTreeRootItem) child).setIndeterminate(false);
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

  private List<FrvaTreeRootItem> removeTickedMeasurements(TreeItem item,
                                                          List<FrvaTreeRootItem> list) {
    if (!item.isLeaf()) {
      for (Object o : item.getChildren()) {
        FrvaTreeRootItem element = (FrvaTreeRootItem) o;
        removeTickedMeasurements(element, list);
        if (element.isSelected()) {
          list.add(element);
        }
      }
    }
    return list;
  }


}
