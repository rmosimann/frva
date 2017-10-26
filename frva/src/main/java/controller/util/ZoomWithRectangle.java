package controller.util;

import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class ZoomWithRectangle implements ZoomLineChart {

  private final Logger logger = Logger.getLogger("FRVA");

  private final LineChart<Double, Double> lineChart;
  private final NumberAxis yaxis;
  private final NumberAxis xaxis;

  private double borderLeft;
  private double borderRight;
  private double borderTop;
  private double borderBottom;

  private double xaxisLowerBoundBegin;
  private double xaxisUpperBoundBegin;
  private double yaxisLowerBoundBegin;
  private double yaxisUpperBoundBegin;

  private Point2D zoomStartPoint;

  private AnchorPane anchorPane;
  private Rectangle zoomRect;
  private Image zoomInImg;
  private Image zoomMoveImg;
  private Image zoomOutImg;
  private Button zoomInButton;
  private Button zoomMoveButton;
  private Button zoomOutButton;
  private Button zoomResetButton;

  private ObjectProperty<Mode> currentMouseMode = new SimpleObjectProperty<>(null);
  private BooleanProperty areAxisAutoranging = new SimpleBooleanProperty();

  private double moveStartX;
  private double moveStartY;

  private enum Mode {
    ZOOMIN,
    ZOOMOUT,
    MOVE;
  }

  /**
   * Creates and adds Zoomfunctionality  to a LineChart and its axes.
   *
   * @param zommableNode Linechart to ZOOMIN.
   * @param xaxis        xAxis to ZOOMIN.
   * @param yaxis        xAxis to ZOOMIN.
   */
  public ZoomWithRectangle(LineChart<Double, Double> zommableNode,
                           NumberAxis xaxis, NumberAxis yaxis) {
    this.lineChart = zommableNode;
    this.xaxis = xaxis;
    this.yaxis = yaxis;

    anchorPane = (AnchorPane) lineChart.getParent();

    addZoomMenu(anchorPane);
    addListeners();
    currentMouseMode.set(null);
  }

  private void addZoomMenu(AnchorPane anchorPane) {

    zoomInImg = new Image(ClassLoader.getSystemClassLoader()
        .getResource("icons/ic_zoom_in_black_24dp/web/ic_zoom_in_black_24dp_1x.png")
        .toExternalForm());
    zoomOutImg = new Image(ClassLoader.getSystemClassLoader()
        .getResource("icons/ic_zoom_out_black_24dp/web/ic_zoom_out_black_24dp_1x.png")
        .toExternalForm());
    zoomMoveImg = new Image(ClassLoader.getSystemClassLoader()
        .getResource("icons/ic_pets_black_24dp/web/ic_pets_black_24dp_1x.png")
        .toExternalForm());


    zoomInButton = new Button();
    zoomInButton.getStyleClass().addAll("zoomMenuButton", "zoomButtonIn");

    zoomOutButton = new Button();
    zoomOutButton.setDisable(true);
    zoomOutButton.getStyleClass().addAll("zoomMenuButton", "zoomButtonOut");

    zoomResetButton = new Button();
    zoomResetButton.setDisable(true);
    zoomResetButton.getStyleClass().addAll("zoomMenuButton", "zoomButtonReset");

    zoomMoveButton = new Button();
    zoomMoveButton.setDisable(true);
    zoomMoveButton.getStyleClass().addAll("zoomMenuButton", "zoomButtonMove");

    Button[] zoomMenuButtons = {zoomInButton, zoomMoveButton, zoomOutButton, zoomResetButton};

    VBox zoomMenuBox = new VBox();

    zoomMenuBox.getChildren().addAll(zoomInButton, zoomOutButton, zoomResetButton, zoomMoveButton);
    zoomMenuBox.setId("zoommenu");

    AnchorPane.setTopAnchor(zoomMenuBox, 14.0);
    AnchorPane.setRightAnchor(zoomMenuBox, 14.0);
    anchorPane.getChildren().add(zoomMenuBox);


    zoomInButton.setOnAction(event -> {
      currentMouseMode.setValue(Mode.ZOOMIN);
      zoomMenuBox.setCursor(new ImageCursor(zoomInImg, zoomInImg.getWidth() / 2,
          zoomInImg.getHeight() / 2));
      selectedZoomButton(zoomMenuButtons, zoomInButton);
    });

    zoomOutButton.setOnAction(event -> {
      currentMouseMode.setValue(Mode.ZOOMOUT);
      zoomMenuBox.setCursor(new ImageCursor(zoomOutImg, zoomOutImg.getWidth() / 2,
          zoomOutImg.getHeight() / 2));
      selectedZoomButton(zoomMenuButtons, zoomOutButton);
    });

    zoomResetButton.setOnAction(event -> {
      currentMouseMode.setValue(null);
      zoomMenuBox.setCursor(Cursor.DEFAULT);
      selectedZoomButton(zoomMenuButtons, null);
      zoomReset();
    });

    zoomMoveButton.setOnAction(event -> {
      currentMouseMode.setValue(Mode.MOVE);
      zoomMenuBox.setCursor(new ImageCursor(zoomMoveImg, zoomMoveImg.getWidth() / 2,
          zoomMoveImg.getHeight() / 2));
      selectedZoomButton(zoomMenuButtons, zoomMoveButton);
    });
  }

  private void selectedZoomButton(Button[] zoomMenuButtons, Button selectedButton) {
    String styleName = "selected";
    for (Button button :
        zoomMenuButtons) {
      button.getStyleClass().remove(styleName);
    }
    if (selectedButton != null) {
      selectedButton.getStyleClass().add(styleName);
    }
  }


  private void addListeners() {

    areAxisAutoranging.bind(xaxis.autoRangingProperty().and(yaxis.autoRangingProperty()));

    areAxisAutoranging.addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        xaxisLowerBoundBegin = xaxis.getLowerBound();
        xaxisUpperBoundBegin = xaxis.getUpperBound();
        yaxisLowerBoundBegin = yaxis.getLowerBound();
        yaxisUpperBoundBegin = yaxis.getUpperBound();
      }
    });

    zoomOutButton.disableProperty().bind(areAxisAutoranging);
    zoomResetButton.disableProperty().bind(areAxisAutoranging);
    zoomMoveButton.disableProperty().bind(areAxisAutoranging);


    lineChart.setOnMouseEntered(event -> {
      borderLeft = yaxis.getWidth();
      borderRight = lineChart.getWidth();
      borderTop = 0;
      borderBottom = lineChart.getHeight() - xaxis.getHeight();
    });


    lineChart.setOnMouseMoved(event -> {
      if (isInChartRange(event) && Mode.MOVE.equals(currentMouseMode.getValue())) {
        lineChart.setCursor(new ImageCursor(zoomMoveImg, zoomMoveImg.getWidth() / 2,
            zoomMoveImg.getHeight() / 2));
      } else if (isInChartRange(event) && Mode.ZOOMOUT.equals(currentMouseMode.getValue())) {
        lineChart.setCursor(new ImageCursor(zoomOutImg, zoomOutImg.getWidth() / 2,
            zoomOutImg.getHeight() / 2));
      } else if (isInChartRange(event) && Mode.ZOOMIN.equals(currentMouseMode.getValue())) {
        lineChart.setCursor(new ImageCursor(zoomInImg, zoomInImg.getWidth() / 2,
            zoomInImg.getHeight() / 2));
      } else {
        lineChart.setCursor(Cursor.DEFAULT);
      }
    });


    lineChart.setOnMousePressed(event -> {
      if (isInChartRange(event)) {
        if (Mode.MOVE.equals(currentMouseMode.getValue())) {
          System.out.println("MOVE");
          moveStartX = event.getX();
          moveStartY = event.getY();
        }

        if (Mode.ZOOMIN.equals(currentMouseMode.getValue())) {
          zoomStartPoint = new Point2D(event.getX(), event.getY());
          zoomRect = new Rectangle(event.getX(), event.getY(), 0, 0);
          zoomRect.getStyleClass().add("zoomRect");
          anchorPane.getChildren().add(zoomRect);
        }
      }
    });


    lineChart.setOnMouseDragged(event -> {
      if (isInChartRange(event)) {
        if (Mode.MOVE.equals(currentMouseMode.getValue())) {
          Point2D moveDelta = calculateAxisFromPoint(new Point2D(event.getX() - moveStartX,
              event.getY() - moveStartY + yaxis.getHeight()));

          xaxis.setLowerBound(xaxis.getLowerBound() - moveDelta.getX());
          xaxis.setUpperBound(xaxis.getUpperBound() - moveDelta.getX());
          yaxis.setLowerBound(yaxis.getLowerBound() - moveDelta.getY());
          yaxis.setUpperBound(yaxis.getUpperBound() - moveDelta.getY());

          moveStartX = event.getX();
          moveStartY = event.getY();
        }

        if (Mode.ZOOMIN.equals(currentMouseMode.getValue())) {
          //Right
          if (event.getX() > zoomStartPoint.getX()) {
            zoomRect.setWidth(event.getX() - zoomRect.getX());
          } else {
            zoomRect.setWidth(zoomStartPoint.getX() - event.getX());
            zoomRect.setX(event.getX());
          }

          if (event.getY() > zoomStartPoint.getY()) {
            zoomRect.setHeight(event.getY() - zoomRect.getY());
          } else {
            zoomRect.setHeight(zoomStartPoint.getY() - event.getY());
            zoomRect.setY(event.getY());
          }

          if (zoomRect.getWidth() < 50 && zoomRect.getHeight() < 50) {
            zoomRect.getStyleClass().add("zoomRectToSmall");
          } else {
            zoomRect.getStyleClass().remove("zoomRectToSmall");
          }

        }
      }
    });

    lineChart.setOnMouseReleased(event -> {
      if (isInChartRange(event) && Mode.ZOOMOUT.equals(currentMouseMode.getValue())) {
        zoomOut(new Point2D(event.getX(), event.getY()));
      }

      if (isInChartRange(event) && Mode.ZOOMIN.equals(currentMouseMode.getValue())) {
        Point2D upperLeft = new Point2D(zoomRect.getX() - borderLeft,
            zoomRect.getY() - borderTop);
        Point2D lowerRight = new Point2D(zoomRect.getX() - borderLeft + zoomRect.getWidth(),
            zoomRect.getY() - borderTop + zoomRect.getHeight());
        if (anchorPane.getChildren().remove(zoomRect)
            && (lowerRight.getX() - upperLeft.getX()) > 50
            && (lowerRight.getY() - upperLeft.getY()) > 50) {
          zoomIn(upperLeft, lowerRight);
        }
      }

    });
  }

  private boolean isInChartRange(MouseEvent event) {
    return event.getX() > borderLeft && event.getX() < borderRight
        && event.getY() > borderTop && event.getY() < borderBottom;
  }


  private Point2D calculateAxisFromPoint(Point2D point2D) {
    double pxWidth = xaxis.getWidth();
    double pxHeigth = yaxis.getHeight();
    double onXaxis = ((xaxis.getUpperBound() - xaxis.getLowerBound()) / pxWidth) * (point2D.getX());
    double onYaxis = (
        (yaxis.getUpperBound() - yaxis.getLowerBound()) / pxHeigth) * (pxHeigth - (point2D.getY()));

    return new Point2D(onXaxis, onYaxis);
  }


  private void zoomIn(Point2D upperLeft, Point2D lowerRight) {
    xaxis.setAutoRanging(false);
    yaxis.setAutoRanging(false);

    Point2D upperLeftAxis = calculateAxisFromPoint(upperLeft);
    Point2D lowerRightAxis = calculateAxisFromPoint(lowerRight);

    xaxis.setUpperBound(Math.round(lowerRightAxis.getX() + xaxis.getLowerBound()));
    yaxis.setUpperBound(Math.round(upperLeftAxis.getY() + yaxis.getLowerBound()));
    xaxis.setLowerBound(Math.round(upperLeftAxis.getX() + xaxis.getLowerBound()));
    yaxis.setLowerBound(Math.round(lowerRightAxis.getY() + yaxis.getLowerBound()));
  }


  private void zoomOut(Point2D point2D) {
    double zoomFactor = 10;

    double xzoomstep = (xaxis.getUpperBound() - xaxis.getLowerBound()) / zoomFactor;
    double yzoomstep = (yaxis.getUpperBound() - yaxis.getLowerBound()) / zoomFactor;

    double zoomLeft = (xzoomstep / lineChart.getWidth()) * point2D.getX();
    if ((xaxis.getLowerBound() - zoomLeft) > xaxisLowerBoundBegin) {
      xaxis.setLowerBound(xaxis.getLowerBound() - zoomLeft);
    } else {
      xaxis.setLowerBound(xaxisLowerBoundBegin);
    }

    double zoomRight = (xzoomstep / lineChart.getWidth()) * (lineChart.getWidth() - point2D.getX());
    if ((xaxis.getUpperBound() + zoomRight) < xaxisUpperBoundBegin) {
      xaxis.setUpperBound(xaxis.getUpperBound() + zoomRight);
    } else {
      xaxis.setUpperBound(xaxisUpperBoundBegin);
    }

    double zoomDown = (yzoomstep / lineChart.getHeight())
        * (lineChart.getHeight() - point2D.getY());
    if (yaxis.getLowerBound() - zoomDown > yaxisLowerBoundBegin) {
      yaxis.setLowerBound(yaxis.getLowerBound() - zoomDown);
    } else {
      yaxis.setLowerBound(yaxisLowerBoundBegin);
    }

    double zoomUp = (yzoomstep / lineChart.getHeight()) * point2D.getY();
    if (yaxis.getUpperBound() + zoomUp < yaxisUpperBoundBegin) {
      yaxis.setUpperBound(yaxis.getUpperBound() + zoomUp);
    } else {
      yaxis.setUpperBound(yaxisUpperBoundBegin);
    }

    if (xaxis.getUpperBound() == xaxisUpperBoundBegin
        && xaxis.getLowerBound() == xaxisLowerBoundBegin
        && yaxis.getUpperBound() == yaxisUpperBoundBegin
        && yaxis.getLowerBound() == yaxisLowerBoundBegin) {
      zoomReset();
    }

  }

  private void zoomReset() {
    xaxis.setAutoRanging(true);
    yaxis.setAutoRanging(true);
  }

}
