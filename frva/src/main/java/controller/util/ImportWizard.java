package controller.util;

import controller.util.treeviewitems.FrvaTreeItem;
import controller.util.treeviewitems.FrvaTreeMeasurementItem;
import controller.util.treeviewitems.FrvaTreeRootItem;
import controller.util.treeviewitems.FrvaTreeSdCardItem;
import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import model.FrvaModel;
import model.data.MeasureSequence;
import model.data.SdCard;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

/**
 * Created by patrick.wigger on 24.10.17.
 */
public class ImportWizard {

  private Window owner;
  private StringProperty chosenDirectoryPath;
  private StringProperty chosenSdCardName;
  private List<SdCard> sdCardList;
  private Wizard wizard;

  private File chosenDirectory;
  private BooleanProperty validDir;

  public TreeView<FrvaTreeRootItem> getPreviewTreeView() {
    return previewTreeView;
  }

  private TreeView<FrvaTreeRootItem> previewTreeView;
  private List<MeasureSequence> importList;
  private FrvaModel model;
  private final Logger logger = Logger.getLogger("FRVA");


  /**
   * Constructor of import wizard, creates a new wizard.
   *
   * @param owner the Window from within the wizard is called
   * @param model the model of the project
   */
  public ImportWizard(Window owner, FrvaModel model) {
    validDir = new SimpleBooleanProperty(false);
    this.owner = owner;
    this.chosenDirectoryPath = new SimpleStringProperty("no directory chosen");
    this.chosenSdCardName = new SimpleStringProperty("unknown SDCARD");
    this.model = model;
    initalizeTreeView();
    importList = new ArrayList<>();
    sdCardList = new ArrayList<>();
  }

  private void initalizeTreeView() {
    this.previewTreeView = new TreeView<>();
    previewTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());
    previewTreeView.setRoot(new FrvaTreeRootItem("Library"));
    previewTreeView.setShowRoot(false);
  }

  /**
   * starts the importprocess.
   *
   * @return a list of the imported MeasureSequences.
   */
  public List<MeasureSequence> startImport() {

    wizard = new Wizard(owner);
    wizard.invalidProperty().bind(validDir.not());

    //First Page
    WizardPane choseSdCardPane = createFirstPage();
    WizardPane choseSdCardNamePane = createSecondPage();
    WizardPane selectMeasurementsPane = createThirdPage();

    wizard.setFlow(new Wizard.LinearFlow(choseSdCardPane, choseSdCardNamePane,
        selectMeasurementsPane));


    // show wizard and wait for response
    wizard.showAndWait().ifPresent(result -> {
      if (result == ButtonType.FINISH) {
        updateImportList((FrvaTreeItem) previewTreeView.getRoot());
      }
    });
    return importList;
  }

  private void updateImportList(FrvaTreeItem item) {
    if (item instanceof FrvaTreeMeasurementItem) {
      if (item.isSelected()) {
        importList.add(((FrvaTreeMeasurementItem) item).getMeasureSequence());
      }
    } else {
      for (Object child : item.getChildren()) {
        updateImportList((FrvaTreeItem) child);
      }
    }
  }

  private WizardPane createFirstPage() {

    WizardPane choseSdCard = new WizardPane();
    choseSdCard.setHeaderText("Please chose the directory of the SD-Card you want to import");

    GridPane choseSdCardGrid = new GridPane();

    choseSdCardGrid.setVgap(40);
    choseSdCardGrid.setHgap(10);

    Button choseSdCardButton = new Button("Chose SD Card Folder");
    choseSdCardButton.setOnAction(e -> choseDirectory());
    choseSdCardGrid.add(choseSdCardButton, 0, 0);
    Label chosenDirectoryLabel = new Label();
    chosenDirectoryLabel.textProperty().bind(chosenDirectoryPath);
    choseSdCardGrid.add(chosenDirectoryLabel, 1, 0);
    choseSdCard.setContent(choseSdCardGrid);
    return choseSdCard;
  }

  private WizardPane createSecondPage() {

    WizardPane choseSdCardNamePane = new WizardPane();
    choseSdCardNamePane.setHeaderText("Please chose a name for the SD-Card you want to import");

    GridPane choseSdCardNameGrid = new GridPane();

    choseSdCardNameGrid.setVgap(40);
    choseSdCardNameGrid.setHgap(10);

    TextField sdCardNameField = new TextField();

    DateFormat df = new SimpleDateFormat("YYYYMMdd_HHmm");
    Date dateobj = new Date();
    sdCardNameField.setText("import_" + df.format(dateobj));

    List<String> existingSdcardNames = model.getLibrary().stream()
        .map(sdCard -> sdCard.getName())
        .collect(Collectors.toList());
    int sdcardnameVersion = 0;
    while (existingSdcardNames.contains(sdCardNameField.getText())) {
      sdcardnameVersion++;
      sdCardNameField.setText("import_" + df.format(dateobj) + "(" + sdcardnameVersion + ")");
    }

    sdCardNameField.setPromptText("SD-Card Name");
    choseSdCardNameGrid.add(sdCardNameField, 0, 0);
    chosenSdCardName.bind(sdCardNameField.textProperty());
    choseSdCardNamePane.setContent(choseSdCardNameGrid);
    return choseSdCardNamePane;
  }

  private WizardPane createThirdPage() {

    WizardPane choseMeasurementsPane = new WizardPane() {
      @Override
      public void onEnteringPage(Wizard wizard) {
        SdCard sdCard = new SdCard(chosenDirectory, chosenSdCardName.get());
        sdCardList.add(sdCard);

        logger.info("set SD-Cardname " + chosenSdCardName.get()
            + " at location" + sdCard.getPath());

        ((FrvaTreeRootItem) previewTreeView.getRoot()).createChildren(sdCardList, true);
        ((FrvaTreeRootItem) previewTreeView.getRoot()).setSelected(true);
        previewTreeView.setRoot(previewTreeView.getRoot().getChildren().get(0)
            .getChildren().get(0));
        previewTreeView.setOnMouseClicked(event -> {
          previewTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
          previewTreeView.getSelectionModel().getSelectedItems().forEach(item ->
              ((FrvaTreeItem) item).setSelected(true));
        });


      }
    };


    choseMeasurementsPane.setHeaderText("Chose measurements you want to import.");
    Pane selectMeasurementsGrid = new VBox();
    HBox checkbox = new HBox();
    CheckBox importAllCheckbox = new CheckBox();
    Label importAllLabel = new Label("Import full SD-Card");
    importAllCheckbox.setSelected(true);
    checkbox.getChildren().addAll(importAllCheckbox, importAllLabel);
    selectMeasurementsGrid.getChildren().add(checkbox);
    selectMeasurementsGrid.getChildren().add(previewTreeView);
    previewTreeView.disableProperty().bind(importAllCheckbox.selectedProperty());
    importAllCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        ((FrvaTreeRootItem) previewTreeView.getRoot()).setSelected(true);
      }
    });
    choseMeasurementsPane.setContent(selectMeasurementsGrid);
    return choseMeasurementsPane;

  }


  private void choseDirectory() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    if (chosenDirectory != null) {
      directoryChooser.setInitialDirectory(chosenDirectory);
    }
    directoryChooser.setTitle("Select SD-Card");
    chosenDirectory = directoryChooser.showDialog(owner);
    if (chosenDirectory != null) {
      if (chosenDirectory.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.equals("cal.csv");
        }
      }).length == 1) {
        chosenDirectoryPath.set(chosenDirectory.getAbsolutePath());
        validDir.setValue(true);
      } else if (chosenDirectory.getParentFile().list(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.equals("cal.csv");
        }
      }).length == 1) {
        chosenDirectoryPath.set(chosenDirectory.getParentFile().getAbsolutePath());
        chosenDirectory = new File(chosenDirectoryPath.get());
        validDir.setValue(true);
      } else {
        chosenDirectoryPath.set(null);
        chosenDirectory = null;
        validDir.setValue(false);

      }
    }
  }

}
