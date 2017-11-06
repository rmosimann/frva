package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.FrvaModel;

public class MainMenuController {

  private final FrvaModel model;
  Node mainViewPane;
  Node settingsViewPane;
  Node liveViewNode;

  @FXML
  private Button buttonLiveView;

  @FXML
  private Button buttonSettings;

  @FXML
  private Button buttonLibrary;

  @FXML
  private VBox contentVbox;


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
      loader.setController(new LiveViewController(model));
      liveViewNode = loader.load();

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
    contentVbox.getChildren().add(mainViewPane);
    addEventhandlers();
  }

  private void addEventhandlers() {
    buttonLibrary.setOnAction(event -> {
      contentVbox.getChildren().add(mainViewPane);
      contentVbox.getChildren().removeAll(settingsViewPane, liveViewNode);

    });

    buttonLiveView.setOnAction(event -> {
      contentVbox.getChildren().add(liveViewNode);
      contentVbox.getChildren().removeAll(settingsViewPane, mainViewPane);

    });

    buttonSettings.setOnAction(event -> {
      contentVbox.getChildren().add(settingsViewPane);
      contentVbox.getChildren().removeAll(liveViewNode, mainViewPane);
    });


  }


}
