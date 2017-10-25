package controller;

import controller.util.ZoomLineChart;
import controller.util.ZoomWithRectangle;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

  private final IntegerProperty maxSeqeuncesToDisplay = new SimpleIntegerProperty(80);
  private final IntegerProperty maxSeqeuncesToProcess = new SimpleIntegerProperty(40);
  private final IntegerProperty runningUpdates = new SimpleIntegerProperty(0);
  private final BooleanProperty isDrawing = new SimpleBooleanProperty(false);


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


  @FXML
  private Label crossedLimitLabel;


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
  }


  /**
   * Sets defaults for all UI elements.
   */
  private void initializeLayout() {
    xaxis.setAnimated(false);
    xaxis.setForceZeroInRange(false);
    yaxis.setAnimated(false);
    yaxis.setLabel("DN (digital number)");
    xaxis.setLabel("Wavelength [nanometer]");
    datachart.setAnimated(false);
    datachart.setCreateSymbols(false);
    datachart.setAlternativeRowFillVisible(false);
    datachart.setLegendVisible(false);
    datachart.setData(lineChartData);

    ZoomLineChart zoom = new ZoomWithRectangle(datachart, xaxis, yaxis);

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
          ignoreMaxToProcess = false;

        } else if (change.wasRemoved()) {
          change.getRemoved().forEach(this::removeSingleSequence);
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


  private void configureRadioButtons() {
    //Radiogroup y-axis
    togglGroupYaxis = new ToggleGroup();
    togglGroupYaxis.getToggles()
        .addAll(radioButtonRadiance, radioButtonRaw, radioButtonReflectance);
    togglGroupYaxis.selectToggle(radioButtonRaw);
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

    //Radiogroup x-axis
    togglGroupXaxis = new ToggleGroup();
    togglGroupXaxis.getToggles().addAll(radioButtonWavelength, radioButtonBands);
    togglGroupXaxis.selectToggle(radioButtonWavelength);
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
        runningUpdates.setValue(runningUpdates.get() - 1);
      });
    });
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
        series.setName(sequence.getSequenceUuid());
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
      Platform.runLater(() -> {
        runningUpdates.setValue(runningUpdates.get() - 1);
      });

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
    tooltip.setOpacity(0.9);
    hackTooltipStartTiming(tooltip);

    Tooltip.install(series.getNode(), tooltip);

    Random rand = new Random();
    int r = rand.nextInt(255);
    Color serieColor = Color.rgb(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200));

    series.getNode().setStyle("-fx-stroke: #" + serieColor.toString().substring(2, 8)
        + "; -fx-stroke-width: 2px");

    String tooltipStyle = "-fx-background-color: #" + serieColor.toString().substring(2, 8) + ";";

    String serieStyleHoover = "-fx-stroke: #" + serieColor.toString().substring(2, 8)
        + "; -fx-stroke-width: 4px;";

    String serieStyleNormal = "-fx-stroke: #" + serieColor.toString().substring(2, 8)
        + "; -fx-stroke-width: 2px;";

    tooltip.setStyle(tooltipStyle);

    series.getNode().setOnMouseEntered(event -> {
      double xlowest = xaxis.getLowerBound();
      double xhighest = xaxis.getUpperBound();
      double ylowest = yaxis.getLowerBound();
      double yhighest = yaxis.getUpperBound();
      double pxWidth = xaxis.getWidth();
      double pxHeigth = yaxis.getHeight();
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

      Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
      fieldTimer.setAccessible(true);
      Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

      objTimer.getKeyFrames().clear();
      objTimer.getKeyFrames().add(new KeyFrame(new Duration(50)));
    } catch (Exception e) {
      e.printStackTrace();
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

}
