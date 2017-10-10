package controller;

import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import model.FrvaModel;


public class TabController {
  private final FrvaModel model;


  public TabController(FrvaModel model) {
    this.model = model;
  }

  @FXML
  private LineChart<Double, Double> datachart;



  @FXML
  private void initialize() {
    addDataToGraph();
  }

  private void addDataToGraph() {

    double[] calibration = model.getLibrary()
        .get(0).getWavelengthCalibrationFile().getCalibration();
    Map<String, double[]> measurements = model.getLibrary()
        .get(0).getDataFiles().get(0).getMeasureSequences().get(0).getMeasurements();

    double[] data = measurements.get("VEG");
    LineChart.Series<Double, Double> series1 = new LineChart.Series<Double, Double>();
    series1.setName("VEG");
    for (int i = 0; i < 1023; i++) {
      series1.getData().add(new XYChart.Data<>(calibration[i], data[i]));
    }

    ObservableList<XYChart.Series<Double, Double>> lineChartData
        = FXCollections.observableArrayList();

    lineChartData.add(series1);

    data = measurements.get("WR");
    series1 = new LineChart.Series<Double, Double>();
    series1.setName("WR");
    for (int i = 0; i < 1023; i++) {
      series1.getData().add(new XYChart.Data<>(calibration[i], data[i]));
    }

    lineChartData.add(series1);


    data = measurements.get("DC_WR");
    series1 = new LineChart.Series<Double, Double>();
    series1.setName("DC_WR");
    for (int i = 0; i < 1023; i++) {
      series1.getData().add(new XYChart.Data<>(calibration[i], data[i]));
    }

    lineChartData.add(series1);

    data = measurements.get("DC_VEG");
    series1 = new LineChart.Series<Double, Double>();
    series1.setName("DC_VEG");
    for (int i = 0; i < 1023; i++) {
      series1.getData().add(new XYChart.Data<>(calibration[i], data[i]));
    }

    lineChartData.add(series1);


    datachart.setData(lineChartData);
  }

}
