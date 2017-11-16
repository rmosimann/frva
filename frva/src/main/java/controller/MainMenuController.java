package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.FrvaModel;

public class MainMenuController {
  private final FrvaModel model;
  private Node mainViewPane;
  private Node settingsViewPane;
  private Node liveViewNode;
  EventHandler<ActionEvent> mainMenuHandler;

  @FXML
  private Button buttonLiveView;

  @FXML
  private Button buttonSettings;

  @FXML
  private Button buttonLibrary;

  @FXML
  private VBox contentVbox;


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
      loader.setController(new MainController(model));
      mainViewPane = loader.load();

      loader = new FXMLLoader(ClassLoader.getSystemClassLoader()
          .getResource("view/liveView.fxml"));
      LiveViewController liveViewController = new LiveViewController(model);
      loader.setController(liveViewController);
      liveViewNode = loader.load();
      liveViewController.setViewNode(liveViewNode);


      loader = new FXMLLoader(ClassLoader.getSystemClassLoader()
          .getResource("view/settingsView.fxml"));
      loader.setController(new SettingsController(model));
      settingsViewPane = loader.load();

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
      } else if (pressedButton == buttonLiveView) {
        nodetouse = liveViewNode;
      } else if (pressedButton == buttonSettings) {
        nodetouse = settingsViewPane;
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
    buttonSettings.addEventHandler(ActionEvent.ACTION, mainMenuHandler);
  }

  private void setSelectedButton(Button button) {
    buttonLiveView.getStyleClass().remove("selected");
    buttonSettings.getStyleClass().remove("selected");
    buttonLibrary.getStyleClass().remove("selected");
    button.getStyleClass().add("selected");

  }
}
