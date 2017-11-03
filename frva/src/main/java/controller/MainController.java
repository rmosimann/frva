package controller;

import controller.util.FrvaTreeViewItem;
import controller.util.ImportWizard;
import controller.util.TreeViewFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.DirectoryChooser;
import model.FrvaModel;
import model.data.DataFile;
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
    initializeTreeView(model.getLibrary());
    addEventHandlers();
    //onChangeTab();
  }

  private void addEventHandlers() {
    expandAllButton.setOnAction(event -> expandAll(treeView.getRoot()));
    collapseAllButton.setOnAction(event -> collapseAll(treeView.getRoot()));
    selectAllButton.setOnAction(event -> ((FrvaTreeViewItem) treeView.getRoot()).setSelected(true));
    selectNoneButton.setOnAction(event -> unselectTickedItems());
    activateMultiSelect();
    deleteSelectedItemsButton.setOnAction(event -> deleteSelectedItems());
    //exportButton.setOnAction(event -> exportData());
    exportButton.setOnAction(event -> serializeDB());
    importSdCardButton.setOnAction(event -> importWizard());

  }

  private void importWizard() {

    ImportWizard importWizard = new ImportWizard(importSdCardButton.getScene().getWindow(), model);
    List<MeasureSequence> list = importWizard.startImport();
    List<SdCard> importedSdCards = model.writeData(list, new File(model.getLibraryPath()).toPath());
    addElementsToTreeView(importedSdCards);
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

    List<FrvaTreeViewItem> list = removeTickedMeasurements(treeView.getRoot(), new ArrayList<>());
    List<MeasureSequence> measureSequenceList = list
        .stream().map(FrvaTreeViewItem::getMeasureSequence).collect(Collectors.toList());
    model.deleteMeasureSequences(measureSequenceList);

    for (FrvaTreeViewItem item : list) {
      item.getParent().getChildren().remove(item);

    }
    unselectTickedItems();

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


  private void initializeTreeView(List<SdCard> list) {

    treeView.setRoot(new FrvaTreeViewItem("Library", null, model,
        FrvaTreeViewItem.Type.ROOT, null, false));
    treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

    addElementsToTreeView(list);
    model.getCurrentlySelectedTabProperty().addListener(
        (observable, oldValue, newValue) -> treeView.getSelectionModel().clearSelection());


  }

  private void addElementsToTreeView(List<SdCard> list) {
    TreeViewFactory.createDummyTreeView(list, treeView, model,  false);
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
          ((FrvaTreeViewItem) item).setSelected(false));
    } else {
      unselectTickedItems(treeView.getRoot());
    }
    treeView.getSelectionModel().clearSelection();
  }

  private void unselectTickedItems(TreeItem<FrvaTreeViewItem> item) {
    if (!item.isLeaf()) {
      for (Object child : item.getChildren()) {
        unselectTickedItems((TreeItem) child);
        ((FrvaTreeViewItem) child).setSelected(false);
        ((FrvaTreeViewItem) child).setIndeterminate(false);
      }
    }
  }

  private void activateMultiSelect() {
    treeView.setOnMouseClicked(event -> {
      treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      treeView.getSelectionModel().getSelectedItems().forEach(item ->
          ((FrvaTreeViewItem) item).setSelected(true));
    });
  }

  private List<FrvaTreeViewItem> removeTickedMeasurements(TreeItem item,
                                                          List<FrvaTreeViewItem> list) {
    if (!item.isLeaf()) {
      for (Object o : item.getChildren()) {
        FrvaTreeViewItem element = (FrvaTreeViewItem) o;
        removeTickedMeasurements(element, list);
        if (element.isSelected()) {
          list.add(element);
        }
      }
    }
    return list;
  }

  private void serializeDB() {
    System.out.println("DB serialize");

    File file = new File(model.getLibraryPath() + File.separator + "treeStructure.csv");
    try {
      Writer writer = Files.newBufferedWriter(Paths.get(file.toURI()));
      for (Object item : treeView.getRoot().getChildren()
          ) {
        serializeDB((FrvaTreeViewItem)item, writer);
      }
      writer.close();
    } catch (IOException ex) {

    }
  }

  private void serializeDB(TreeItem item, Writer writer) throws IOException {
    writer.write(((FrvaTreeViewItem) item).serialize()+"\n");
    writer.flush();
    if (!item.isLeaf()) {
      for (Object child : item.getChildren()
          ) {
        serializeDB((TreeItem) child, writer);

      }
    }


  }

}
