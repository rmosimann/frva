package controller;

import controller.util.bluetooth.BluetoothConnection;
import controller.util.bluetooth.ConnectionState;
import controller.util.bluetooth.ConnectionStateInit;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.bluetooth.ServiceRecord;
import model.FrvaModel;


public class LiveViewController {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private Node activeView;
  ConnectionStateInit connectionStateInit;

  List<ServiceRecord[]> availableServiceRecords;

  private ObjectProperty<ConnectionState> state = new SimpleObjectProperty<>();

  @FXML
  private LineChart<?, ?> datachart;

  @FXML
  private NumberAxis xaxis;

  @FXML
  private NumberAxis yaxis;

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
  private HBox messageBoxAvailableDevicesHbox;

  @FXML
  private Label messageBoxTitleSelectLabel;

  @FXML
  private VBox AvailableDevicesListVbox;


  /**
   * Constructor.
   *
   * @param model the one and only model.
   */
  public LiveViewController(FrvaModel model) {

    connectionStateInit = new ConnectionStateInit(this);
    state.setValue(connectionStateInit);
    this.model = model;
    addListeners();
  }

  private void addListeners() {
    model.activeViewProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(activeView) && state.getValue().equals(connectionStateInit)) {
        state.getValue().handle();
      }
    });

    state.addListener(observable -> {
      state.getValue().handle();
    });
  }


  /**
   * Displays a dialog to indicate that the BLuetooth is off.
   * @param active  true displays, false not.
   */
  public void displayBluetoothOffDialog(boolean active) {
    messageBOxOutgreyHbox.setVisible(active);
    messageBoxHbox.setVisible(active);
    cancelButton.setOnAction(event -> state.getValue().handle());
  }


  public void setActiveView(Node activeView) {
    this.activeView = activeView;
  }

  public Node getActiveView() {
    return activeView;
  }

  public void setState(ConnectionState state) {
    this.state.setValue(state);
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

  Button serviceRecordButton;

  public void displayAvailableDevicesDialog(boolean active) {


    availableServiceRecords.forEach(serviceRecords -> {
      System.out.println("mddddddddddmdmdmd" + serviceRecords);
      serviceRecordButton = new Button();
      serviceRecordButton.setText(serviceRecords[0].getConnectionURL(0, false));
      serviceRecordButton.setOnAction(event -> BluetoothConnection.connectToService(serviceRecords));
      AvailableDevicesListVbox.getChildren().add(serviceRecordButton);
    });
    messageBOxOutgreyHbox.setVisible(active);
    messageBoxAvailableDevicesHbox.setVisible(active);

  }
}
