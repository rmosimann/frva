package controller;

import controller.util.ZoomLineChart;
import controller.util.ZoomWithRectangle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
  private boolean ignoreMaxToProcess = false;
  private boolean updatedelayisrunning = false;
  private final ObservableList<MeasureSequence> actualShowingSeqeunces =
      FXCollections.observableArrayList();
  private ArrayList<MeasureSequence.SequenceKeyName> filterThoseSeries = new ArrayList<>();
  private ArrayList<XYChart.Series<Double, Double>> filteredSeries = new ArrayList<>();

  private final IntegerProperty maxSeqeuncesToDisplay = new SimpleIntegerProperty(80);
  private final IntegerProperty maxSeqeuncesToProcess = new SimpleIntegerProperty(40);
  private final IntegerProperty runningUpdates = new SimpleIntegerProperty(0);
  private final BooleanProperty isDrawing = new SimpleBooleanProperty(false);

  private final String axisLabelWaveLength = "Wavelength [nanometer]";
  private final String axisLabelDigitalNumber = "DN (digital number)";
  private final String axisLabelRadiance = "Radiance [W/( m²sr nm)]";
  private final String axisLabelReflectance = "Reflectance Factor";
  private final String axisLabelBands = "Bands";

  private final DoubleProperty indexNdviAverage = new SimpleDoubleProperty();
  private final DoubleProperty indexNdviMin = new SimpleDoubleProperty();
  private final DoubleProperty indexNdviMax = new SimpleDoubleProperty();
  private final DoubleProperty indexPriAverage = new SimpleDoubleProperty();
  private final DoubleProperty indexPriMin = new SimpleDoubleProperty();
  private final DoubleProperty indexPriMax = new SimpleDoubleProperty();
  private final DoubleProperty indexTcariAverage = new SimpleDoubleProperty();
  private final DoubleProperty indexTcariMin = new SimpleDoubleProperty();
  private final DoubleProperty indexTcariMax = new SimpleDoubleProperty();

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
  private VBox vegetationIndicesBox;

  @FXML
  private Button ignoreLimitButton;

  @FXML
  private Label crossedLimitLabel;

  @FXML
  private Label indexNdviMinLabel;

  @FXML
  private Label indexNdviMaxLabel;

  @FXML
  private Label indexNdviAverageLabel;

  @FXML
  private Label indexTcariMinLabel;

  @FXML
  private Label indexTcariMaxLabel;

  @FXML
  private Label indexTcariAverageLabel;

  @FXML
  private Label indexPriMinLabel;

  @FXML
  private Label indexPriMaxLabel;

  @FXML
  private Label indexPriAverageLabel;

  @FXML
  private CheckBox checkBoxRawVeg;

  @FXML
  private CheckBox checkBoxRawWr;

  @FXML
  private CheckBox checkBoxRawDcVeg;

  @FXML
  private CheckBox checkBoxRawDcWr;

  @FXML
  private CheckBox checkBoxRadianceVeg;

  @FXML
  private CheckBox checkBoxRadianceWr;

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
  private void initialize() {
    addBindings();
    initializeLayout();
    configureRadioButtons();
    addListeners();
  }


  /**
   * Adds Bindingings to UI elements.
   */
  private void addBindings() {
    calculatingLabelBox.visibleProperty().bind(isDrawing);
    radioButtonWavelength.disableProperty().bind(isDrawing);
    radioButtonBands.disableProperty().bind(isDrawing);
    radioButtonReflectance.disableProperty().bind(isDrawing);
    radioButtonRaw.disableProperty().bind(isDrawing);
    radioButtonRadiance.disableProperty().bind(isDrawing);

    indexNdviAverageLabel.textProperty().bind(Bindings.convert(indexNdviAverage));
    indexNdviMaxLabel.textProperty().bind(Bindings.convert(indexNdviMax));
    indexNdviMinLabel.textProperty().bind(Bindings.convert(indexNdviMin));
    indexPriAverageLabel.textProperty().bind(Bindings.convert(indexPriAverage));
    indexPriMaxLabel.textProperty().bind(Bindings.convert(indexPriMax));
    indexPriMinLabel.textProperty().bind(Bindings.convert(indexPriMin));
    indexTcariAverageLabel.textProperty().bind(Bindings.convert(indexTcariAverage));
    indexTcariMaxLabel.textProperty().bind(Bindings.convert(indexTcariMax));
    indexTcariMinLabel.textProperty().bind(Bindings.convert(indexTcariMin));
  }


  /**
   * Sets defaults for all UI elements.
   */
  private void initializeLayout() {
    xaxis.setAnimated(false);
    xaxis.setForceZeroInRange(false);
    yaxis.setAnimated(false);
    yaxis.setLabel(axisLabelDigitalNumber);
    xaxis.setLabel(axisLabelWaveLength);
    datachart.setAnimated(false);
    datachart.setCreateSymbols(false);
    datachart.setAlternativeRowFillVisible(false);
    datachart.setLegendVisible(false);
    datachart.setData(lineChartData);

    ZoomLineChart zoom = new ZoomWithRectangle(datachart, xaxis, yaxis);
    zoom.activateZoomHandler();
  }


  /**
   * Adds Listeners.
   */
  private void addListeners() {

    //  Listener on the Ticked-List in the Model, executes a sleeping thread, to afterwards get
    //  multiple changes at once.
    listToWatch.addListener((ListChangeListener<MeasureSequence>) change -> {
      if (!updatedelayisrunning) {
        updatedelayisrunning = true;

        Task task = new Task<Void>() {
          @Override
          public Void call() {
            try {
              TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

        task.setOnSucceeded(e -> {
          actualShowingSeqeunces.addAll(listToWatch.filtered(sequence ->
              !actualShowingSeqeunces.contains(sequence)));
          actualShowingSeqeunces.retainAll(listToWatch);
          updatedelayisrunning = false;
        });

        model.getExecutor().execute(task);
      }
    });


    runningUpdates.addListener((observable, oldValue, newValue) -> {
      if (newValue.intValue() > 0) {
        isDrawing.setValue(true);
      } else {
        isDrawing.setValue(false);
        runningUpdates.setValue(0);
      }
    });


    // Adds listener to Tab-Local list too apply changes to the LineChart.
    actualShowingSeqeunces.addListener((ListChangeListener<MeasureSequence>) change -> {
      while (change.next()) {
        if (change.wasAdded() && (change.getAddedSubList().size() < maxSeqeuncesToProcess.getValue()
            || ignoreMaxToProcess)) {
          crossedLimitBox.setVisible(false);
          change.getAddedSubList().forEach(this::addSingleSequence);
          calculateIndices();
          ignoreMaxToProcess = false;

        } else if (change.wasRemoved()) {
          change.getRemoved().forEach(this::removeSingleSequence);
          calculateIndices();
          if (change.getAddedSubList().size() < maxSeqeuncesToProcess.getValue()) {
            crossedLimitBox.setVisible(false);
          }

        } else {
          crossedLimitBox.setVisible(true);
          crossedLimitLabel.setText("You added " + change.getAddedSubList().size()
              + " at once, this will take some time to compute.");
          ignoreLimitButton.setOnAction(event -> {
            crossedLimitBox.setVisible(false);
            ignoreMaxToProcess = true;
            change.getAddedSubList().forEach(this::addSingleSequence);
            actualShowingSeqeunces.addAll(listToWatch.filtered(sequence ->
                !actualShowingSeqeunces.contains(sequence)));
          });
          actualShowingSeqeunces.removeAll(change.getAddedSubList());
        }
      }
    });
  }

  /**
   * Calculates the VegetationIndices and updates the GUI.
   * Indices are only shown when reflectance is selected.
   */
  private void calculateIndices() {
    if (radioButtonReflectance.isSelected() && actualShowingSeqeunces.size() > 0) {
      vegetationIndicesBox.setVisible(true);

      double[] ndvi = new double[actualShowingSeqeunces.size()];
      double[] pri = new double[actualShowingSeqeunces.size()];
      double[] tcari = new double[actualShowingSeqeunces.size()];

      for (int i = 0; i < actualShowingSeqeunces.size(); i++) {
        ndvi[i] = actualShowingSeqeunces.get(i).getIndices().getNdvi();
        pri[i] = actualShowingSeqeunces.get(i).getIndices().getPri();
        tcari[i] = actualShowingSeqeunces.get(i).getIndices().getTcari();
      }

      Arrays.parallelSort(ndvi);
      Arrays.parallelSort(pri);
      Arrays.parallelSort(tcari);

      double ndviSum = 0;
      for (double value : ndvi) {
        ndviSum += value;
      }

      double priSum = 0;
      for (double value : pri) {
        priSum += value;
      }

      double tcariSum = 0;
      for (double value : tcari) {
        tcariSum += value;
      }

      double roundOnStel = 100000;

      indexNdviMin.setValue(Math.round(ndvi[0] * roundOnStel) / roundOnStel);
      indexNdviMax.setValue(Math.round(ndvi[ndvi.length - 1] * roundOnStel) / roundOnStel);
      indexNdviAverage.setValue(Math.round((ndviSum / ndvi.length) * roundOnStel) / roundOnStel);

      indexPriMin.setValue(Math.round(pri[0] * roundOnStel) / roundOnStel);
      indexPriMax.setValue(Math.round(pri[pri.length - 1] * roundOnStel) / roundOnStel);
      indexPriAverage.setValue(Math.round((priSum / pri.length) * roundOnStel) / roundOnStel);

      indexTcariMin.setValue(Math.round(tcari[0] * roundOnStel) / roundOnStel);
      indexTcariMax.setValue(Math.round(tcari[tcari.length - 1] * roundOnStel) / roundOnStel);
      indexTcariAverage.setValue(Math.round((tcariSum / tcari.length) * roundOnStel) / roundOnStel);

    } else {
      vegetationIndicesBox.setVisible(false);
    }
  }


  private void configureRadioButtons() {
    //Radiogroup y-axis
    togglGroupYaxis = new ToggleGroup();
    togglGroupYaxis.getToggles()
        .addAll(radioButtonRadiance, radioButtonRaw, radioButtonReflectance);
    togglGroupYaxis.selectToggle(radioButtonRaw);
    togglGroupYaxis.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(radioButtonRaw)) {
        yaxis.setLabel(axisLabelDigitalNumber);
      }
      if (newValue.equals(radioButtonRadiance)) {
        yaxis.setLabel(axisLabelRadiance);
      }
      if (newValue.equals(radioButtonReflectance)) {
        yaxis.setLabel(axisLabelReflectance);
      }
      redrawMeasurementSequences();
    });

    //Radiogroup x-axis
    togglGroupXaxis = new ToggleGroup();
    togglGroupXaxis.getToggles().addAll(radioButtonWavelength, radioButtonBands);
    togglGroupXaxis.selectToggle(radioButtonWavelength);
    togglGroupXaxis.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (togglGroupXaxis.getSelectedToggle().equals(radioButtonWavelength)) {
        asWavelength = true;
        xaxis.setLabel(axisLabelWaveLength);
      } else {
        asWavelength = false;
        xaxis.setLabel(axisLabelBands);
      }
      redrawMeasurementSequences();
    });


    checkBoxRawVeg.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        filterThoseSeries.add(MeasureSequence.SequenceKeyName.VEG);
      } else {
        filterThoseSeries.remove(MeasureSequence.SequenceKeyName.VEG);
      }
      filter();
    });

    checkBoxRawWr.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        filterThoseSeries.add(MeasureSequence.SequenceKeyName.WR);
      } else {
        filterThoseSeries.remove(MeasureSequence.SequenceKeyName.WR);
      }
      filter();
    });

    checkBoxRawDcVeg.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        filterThoseSeries.add(MeasureSequence.SequenceKeyName.DC_VEG);
      } else {
        filterThoseSeries.remove(MeasureSequence.SequenceKeyName.DC_VEG);
      }
      filter();
    });

    checkBoxRawDcWr.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        filterThoseSeries.add(MeasureSequence.SequenceKeyName.DC_WR);
      } else {
        filterThoseSeries.remove(MeasureSequence.SequenceKeyName.DC_WR);
      }
      filter();
    });

    checkBoxRadianceVeg.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        filterThoseSeries.add(MeasureSequence.SequenceKeyName.RADIANCE_VEG);
      } else {
        filterThoseSeries.remove(MeasureSequence.SequenceKeyName.RADIANCE_VEG);
      }
      filter();
    });

    checkBoxRadianceWr.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        filterThoseSeries.add(MeasureSequence.SequenceKeyName.RADIANCE_WR);
      } else {
        filterThoseSeries.remove(MeasureSequence.SequenceKeyName.RADIANCE_WR);
      }
      filter();
    });

  }


  /**
   * Removes all MeasurementSequences and adds them again,
   * needed when recalculating the LineChart.
   */
  private void redrawMeasurementSequences() {
    logger.info("Redrawing Graph  ");
    lineChartData.clear();
    actualShowingSeqeunces.clear();
    actualShowingSeqeunces.addAll(listToWatch);

  }


  /**
   * Removes a MeasurementSequence from the LineChart.
   *
   * @param sequence MeasurementSequence to remove.
   */
  private void removeSingleSequence(MeasureSequence sequence) {
    model.getExecutor().execute(() -> {
      runningUpdates.setValue(runningUpdates.get() + 1);
      Platform.runLater(() -> {
        lineChartData.removeIf(doubleDoubleSeries -> {
          return doubleDoubleSeries.getName().contains(sequence.getSequenceUuid());
        });

        filteredSeries.removeIf(doubleDoubleSeries -> {
          return doubleDoubleSeries.getName().contains(sequence.getSequenceUuid());
        });

        runningUpdates.setValue(runningUpdates.get() - 1);
      });
    });
  }


  /**
   * Filters the displayed series according to the ticked checkboxes under Raw and Radiance.
   * Sets the Axis to autoranging to reset the zoom level.
   */
  private void filter() {
    filteredSeries.addAll(
        lineChartData.filtered(doubleDoubleSeries -> {
          return filterThoseSeries.contains(MeasureSequence.SequenceKeyName.valueOf(
              doubleDoubleSeries.getName().split("/")[1]));
        }));

    lineChartData.removeIf(doubleDoubleSeries -> {
      return filterThoseSeries.contains(MeasureSequence.SequenceKeyName.valueOf(
          doubleDoubleSeries.getName().split("/")[1]));
    });

    lineChartData.addAll(
        filteredSeries.stream()
            .filter(doubleDoubleSeries -> {
              return !filterThoseSeries.contains(MeasureSequence.SequenceKeyName.valueOf(
                  doubleDoubleSeries.getName().split("/")[1]));
            }).collect(Collectors.toList())
    );

    filteredSeries.removeIf(doubleDoubleSeries -> {
      return !filterThoseSeries.contains(MeasureSequence.SequenceKeyName.valueOf(
          doubleDoubleSeries.getName().split("/")[1]));
    });

    xaxis.setAutoRanging(true);
    yaxis.setAutoRanging(true);

  }


  /**
   * Adds a MeasurementSequnce to the LineChart.
   *
   * @param sequence MeasurementSequence to add.
   */
  private void addSingleSequence(MeasureSequence sequence) {
    runningUpdates.setValue(runningUpdates.get() + 1);
    model.getExecutor().execute(() -> {
      xaxis.setAutoRanging(true);
      yaxis.setAutoRanging(true);

      Set<Map.Entry<MeasureSequence.SequenceKeyName, double[]>> entries = null;
      if (togglGroupYaxis.getSelectedToggle().equals(radioButtonRaw)) {
        entries = sequence.getMeasurements().entrySet();
      }
      if (togglGroupYaxis.getSelectedToggle().equals(radioButtonRadiance)) {
        entries = sequence.getRadiance().entrySet();
      }
      if (togglGroupYaxis.getSelectedToggle().equals(radioButtonReflectance)) {
        entries = sequence.getReflectance().entrySet();
      }

      double[] calibration = sequence.getWavlengthCalibration();

      for (Map.Entry<MeasureSequence.SequenceKeyName, double[]> entry : entries) {
        double[] data = entry.getValue();
        LineChart.Series<Double, Double> series = new LineChart.Series<Double, Double>();
        series.setName(sequence.getSequenceUuid() + "/" + entry.getKey());

        for (int i = 0; i < data.length; i++) {
          if (data[i] != Double.POSITIVE_INFINITY && data[i] != Double.NEGATIVE_INFINITY) {
            double x = asWavelength ? calibration[i] : i;
            double y = data[i];
            series.getData().add(new XYChart.Data<>(x, y));
          }
        }

        Platform.runLater(() -> {
          lineChartData.add(series);
          formatSerieLayout(entry.getKey(), sequence, series);
          filter();
        });

      }
      Platform.runLater(() -> {
        runningUpdates.setValue(runningUpdates.get() - 1);
      });

    });
  }


  /**
   * Adds a tooltip and MouseHoovering behaviour to a Serie.
   *
   * @param key      The key that is used to get the name from.
   * @param sequence The seuqence to get Tooltip infos from.
   * @param series   The Serie to add the Tootlip.
   */
  private void formatSerieLayout(MeasureSequence.SequenceKeyName key,
                                 MeasureSequence sequence, XYChart.Series<Double, Double> series) {
    Tooltip tooltip = new Tooltip();
    tooltip.setOpacity(0.9);

    hackTooltipStartTiming(tooltip);

    Tooltip.install(series.getNode(), tooltip);

    tooltip.getStyleClass().addAll("chart-series-line", "series" + (lineChartData.size() - 1 % 63));

    series.getNode().setOnMouseEntered(event -> {
      double xlowest = xaxis.getLowerBound();
      double xhighest = xaxis.getUpperBound();
      double ylowest = yaxis.getLowerBound();
      double yhighest = yaxis.getUpperBound();
      double pxWidth = xaxis.getWidth();
      double pxHeigth = yaxis.getHeight();
      double xvalue = (((xhighest - xlowest) / pxWidth) * event.getX()) + xlowest;
      double yvalue = (((yhighest - ylowest) / pxHeigth) * (pxHeigth - event.getY())) + ylowest;

      tooltip.setText("ID: " + sequence.getId() + "\n"
          + "Serial: " + sequence.getSerial() + "\n"
          + "Type: " + key.name() + "\n"
          + "x: " + String.valueOf(xvalue) + "\n"
          + "y: " + String.valueOf(yvalue));
    });
  }

  /**
   * Set the TooltipDelay with reflection (missing feature).
   * Found on: https://stackoverflow.com/questions/26854301/
   * how-to-control-the-javafx-tooltips-delay#27739605
   *
   * @param tooltip The Tooltip to set the delay on.
   */
  private void hackTooltipStartTiming(Tooltip tooltip) {
    try {
      Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
      fieldBehavior.setAccessible(true);
      Object objBehavior = fieldBehavior.get(tooltip);

      Field activationTimer = objBehavior.getClass().getDeclaredField("activationTimer");
      activationTimer.setAccessible(true);
      Timeline objTimer = (Timeline) activationTimer.get(objBehavior);

      objTimer.getKeyFrames().clear();
      objTimer.getKeyFrames().add(new KeyFrame(new Duration(0)));

      Field hideTimer = objBehavior.getClass().getDeclaredField("hideTimer");
      hideTimer.setAccessible(true);
      objTimer = (Timeline) hideTimer.get(objBehavior);

      objTimer.getKeyFrames().clear();
      objTimer.getKeyFrames().add(new KeyFrame(new Duration(50000)));

      Field leftTimer = objBehavior.getClass().getDeclaredField("leftTimer");
      leftTimer.setAccessible(true);
      objTimer = (Timeline) leftTimer.get(objBehavior);

      objTimer.getKeyFrames().clear();
      objTimer.getKeyFrames().add(new KeyFrame(new Duration(0)));


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
