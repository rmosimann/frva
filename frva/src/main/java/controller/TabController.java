package controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
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
  private final List<MeasureSequence> actualShowingSeqeunces = new ArrayList<>();

  private final IntegerProperty runningTasks = new SimpleIntegerProperty(0);
  private final IntegerProperty maxSeqeuncesToDisplay = new SimpleIntegerProperty(30);

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
  private VBox calculatingLabelBox;

  @FXML
  private VBox crossedLimitBox;

  @FXML
  private Button ignoreLimitButton;

  private boolean updatedelayisrunning = false;

  @FXML
  private void initialize() {
    configureRadioButtons();
    initializeLayout();
    addListeners();
  }

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


  /**
   * Adds Listeners.
   */
  private void addListeners() {
    runningTasks.addListener((observable, oldValue, newValue) -> {
      if (newValue.intValue() == 0) {
        calculatingLabelBox.setVisible(false);
      } else {
        calculatingLabelBox.setVisible(true);
      }
    });




    listToWatch.addListener(new ListChangeListener<MeasureSequence>() {
      @Override
      public void onChanged(Change<? extends MeasureSequence> c) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        if (!updatedelayisrunning) {
          updatedelayisrunning = true;
          executorService.schedule(new Runnable() {
            @Override
            public void run() {
              addMultipleMeasurementSequences(true);
            }
          }, 10, TimeUnit.MILLISECONDS);
          executorService.shutdown();
        }
      }
    });
  }


  private void configureRadioButtons() {
    //Radiogroup y-axis
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
      redrawMeasurementSequences();

    });
    togglGroupYaxis.selectToggle(radioButtonRaw);

    //Radiogroup x-axis
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
      redrawMeasurementSequences();
    });
    togglGroupXaxis.selectToggle(radioButtonWavelength);
  }


  private void initializeLayout() {
    xaxis.setAnimated(false);
    xaxis.setForceZeroInRange(false);
    yaxis.setAnimated(false);
    datachart.setAnimated(false);
    datachart.setCreateSymbols(false);
    datachart.setAlternativeRowFillVisible(false);
    datachart.setLegendVisible(false);
    datachart.setData(lineChartData);

    datachart.setOnScroll(event -> {
      if (event.getDeltaY() < 0) {
        zoomOut(event.getX(), event.getY());
      } else {
        zoomIn(event.getX(), event.getY());
      }
    });
  }


  /**
   * Removes all MeasurementSequences and adds them again,
   * this is needed when recalculating the LineChart.
   */
  private void redrawMeasurementSequences() {
    logger.info("Redrawing Graph  ");
    lineChartData.clear();
    actualShowingSeqeunces.clear();
    addMultipleMeasurementSequences(true);
  }

  private void addMultipleMeasurementSequences(boolean checkLimit) {
    updatedelayisrunning = false;
    int difference = listToWatch.size() - actualShowingSeqeunces.size();
    System.out.println(difference);
    if (checkLimit && difference > maxSeqeuncesToDisplay.getValue()) {
      crossedLimitBox.setVisible(true);
      ignoreLimitButton.setOnAction(event -> {
        crossedLimitBox.setVisible(false);
        addMultipleMeasurementSequences(false);
      });
    } else {
      crossedLimitBox.setVisible(false);
      listToWatch.forEach(sequence -> {
        if (!actualShowingSeqeunces.contains(sequence)) {
          addSingleSequence(sequence);
        }
      });
      actualShowingSeqeunces.forEach(sequence -> {
        if (!listToWatch.contains(sequence)) {
          Platform.runLater(() -> removeSingleSequence(sequence));

        }
      });
    }
  }


  /**
   * Removes a MeasurementSequence from the LineChart.
   *
   * @param sequence MeasurementSequence to remove.
   */
  private void removeSingleSequence(MeasureSequence sequence) {
    actualShowingSeqeunces.remove(sequence);
    lineChartData.removeIf(doubleDoubleSeries -> {
      return doubleDoubleSeries.getName().contains(sequence.getSerial())
          && doubleDoubleSeries.getName().contains("ID" + sequence.getId());
    });
  }


  /**
   * Adds a MeasurementSequnce to the LineChart.
   *
   * @param sequence MeasurementSequence to add.
   */
  private void addSingleSequence(MeasureSequence sequence) {
    runningTasks.setValue(runningTasks.get() + 1);
    actualShowingSeqeunces.add(sequence);
    model.getExecutor().execute(new Runnable() {
      @Override
      public void run() {
        xaxis.setAutoRanging(true);
        yaxis.setAutoRanging(true);

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

          Platform.runLater(() -> {
            lineChartData.add(series);
            formatSerieLayout(sequence, series);
          });
        }
        Platform.runLater(() -> runningTasks.setValue(runningTasks.get() - 1));
      }
    });
  }


  /**
   * Adds a tooltip and MouseHoovering behaviour to a Serie.
   *
   * @param sequence The seuqence to get Tooltip infos from.
   * @param series   The Serie to add the Tootlip.
   */
  private void formatSerieLayout(MeasureSequence sequence, XYChart.Series<Double, Double> series) {
    Tooltip tooltip = new Tooltip();
    Tooltip.install(series.getNode(), tooltip);

    Random rand = new Random();
    Color serieColor = new Color(rand.nextInt(200),
        rand.nextInt(200), rand.nextInt(200));

    series.getNode().setStyle("-fx-stroke: rgba(" + serieColor.getRed()
        + "," + serieColor.getGreen() + "," + serieColor.getBlue() + ");"
        + "-fx-stroke-width: 2px");


    String tooltipStyle = "-fx-background-color: rgba(" + serieColor.getRed()
        + "," + serieColor.getGreen() + "," + serieColor.getBlue() + ");";
    String serieStyleHoover = "-fx-stroke: rgba(" + serieColor.getRed() + "," + serieColor.getGreen() + "," + serieColor.getBlue() + ");"
        + "-fx-stroke-width: 4px";
    String serieStyleNormal = "-fx-stroke: rgba(" + serieColor.getRed() + "," + serieColor.getGreen() + "," + serieColor.getBlue() + ");"
        + "-fx-stroke-width: 2px";


    tooltip.setStyle(tooltipStyle);

    series.getNode().setOnMouseEntered(event -> {
      double xlowest = xaxis.getLowerBound();
      double xhighest = xaxis.getUpperBound();
      double ylowest = yaxis.getLowerBound();
      double yhighest = yaxis.getUpperBound();
      double pxWidth = series.getNode().getParent().getParent().getBoundsInLocal().getWidth();
      double pxHeigth = series.getNode().getParent().getParent().getBoundsInLocal().getHeight();
      double xvalue = (((xhighest - xlowest) / pxWidth) * event.getX()) + xlowest;
      double yvalue = (((yhighest - ylowest) / pxHeigth) * (pxHeigth - event.getY())) + ylowest;

      series.getNode().setStyle(serieStyleHoover);

      tooltip.setText("ID: " + sequence.getId() + "\n"
          + "Serial: " + sequence.getSerial() + "\n"
          + "x: " + String.valueOf(xvalue) + "\n"
          + "y: " + String.valueOf(yvalue));
    });

    series.getNode().setOnMouseExited(event -> {
      series.getNode().setStyle(serieStyleNormal);
    });
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
    logger.info("Zooming in");

    xaxis.setAutoRanging(false);
    yaxis.setAutoRanging(false);

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


  private void zoomOut(double xpos, double ypos) {
    logger.info("Zooming out");

    xaxis.setAutoRanging(false);
    yaxis.setAutoRanging(false);

    double zoomFactor = 10;

    double xzoomstep = (xaxis.getUpperBound() - xaxis.getLowerBound()) / zoomFactor;
    double yzoomstep = (yaxis.getUpperBound() - yaxis.getLowerBound()) / zoomFactor;

    double partLeft = xpos;
    double zoomLeft = (xzoomstep / datachart.getWidth()) * partLeft;
    xaxis.setLowerBound(xaxis.getLowerBound() - zoomLeft);

    double partRight = datachart.getWidth() - xpos;
    double zoomRight = (xzoomstep / datachart.getWidth()) * partRight;
    xaxis.setUpperBound(xaxis.getUpperBound() + zoomRight);

    double partDown = datachart.getHeight() - ypos;
    double zoomDown = (yzoomstep / datachart.getHeight()) * partDown;
    yaxis.setLowerBound(yaxis.getLowerBound() - zoomDown);

    double partUp = ypos;
    double zoomUp = (yzoomstep / datachart.getHeight()) * partUp;
    yaxis.setUpperBound(yaxis.getUpperBound() + zoomUp);
  }
}

