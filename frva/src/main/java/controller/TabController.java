package controller;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import model.FrvaModel;


public class TabController extends Tab {
  private final Logger logger = Logger.getLogger("FRVA");
  private final FrvaModel model;
  private final ObservableList<XYChart.Series<Double, Double>> lineChartData;


  public TabController(FrvaModel model) {
    this.model = model;
    lineChartData = FXCollections.observableArrayList();
  }

  @FXML
  private ToggleButton wavelengthButton;

  @FXML
  private ToggleButton bandsButton;

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
  private void initialize() {
    configureRadios();
    initializeGraph();
    addRawData();
  }

  private void configureRadios() {
    ToggleGroup radiogaga = new ToggleGroup();
    radiogaga.getToggles().addAll(radioButtonRadiance, radioButtonRaw, radioButtonReflectance);
    radiogaga.selectToggle(radioButtonRaw);

    radiogaga.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(radioButtonRadiance)) {
        this.showRadiance();
      } else if (newValue.equals(radioButtonRaw)) {
        this.showRaw();
      } else if (newValue.equals(radioButtonReflectance)) {
        this.showReflectance();
      }
    });
  }

  private void initializeGraph() {
    xaxis.setLowerBound(300);
    xaxis.setUpperBound(1100);
    xaxis.setAutoRanging(false);
    xaxis.setTickUnit(100);
    xaxis.setAnimated(true);

    yaxis.setUpperBound(30000);
    yaxis.setAutoRanging(true);
    yaxis.setAnimated(true);
    yaxis.setTickUnit(1000);

    datachart.setAnimated(true);
    datachart.setCreateSymbols(false);
    datachart.setAlternativeRowFillVisible(false);
    datachart.setLegendVisible(false);

    datachart.setData(lineChartData);

    datachart.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        System.out.println(event.getX());
        System.out.println(event.getY());
        zoomIn(event.getX(), event.getY());
      }
    });

    datachart.setOnScroll(new EventHandler<ScrollEvent>() {
      @Override
      public void handle(ScrollEvent event) {
        System.out.println(event.getDeltaX());
      }
    });

  }

  private void addRawData() {
    double[] calibration = model.getLibrary()
        .get(0).getWavelengthCalibrationFile().getCalibration();

    for (int j = 0; j < 1; j++) {
      Map<String, double[]> measurements = model.getLibrary()
          .get(0).getDataFiles().get(0).getMeasureSequences().get(j).getMeasurements();

      Set<Map.Entry<String, double[]>> entries = measurements.entrySet();

      for (Map.Entry<String, double[]> entry : entries) {
        double[] data = entry.getValue();
        LineChart.Series<Double, Double> series = new LineChart.Series<Double, Double>();
        series.setName(entry.getKey());
        for (int i = 0; i < data.length; i++) {
          series.getData().add(new XYChart.Data<>(calibration[i], data[i]));
        }
        lineChartData.add(series);
      }
    }
    yaxis.setLabel("DN (digital number)");
    xaxis.setLabel("Wavelength [nanometer]");
  }

  @FXML
  private void xunitAsWavelenght() {
    //requires redraw, as the calibrationfiles depend on a measuresequence
    xaxis.setLabel("Wavelength [nanometer]");
  }

  @FXML
  private void xunitAsBands() {
    int maxSize = 0;
    for (XYChart.Series<Double, Double> serie : lineChartData) {
      if (serie.getData().size() > maxSize) {
        maxSize = serie.getData().size();
      }
      for (int i = 0; i < serie.getData().size(); i++) {
        serie.getData().get(i).setXValue((double) i);
      }
    }

    xaxis.setLowerBound(0);
    xaxis.setUpperBound(maxSize);
    xaxis.setLabel("Bands");
  }


  private void showRaw() {
    /*
      Data:   as they are
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: DN (digital numbers)
     */
    lineChartData.clear();
    this.addRawData();

  }

  private void showRadiance() {
    /*
    Radiance L
      Data:
        L(VEG) = (DN(VEG) - DC(VEG)) * FLAMEradioVEG_2017-08-03
        L(WR) = (DN(WR) - DC(WR)) * FLAMEradioWR_2017-08-03
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: W/( m²sr nm) which can also be written as W m-2 sr-1 nm-1
     */

    lineChartData.clear();

    double[] waveCalibration = model.getLibrary()
        .get(0).getWavelengthCalibrationFile().getCalibration();

    double[] vegCalibration = model.getLibrary()
        .get(0).getSensorCalibrationFileVeg().getCalibration();

    double[] wrCalibration = model.getLibrary()
        .get(0).getSensorCalibrationFileWr().getCalibration();

    for (int j = 0; j < 1; j++) {
      Map<String, double[]> measurements = model.getLibrary()
          .get(0).getDataFiles().get(0).getMeasureSequences().get(j).getMeasurements();

      double[] vegs = measurements.get("VEG");
      double[] dcVegs = measurements.get("DC_VEG");

      double[] wrs = measurements.get("WR");
      double[] dcWrs = measurements.get("DC_WR");

      LineChart.Series<Double, Double> series = new LineChart.Series<Double, Double>();
      series.setName("VEG_radiance");
      for (int i = 0; i < waveCalibration.length; i++) {
        double calculatedRadiance = (vegs[i] - dcVegs[i]) * vegCalibration[i];
        series.getData().add(new XYChart.Data<>(waveCalibration[i], calculatedRadiance));
      }
      lineChartData.add(series);

      series = new LineChart.Series<Double, Double>();
      series.setName("WR_radiance");
      for (int i = 0; i < waveCalibration.length; i++) {
        double calculatedRadiance = (wrs[i] - dcWrs[i]) * wrCalibration[i];
        series.getData().add(new XYChart.Data<>(waveCalibration[i], calculatedRadiance));
      }
      lineChartData.add(series);

    }
    yaxis.setLabel("[W/( m²sr nm)]");
    xaxis.setLabel("Wavelength [nanometer]");

  }

  private void showReflectance() {
    /*
    Reflectance R
      Data:
        R(VEG) = L(VEG) / L(WR)
      X-Axis: Wavelength[Nanometers]/Bands[dn]
      Y-Axis: ReflectanceFactor (none)
     */


    lineChartData.clear();

    double[] waveCalibration = model.getLibrary()
        .get(0).getWavelengthCalibrationFile().getCalibration();

    double[] vegCalibration = model.getLibrary()
        .get(0).getSensorCalibrationFileVeg().getCalibration();

    double[] wrCalibration = model.getLibrary()
        .get(0).getSensorCalibrationFileWr().getCalibration();

    for (int j = 0; j < 1; j++) {
      Map<String, double[]> measurements = model.getLibrary()
          .get(0).getDataFiles().get(0).getMeasureSequences().get(j).getMeasurements();

      double[] vegs = measurements.get("VEG");
      double[] dcVegs = measurements.get("DC_VEG");

      double[] wrs = measurements.get("WR");
      double[] dcWrs = measurements.get("DC_WR");

      LineChart.Series<Double, Double> series = new LineChart.Series<Double, Double>();
      series.setName("Reflectance");
      for (int i = 0; i < waveCalibration.length; i++) {
        double calculatedRadiance
            = ((vegs[i] - dcVegs[i]) * vegCalibration[i])
            / ((wrs[i] - dcWrs[i]) * wrCalibration[i]);
        series.getData().add(new XYChart.Data<>(waveCalibration[i], calculatedRadiance));
      }
      lineChartData.add(series);

    }
    yaxis.setLabel("Reflectance Factor");
    xaxis.setLabel("Wavelength [nanometer]");

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

