package controller;

import controller.util.bluetooth.ConnectionState;
import controller.util.bluetooth.ConnectionStateBltOff;
import controller.util.bluetooth.ConnectionStateInit;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javax.bluetooth.ServiceRecord;
import model.FrvaModel;


public class LiveViewController {
  private final FrvaModel model;
  private Node activeView;

  List<ServiceRecord[]> availableServiceRecords;

  private ConnectionState state;
  private ConnectionState bltoff = new ConnectionStateBltOff(this);
  private ConnectionState searching;
  private ConnectionState initState = new ConnectionStateInit(this);

  @FXML
  private HBox messageBOxOutgreyHbox;

  @FXML
  private HBox messageBoxHbox;

  @FXML
  private Label messageBoxTitleLabel;

  @FXML
  private Label messageBoxTextLabel;

  @FXML
  private Button cancelButton;

  @FXML
  private HBox messageBoxSearchingHbox;

  @FXML
  private Label messageBoxTitleSearchingLabel;

  @FXML
  private HBox messageBoxConnectToHbox;

  @FXML
  private Label messageBoxTitleSelectLabel;

  /**
   * Constructor.
   *
   * @param model the one and only model.
   */
  public LiveViewController(FrvaModel model) {
    state = initState;
    this.model = model;
    addListeners();
  }

  private void addListeners() {
    model.activeViewProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(activeView)) {
        state.handle();
      }
    });
  }


  /**
   * Displays a dialog to indicate that the BLuetooth is off.
   * @param active  true displays, false not.
   */
  public void displayBluetoothOffDialog(boolean active) {
    messageBOxOutgreyHbox.setVisible(active);
    messageBoxHbox.setVisible(active);
    cancelButton.setOnAction(event -> state.handle());
  }


  public void setActiveView(Node activeView) {
    this.activeView = activeView;
  }

  public Node getActiveView() {
    return activeView;
  }

  public void setState(ConnectionState state) {
    this.state = state;
  }

  public void displaySearchingDialog(boolean active) {
    messageBOxOutgreyHbox.setVisible(active);
    messageBoxSearchingHbox.setVisible(active);
  }


  public List<ServiceRecord[]> getAvailableServiceRecords() {
    return availableServiceRecords;
  }

  public void setAvailableServiceRecords(List<ServiceRecord[]> availableServiceRecords) {
    this.availableServiceRecords = availableServiceRecords;
  }
}
