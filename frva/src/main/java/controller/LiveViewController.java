package controller;

import controller.util.bluetooth.ConnectionState;
import controller.util.bluetooth.ConnectionStateConnecting;
import controller.util.bluetooth.ConnectionStateDisconnecting;
import controller.util.bluetooth.ConnectionStateInit;
import controller.util.bluetooth.ConnectionStateSearching;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;
import model.FrvaModel;


public class LiveViewController {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private Node activeView;
  private ConnectionStateInit connectionStateInit;
  private String deviceName;

  private List<ServiceRecord[]> availableServiceRecords;
  private ServiceRecord[] selectedServiceRecord;
  private StreamConnection openStreamConnection;

  private ObjectProperty<ConnectionState> state = new SimpleObjectProperty<>();

  @FXML
  private LineChart<?, ?> datachart;

  @FXML
  private NumberAxis xaxis;

  @FXML
  private NumberAxis yaxis;

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
  private TextArea miniTerminalTextArea;


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
        final String[] deviceAddress = {serviceRecords[0].getHostDevice().getBluetoothAddress()};
        final String[] deviceName = {deviceAddress[0]};
        Runnable r = new Runnable() {
          @Override
          public void run() {
            try {
              deviceName[0] = serviceRecords[0].getHostDevice().getFriendlyName(false);

            } catch (IOException e) {

              e.printStackTrace();
            }
          }
        };

        Thread t = new Thread(r);
        t.start();




        VBox buttonLabaleBox = new VBox();

        Label namelabel=null;

        try {
          t.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        namelabel = new Label(deviceName[0]);


        namelabel.setStyle("-fx-font-weight: bold");


        String[] split = deviceAddress[0].split("(?<=\\G..)");
        Label addresslabel = new Label(Arrays.toString(split)
            .replace(", ", ":"));
        buttonLabaleBox.getChildren().addAll(namelabel, addresslabel);


        Button serviceRecordButton = new Button();
        serviceRecordButton.setGraphic(buttonLabaleBox);
        serviceRecordButton.setPrefWidth(200);
        serviceRecordButton.setPrefHeight(50);

        serviceRecordButton.setOnAction((ActionEvent event) -> {
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

  /**
   * Just for testing reasons.
   *
   * @param openStreamConnection The Stream of the device
   */
  public void setOpenStreamConnection(StreamConnection openStreamConnection) {
    this.openStreamConnection = openStreamConnection;
    //TODO: Test impl
    try {
      InputStream is = openStreamConnection.openInputStream();
      int i;
      StringBuilder temp = new StringBuilder();
      while (true) {
        while ((i = is.read()) != -1) {
          miniTerminalTextArea.setText(temp.append((char) i).toString());

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public StreamConnection getOpenStreamConnection() {
    return openStreamConnection;
  }

  public TextArea getMiniTerminalTextArea() {
    return miniTerminalTextArea;
  }
}
