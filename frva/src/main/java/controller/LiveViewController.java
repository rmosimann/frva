package controller;

import controller.util.DeviceStatus;
import controller.util.bluetooth.ConnectionState;
import controller.util.bluetooth.ConnectionStateConnecting;
import controller.util.bluetooth.ConnectionStateDisconnecting;
import controller.util.bluetooth.ConnectionStateInit;
import controller.util.bluetooth.ConnectionStateSearching;
import controller.util.liveviewparser.CommandAny;
import controller.util.liveviewparser.CommandAutoMode;
import controller.util.liveviewparser.CommandC;
import controller.util.liveviewparser.CommandManualMode;
import controller.util.liveviewparser.CommandT;
import controller.util.liveviewparser.Commandc;
import controller.util.liveviewparser.Commandfc;
import controller.util.liveviewparser.LiveDataParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;
import model.FrvaModel;
import model.data.MeasureSequence;


public class LiveViewController {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private Node activeView;
  private ConnectionStateInit connectionStateInit;
  private DeviceStatus deviceStatus = new DeviceStatus();
  private List<ServiceRecord[]> availableServiceRecords;
  private ServiceRecord[] selectedServiceRecord;
  private StreamConnection openStreamConnection;

  private ObjectProperty<ConnectionState> state = new SimpleObjectProperty<>();

  private LiveDataParser liveDataParser;

  @FXML
  private ListView<MeasureSequence> measurementListView;

  @FXML
  private LineChart<?, ?> datachart;

  @FXML
  private NumberAxis xaxis;

  @FXML
  private NumberAxis yaxis;

  @FXML
  private TextArea miniTerminalTextArea;

  @FXML
  private Label systemNameLabel;

  @FXML
  private Label systemTimeLabel;

  @FXML
  private Label gpsPositionLabel;

  @FXML
  private Label currentCommandLabel;

  @FXML
  private TextField sendAnyCommandField;

  @FXML
  private Button sendAnyCommandButton;

  @FXML
  private Button changeModeButton;

  @FXML
  private Button setTimeButton;

  @FXML
  private Button commandCButton;

  @FXML
  private Button commandcButton;

  @FXML
  private HBox msgBoxBltOff;

  @FXML
  private Button msgBoxBltOffRefreshButton;

  @FXML
  private HBox msgBoxSearching;

  @FXML
  private HBox msgBoxDevices;

  @FXML
  private VBox msgBoxDevicesList;

  @FXML
  private Button msgBoxDevicesRefreshButton;

  @FXML
  private Button bltDisconnectButton;

  @FXML
  private Button commandManualModeButton;

  @FXML
  private Button commandfcButton;


  /**
   * Constructor.
   *
   * @param model the one and only model.
   */
  public LiveViewController(FrvaModel model) {
    connectionStateInit = new ConnectionStateInit(this);
    state.setValue(connectionStateInit);
    this.model = model;
    liveDataParser = new LiveDataParser(this, model);
    addListeners();
  }

  @FXML
  private void initialize() {
    defineButtonActions();
    addBindings();
  }

  private void addBindings() {
    systemNameLabel.textProperty().bind(deviceStatus.systemnameProperty());
    gpsPositionLabel.textProperty().bind(deviceStatus.gpsInformationProperty());

    measurementListView.setItems(model.getLiveSequences());

  }


  private void defineButtonActions() {
    sendAnyCommandButton.setOnAction(event -> {
      String command = sendAnyCommandField.getText();
      sendAnyCommandField.setText("");
      liveDataParser.addCommandToQueue(new CommandAny(liveDataParser, model, command));
    });

    commandCButton.setOnAction(event -> {
      liveDataParser.addCommandToQueue(new CommandC(liveDataParser, model));
    });

    changeModeButton.setOnAction(event -> {
      liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser, model));
    });

    setTimeButton.setOnAction(event -> {
      liveDataParser.addCommandToQueue(new CommandT(liveDataParser, model, null));
    });

    commandcButton.setOnAction(event -> {
      liveDataParser.addCommandToQueue(new Commandc(liveDataParser, model));
    });

    commandManualModeButton.setOnAction(event -> {
      liveDataParser.addCommandToQueue(new CommandManualMode(liveDataParser, model));
    });

    commandfcButton.setOnAction(event -> {
      liveDataParser.addCommandToQueue(new Commandfc(liveDataParser, model));
    });

  }


  private void addListeners() {
    model.activeViewProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(activeView) && state.getValue().equals(connectionStateInit)) {
        state.getValue().handle();
      }
    });

    state.addListener(observable -> {
      logger.info("New state is: " + state.getValue().getClass().getSimpleName());
      state.getValue().handle();
    });
  }


  /**
   * Displays a dialog to indicate that the BLuetooth is off.
   *
   * @param active true displays, false not.
   */
  public void displayBluetoothOffDialog(boolean active) {
    msgBoxBltOff.setVisible(active);
    msgBoxBltOffRefreshButton.setOnAction(event -> state.getValue().handle());
  }


  /**
   * Dialog to displays all availble Devices with a SPP.
   *
   * @param active true to show the dialog.
   */
  public void displayAvailableDevicesDialog(boolean active) {
    if (active) {
      if (availableServiceRecords.size() > 0) {
        msgBoxDevicesList.getChildren().clear();
      }
      availableServiceRecords.forEach(serviceRecords -> {
        String deviceAddress = serviceRecords[0].getHostDevice().getBluetoothAddress();
        String deviceName = deviceAddress;
        try {
          deviceName = serviceRecords[0].getHostDevice().getFriendlyName(true);
        } catch (IOException e) {
          e.printStackTrace();
        }

        VBox buttonLabaleBox = new VBox();
        Label namelabel = new Label(deviceName);
        namelabel.setStyle("-fx-font-weight: bold");
        String[] split = deviceAddress.split("(?<=\\G..)");
        Label addresslabel = new Label(Arrays.toString(split)
            .replace(", ", ":"));
        buttonLabaleBox.getChildren().addAll(namelabel, addresslabel);
        Button serviceRecordButton = new Button();
        serviceRecordButton.setGraphic(buttonLabaleBox);
        serviceRecordButton.setPrefWidth(200);
        serviceRecordButton.setPrefHeight(50);

        String finalDeviceName = deviceName;
        serviceRecordButton.setOnAction((ActionEvent event) -> {
          deviceStatus.setSystemname(finalDeviceName);
          setSelectedServiceRecord(serviceRecords);
          displayAvailableDevicesDialog(false);
          setState(new ConnectionStateConnecting(this));
        });
        msgBoxDevicesList.getChildren().add(serviceRecordButton);
      });
    }

    msgBoxDevices.setVisible(active);

    msgBoxDevicesRefreshButton.setOnAction(event -> {
      setState(new ConnectionStateSearching(this));
      displayAvailableDevicesDialog(false);
    });
  }

  /**
   * Dialog displayed during deviceSearch.
   *
   * @param active true to show the dialog.
   */
  public void displaySearchingDialog(boolean active) {
    msgBoxSearching.setVisible(active);
  }

  /**
   * Enables the DisconnectButton on the view.
   */
  public void enableBltDisconnectButton() {
    bltDisconnectButton.setDisable(false);
    bltDisconnectButton.setOnAction(event -> {
      bltDisconnectButton.setDisable(true);
      setState(new ConnectionStateDisconnecting(this));
    });
  }

  public void setActiveView(Node activeView) {
    this.activeView = activeView;
  }

  public void setState(ConnectionState state) {
    this.state.setValue(state);
  }

  public void setAvailableServiceRecords(List<ServiceRecord[]> availableServiceRecords) {
    this.availableServiceRecords = availableServiceRecords;
  }

  public void setSelectedServiceRecord(ServiceRecord[] selectedServiceRecord) {
    this.selectedServiceRecord = selectedServiceRecord;
  }

  public ServiceRecord[] getSelectedServiceRecord() {
    return selectedServiceRecord;
  }

  public void setOpenStreamConnection(StreamConnection openStreamConnection) {
    this.openStreamConnection = openStreamConnection;
  }

  public StreamConnection getOpenStreamConnection() {
    return openStreamConnection;
  }

  public TextArea getMiniTerminalTextArea() {
    return miniTerminalTextArea;
  }

  public void setLiveDataParser(LiveDataParser liveDataParser) {
    this.liveDataParser = liveDataParser;
  }

  public LiveDataParser getLiveDataParser() {
    return liveDataParser;
  }


  public DeviceStatus getDeviceStatus() {
    return deviceStatus;
  }

  public void setCurrentCommandLabel(String text) {
    Platform.runLater(() -> this.currentCommandLabel.setText(text));
  }
}
