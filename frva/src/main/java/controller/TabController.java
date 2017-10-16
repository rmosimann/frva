package controller;

import java.awt.Color;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import model.FrvaModel;
import model.data.MeasureSequence;


public class TabController {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private final ObservableList<XYChart.Series<Double, Double>> lineChartData;
  private final int tabId;
  private final ObservableList<MeasureSequence> listToWatch;
  private ToggleGroup togglGroupYaxis;
  private ToggleGroup togglGroupXaxis;
  private boolean asWavelength = true;

  /**
   * Constructor for new TabController.
   *
   * @param model     The one and only Model.
   * @param thisTabId the ID of this Tab.
   */
  public TabController(FrvaModel model, int thisTabId) {
    this.model = model;
    lineChartData = FXCollections.observableArrayList();
    tabId = thisTabId;
    listToWatch = model.getObservableList(thisTabId);
  }

  @FXML
  private LineChart<Double, Double> datachart;

  @FXML
  private NumberAxis xaxis;

  @FXML
  private NumberAxis yaxis;

  @FXML
  private RadioButton radioButtonRaw;

  @FXML
  private RadioButton radioButtonReflectance;

  @FXML
  private RadioButton radioButtonRadiance;

  @FXML
  private RadioButton radioButtonWavelength;

  @FXML
  private RadioButton radioButtonBands;


  @FXML
  private void initialize() {
    configureRadioButtons();
    initializeGraph();

    listToWatch.addListener(new ListChangeListener<MeasureSequence>() {
      @Override
      public void onChanged(Change<? extends MeasureSequence> c) {
        while (c.next()) {
          if (c.wasAdded()) {
            for (MeasureSequence sequence : c.getAddedSubList()) {
              addSingleSequence(sequence);
            }
          } else if (c.wasRemoved()) {
            for (MeasureSequence sequence : c.getRemoved()) {
              removeSingleSequence(sequence);
            }
          }
        }
      }
    });
  }

  private void configureRadioButtons() {
    togglGroupYaxis = new ToggleGroup();
    togglGroupYaxis.getToggles()
        .addAll(radioButtonRadiance, radioButtonRaw, radioButtonReflectance);
    togglGroupYaxis.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(radioButtonRaw)) {
        yaxis.setLabel("DN (digital number)");
      }
      if (newValue.equals(radioButtonRadiance)) {
        yaxis.setLabel("[W/( m²sr nm)]");
      }
      if (newValue.equals(radioButtonReflectance)) {
        yaxis.setLabel("Reflectance Factor");
      }
      changemode();
    });
    togglGroupYaxis.selectToggle(radioButtonRaw);


    togglGroupXaxis = new ToggleGroup();
    togglGroupXaxis.getToggles().addAll(radioButtonWavelength, radioButtonBands);
    togglGroupXaxis.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (togglGroupXaxis.getSelectedToggle().equals(radioButtonWavelength)) {
        asWavelength = true;
        xaxis.setLabel("Wavelength [nanometer]");
      } else {
        asWavelength = false;
        xaxis.setLabel("Bands");
      }
      changemode();
    });
    togglGroupXaxis.selectToggle(radioButtonWavelength);
  }

  private void initializeGraph() {

    xaxis.setAutoRanging(true);
    xaxis.setAnimated(false);
    xaxis.setForceZeroInRange(false);

    yaxis.setAutoRanging(true);
    yaxis.setAnimated(false);

    datachart.setAnimated(false);
    datachart.setCreateSymbols(false);
    datachart.setAlternativeRowFillVisible(false);
    datachart.setLegendVisible(false);
    datachart.setData(lineChartData);

    datachart.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        zoomIn(event.getX(), event.getY());
      }
    });
  }

  private void changemode() {
    logger.info("Redrawing Graph  ");
    lineChartData.clear();
    for (MeasureSequence sequence : listToWatch) {
      addSingleSequence(sequence);
    }
  }

  private void removeSingleSequence(MeasureSequence sequence) {
    lineChartData.removeIf(doubleDoubleSeries -> {
      return doubleDoubleSeries.getName().contains(sequence.getSerial())
          && doubleDoubleSeries.getName().contains("ID" + sequence.getId());
    });
  }

  private void addSingleSequence(MeasureSequence sequence) {
    Set<Map.Entry<String, double[]>> entries = null;
    if (togglGroupYaxis.getSelectedToggle().equals(radioButtonRaw)) {
      entries = sequence.getMeasurements().entrySet();
      yaxis.setLabel("DN (digital number)");
    }
    if (togglGroupYaxis.getSelectedToggle().equals(radioButtonRadiance)) {
      entries = sequence.getRadiance().entrySet();
      yaxis.setLabel("[W/( m²sr nm)]");
    }
    if (togglGroupYaxis.getSelectedToggle().equals(radioButtonReflectance)) {
      entries = sequence.getReflection().entrySet();
      yaxis.setLabel("Reflectance Factor");
    }

    double[] calibration = sequence.getWavlengthCalibration();

    for (Map.Entry<String, double[]> entry : entries) {
      double[] data = entry.getValue();
      LineChart.Series<Double, Double> series = new LineChart.Series<Double, Double>();
      series.setName("ID" + sequence.getId() + " - "
          + sequence.getSerial() + " - " + entry.getKey());
      for (int i = 0; i < data.length; i++) {
        double x = asWavelength ? calibration[i] : i;
        double y = data[i];
        series.getData().add(new XYChart.Data<>(x, y));
      }


      lineChartData.add(series);

      Tooltip tooltip = new Tooltip();
      Tooltip.install(series.getNode(), tooltip);

      Random rand = new Random();
      int r = rand.nextInt(200);
      int g = rand.nextInt(200);
      int b = rand.nextInt(200);
      series.getNode().setStyle("-fx-stroke: rgba(" + r + "," + g + "," + b + ");"
          + "-fx-stroke-width: 1px");

      tooltip.setStyle("-fx-background-color: rgba(" + r + "," + g + "," + b + ");");

      series.getNode().setOnMouseEntered(event -> {
        double xlowest = xaxis.getLowerBound();
        double xhighest = xaxis.getUpperBound();
        double ylowest = yaxis.getLowerBound();
        double yhighest = yaxis.getUpperBound();
        double pxWidth = series.getNode().getParent().getParent().getBoundsInLocal().getWidth();
        double pxHeigth = series.getNode().getParent().getParent().getBoundsInLocal().getHeight();
        double xvalue = (((xhighest - xlowest) / pxWidth) * event.getX()) + xlowest;
        double yvalue = (((yhighest - ylowest) / pxHeigth) * (pxHeigth - event.getY())) + ylowest;

        series.getNode().setStyle("-fx-stroke: rgba(" + r + "," + g + "," + b + ");"
            + "-fx-stroke-width: 4px");


        tooltip.setText("ID: " + sequence.getId() + "\n"
            + "Serial: " + sequence.getSerial() + "\n"
            + "x: " + String.valueOf(xvalue) + "\n"
            + "y: " + String.valueOf(yvalue));
      });

      series.getNode().setOnMouseExited(event -> {
        series.getNode().setStyle("-fx-stroke: rgba(" + r + "," + g + "," + b + ");"
            + "-fx-stroke-width: 1px");
      });
    }
  }

  private void calculateIndicies() {
    /*
     * NDVI:
     *    NDVI is defined as (NIR - RED) / (NIR + RED)
     *    where NIR = reflectance at wavelength 920nm
     *    RED = reflectance at wavelength 696nm
     *
     * TCARI
     *
     * PRI
     *
     */
  }


  private void zoomIn(double xpos, double ypos) {
    double zoomFactor = 10;

    double xzoomstep = (xaxis.getUpperBound() - xaxis.getLowerBound()) / zoomFactor;
    double yzoomstep = (yaxis.getUpperBound() - yaxis.getLowerBound()) / zoomFactor;

    double partLeft = xpos;
    double zoomLeft = (xzoomstep / datachart.getWidth()) * partLeft;
    xaxis.setLowerBound(xaxis.getLowerBound() + zoomLeft);

    double partRight = datachart.getWidth() - xpos;
    double zoomRight = (xzoomstep / datachart.getWidth()) * partRight;
    xaxis.setUpperBound(xaxis.getUpperBound() - zoomRight);

    double partDown = datachart.getHeight() - ypos;
    double zoomDown = (yzoomstep / datachart.getHeight()) * partDown;
    yaxis.setLowerBound(yaxis.getLowerBound() + zoomDown);

    double partUp = ypos;
    double zoomUp = (yzoomstep / datachart.getHeight()) * partUp;
    yaxis.setUpperBound(yaxis.getUpperBound() - zoomUp);
  }
}

