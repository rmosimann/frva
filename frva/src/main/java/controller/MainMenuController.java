package controller;

import controller.util.treeviewitems.FrvaTreeRootItem;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import model.FrvaModel;
import model.data.FileInOut;
import model.data.LiveMeasureSequence;
import model.data.MeasureSequence;

public class MainMenuController {
  private final FrvaModel model;
  private Node mainViewPane;
  private Node liveViewNode;
  private MainController mainController;
  EventHandler<ActionEvent> mainMenuHandler;

  @FXML
  private Button buttonLiveView;

  @FXML
  private Button buttonLibrary;

  @FXML
  private VBox contentVbox;

  @FXML
  private Button settingsButton;


  /**
   * Creates a MAinMenuController.
   *
   * @param model The one and only model.
   */
  public MainMenuController(FrvaModel model) {
    this.model = model;

    try {
      //Load MainView
      FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemClassLoader()
          .getResource("view/mainView.fxml"));
      mainController = new MainController(model);
      loader.setController(mainController);
      mainViewPane = loader.load();

      loader = new FXMLLoader(ClassLoader.getSystemClassLoader()
          .getResource("view/liveView.fxml"));
      LiveViewController liveViewController = new LiveViewController(model);
      loader.setController(liveViewController);
      liveViewNode = loader.load();
      liveViewController.setActiveView(liveViewNode);


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void initialize() throws IOException {
    addEventhandlers();
    buttonLibrary.fire();
  }


  private void addEventhandlers() {
    mainMenuHandler = event -> {
      Button pressedButton;
      if (event.getSource() instanceof Button) {
        pressedButton = (Button) event.getSource();
      } else {
        throw new IllegalArgumentException();
      }

      Node nodetouse = null;

      if (pressedButton == buttonLibrary) {
        nodetouse = mainViewPane;
        if (model.getLiveSequences().size() > 0) {
          MeasureSequence liveMs = model.getLiveSequences().get(0);
          if (liveMs instanceof LiveMeasureSequence
              && ((LiveMeasureSequence) liveMs).isComplete()) {
            mainController.refreshTreeView();
          }
        }

      } else if (pressedButton == buttonLiveView) {
        nodetouse = liveViewNode;
      }

      if (contentVbox.getChildren().size() > 1) {
        contentVbox.getChildren().remove(1);
      }

      if (nodetouse != null) {
        contentVbox.getChildren().add(1, nodetouse);
        model.setActiveView(nodetouse);
      }

      setSelectedButton(pressedButton);
    };


    buttonLibrary.addEventHandler(ActionEvent.ACTION, mainMenuHandler);
    buttonLiveView.addEventHandler(ActionEvent.ACTION, mainMenuHandler);

    settingsButton.setOnAction(event -> model.changeLibraryPath(choseLibraryPath()));


  }

  private void setSelectedButton(Button button) {
    buttonLiveView.getStyleClass().remove("selected");
    buttonLibrary.getStyleClass().remove("selected");
    button.getStyleClass().add("selected");
  }

  private String choseLibraryPath() {
    String selectedPath = null;
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.getDialogPane().setMinHeight(200);
    alert.setTitle("Warning");
    alert.setHeaderText("You are going to change the path of your library");
    alert.setContentText("No files are going to be moved to the new location. "
        + "If you want to move files from your old library to the new library, import them with "
        + "the import-wizard, after the restart of the application.");

    Optional<ButtonType> result = alert.showAndWait();
    boolean ok = result.isPresent() && result.get() == ButtonType.OK;
    if (ok) {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      directoryChooser.setTitle("Select path for library");
      directoryChooser.setInitialDirectory(new File(FrvaModel.LIBRARYPATH));

      File path = directoryChooser.showDialog(settingsButton.getScene().getWindow());
      if (path != null) {
        selectedPath = path.getPath();
      }
    }
    return selectedPath;
  }
}
