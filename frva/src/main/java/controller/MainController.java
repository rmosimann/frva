package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.FrvaModel;


public class MainController {
  private final FrvaModel model;

  public MainController(FrvaModel model) {
    this.model = model;
  }


  @FXML
  private TabPane tabPane;


  @FXML
  private void initialize() {
    initializeTabHandling();
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
          }
        });
  }

  private void addTab() {
    int numTabs = tabPane.getTabs().size();
    Tab newtab = new Tab("Untitled " + (numTabs + 1));
    newtab.closableProperty().setValue(true);
    newtab.setOnCloseRequest(event -> {
      if (tabPane.getTabs().size() == 2) {
        addTab();
      }
    });


    tabPane.getTabs().add(tabPane.getTabs().size() - 1, newtab);
    try {
      Node node = (Node) FXMLLoader.load(getClass().getResource("../view/tabContent.fxml"));
      newtab.setContent(node);
    } catch (IOException e) {
      e.printStackTrace();
    }
    tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
  }
}
