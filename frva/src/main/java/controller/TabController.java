package controller;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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

    listToWatch.addListener((ListChangeListener<? super MeasureSequence>) c -> {
      updateData();
    });
  }

  private void configureRadioButtons() {
    togglGroupYaxis = new ToggleGroup();
    togglGroupYaxis.getToggles()
        .addAll(radioButtonRadiance, radioButtonRaw, radioButtonReflectance);
    togglGroupYaxis.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      updateData();
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
      updateData();
    });
    togglGroupXaxis.selectToggle(radioButtonWavelength);
  }

  private void initializeGraph() {

    xaxis.setAutoRanging(true);
    xaxis.setAnimated(false);
    xaxis.setForceZeroInRange(false);

    yaxis.setAutoRanging(true);
    yaxis.setAnimated(false);

    datachart.setAnimated(true);
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

  private void updateData() {
    lineChartData.clear();
    for (MeasureSequence sequence : listToWatch) {
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
        series.setName(entry.getKey());
        for (int i = 0; i < data.length; i++) {
          double x = asWavelength ? calibration[i] : i;
          double y = data[i];

          series.getData().add(new XYChart.Data<>(x, y));
        }
        lineChartData.add(series);
      }
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

