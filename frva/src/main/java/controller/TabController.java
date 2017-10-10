package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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
  private NumberAxis xaxis;

  @FXML
  private NumberAxis yaxis;

  @FXML
  private void initialize() {
    addDataToGraph();
  }

  private void addDataToGraph() {

    LineChart.Series<Double, Double> series1 = new LineChart.Series<Double, Double>();
    series1.setName("Series 1");
    series1.getData().add(new XYChart.Data<Double, Double>(0.0, 1.0));
    series1.getData().add(new XYChart.Data<Double, Double>(1.2, 1.4));
    series1.getData().add(new XYChart.Data<Double, Double>(2.2, 1.9));
    series1.getData().add(new XYChart.Data<Double, Double>(2.7, 2.3));
    series1.getData().add(new XYChart.Data<Double, Double>(2.9, 0.5));

    ObservableList<XYChart.Series<Double, Double>> lineChartData
        = FXCollections.observableArrayList();

    lineChartData.add(series1);

    LineChart.Series<Double, Double> series2 = new LineChart.Series<Double, Double>();
    series2.setName("Series 2");
    series2.getData().add(new XYChart.Data<>(0.0, 1.6));
    series2.getData().add(new XYChart.Data<Double, Double>(0.8, 0.4));
    series2.getData().add(new XYChart.Data<Double, Double>(1.4, 2.9));
    series2.getData().add(new XYChart.Data<Double, Double>(2.1, 1.3));
    series2.getData().add(new XYChart.Data<Double, Double>(2.6, 0.9));

    lineChartData.add(series2);

    datachart.setData(lineChartData);
  }

}
