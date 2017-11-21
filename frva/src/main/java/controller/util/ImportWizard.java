package controller.util;

import controller.util.treeviewitems.FrvaTreeItem;
import controller.util.treeviewitems.FrvaTreeMeasurementItem;
import controller.util.treeviewitems.FrvaTreeRootItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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
  }

  /**
   * starts the importprocess.
   *
   * @return a list of the imported MeasureSequences.
   */
  public List<MeasureSequence> startImport() {

    Wizard wizard = new Wizard(owner);
    //First Page
    WizardPane choseSdCardPane = createFirstPage();
    WizardPane choseSdCardNamePane = createSecondPage();
    WizardPane selectMeasurementsPane = createThirdPage();

    wizard.setFlow(new Wizard.LinearFlow(choseSdCardPane, choseSdCardNamePane,
        selectMeasurementsPane));


    // show wizard and wait for response
    wizard.showAndWait().ifPresent(result -> {
      if (result == ButtonType.FINISH) {
        updateImportList((FrvaTreeRootItem) previewTreeView.getRoot());
      }
    });
    return importList;
  }

  private void updateImportList(FrvaTreeItem item) {
    if (item instanceof FrvaTreeMeasurementItem) {
      if (item.isSelected()) {
        importList.add(((FrvaTreeMeasurementItem)item).getMeasureSequence());
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

        File file = new File(chosenDirectoryPath.get());
        SdCard sdCard = new SdCard(file, chosenSdCardName.get());
        sdCardList.add(sdCard);

        logger.info("set SD-Cardname " + chosenSdCardName.get()
            + " at location" + sdCard.getPath());


        TreeViewFactory.extendTreeView(sdCard, previewTreeView, model, true);
        ((FrvaTreeRootItem) previewTreeView.getRoot()).setSelected(true);

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
    directoryChooser.setTitle("Select SD-Card");
    File chosenFile = directoryChooser.showDialog(owner);
    chosenDirectoryPath.set(chosenFile.getAbsolutePath());
  }


}
