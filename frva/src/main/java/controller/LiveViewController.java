package controller;

import controller.util.bluetooth.BluetoothConnection;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.FrvaModel;


public class LiveViewController {
  private final FrvaModel model;
  private State state;
  private Node viewNode;

  public void setViewNode(Node viewNode) {
    this.viewNode = viewNode;
  }

  public Node getViewNode() {
    return viewNode;
  }

  public enum State {
    INIT, BLUETOOTH_OFF, LIST_DEVICES, CONNECTING, CONNECTED, CONNECTION_FAILED;
  }

  /**
   * Constructor.
   *
   * @param model the one and only model.
   */
  public LiveViewController(FrvaModel model) {
    state = State.INIT;
    if (!BluetoothConnection.isBluetoothOn()) {
      state = State.BLUETOOTH_OFF;
      displayBluetoothOffDialog();
    }
    this.model = model;
    addListeners();
  }

  private void addListeners() {
    model.activeViewProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(viewNode)) {
        //do something
      }
    });
  }

  /**
   * Shows dialog when bluetooth is powered off.
   */
  public void displayBluetoothOffDialog() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Bluetooth is off");
    alert.setHeaderText(null);
    alert.setContentText("You need to turn on Bluetooth to use LiveView");


    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      // ... user chose OK
    } else {
      // ... user chose CANCEL or closed the dialog
    }

  }

  public void displayBluetoothDevicesDialog() {

  }

  public void bla() {

  }
}
