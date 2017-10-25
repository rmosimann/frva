package controller.util;

import java.util.logging.Logger;
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

public class ZoomWithSquare implements ZoomLineChart {

  private final Logger logger = Logger.getLogger("FRVA");

  private final LineChart<Double, Double> lineChart;
  private final NumberAxis yaxis;
  private final NumberAxis xaxis;

  private double borderLeft;
  private double borderRight;
  private double borderTop;
  private double borderBottom;
  private Point2D zoomStartPoint;

  private AnchorPane anchorPane;
  private Rectangle zoomRect;

  private Mode currentMouseMode;

  private enum Mode {
    ZOOMIN,
    ZOOMOUT,
    MOVE;

  }


  Image imgZoomIn = new Image(ClassLoader.getSystemClassLoader()
      .getResource("icons/ic_zoom_in_black_24dp/web/ic_zoom_in_black_24dp_1x.png")
      .toExternalForm());

  /**
   * Creates and adds Zoomfunctionality  to a LineChart and its axes.
   *
   * @param zommableNode Linechart to ZOOMIN.
   * @param xaxis        xAxis to ZOOMIN.
   * @param yaxis        xAxis to ZOOMIN.
   */
  public ZoomWithSquare(LineChart<Double, Double> zommableNode,
                        NumberAxis xaxis, NumberAxis yaxis) {
    this.lineChart = zommableNode;
    this.xaxis = xaxis;
    this.yaxis = yaxis;

    anchorPane = (AnchorPane) lineChart.getParent();


    addZoomButtons(anchorPane);

    addMouseListeners();
  }

  private void addZoomButtons(AnchorPane anchorPane) {
    VBox zoomMenuBox = new VBox();

    Button zoomin = new Button();
    zoomin.getStyleClass().addAll("zoomMenuButton", "zoomButtonIn");

    Button zoomout = new Button();
    zoomout.getStyleClass().addAll("zoomMenuButton", "zoomButtonOut");

    Button zoomReset = new Button();
    zoomReset.getStyleClass().addAll("zoomMenuButton", "zoomButtonReset");

    Button zoomMove = new Button();
    zoomMove.getStyleClass().addAll("zoomMenuButton", "zoomButtonMove");

    Button[] zoomMenuButtons = {zoomin, zoomMove, zoomout, zoomReset};

    zoomMenuBox.getChildren().addAll(zoomin, zoomout, zoomReset, zoomMove);
    zoomMenuBox.setId("zoommenu");

    AnchorPane.setTopAnchor(zoomMenuBox, 14.0);
    AnchorPane.setRightAnchor(zoomMenuBox, 14.0);
    anchorPane.getChildren().add(zoomMenuBox);


    zoomin.setOnAction(event -> {
      currentMouseMode = Mode.ZOOMIN;
      zoomMenuBox.setCursor(new ImageCursor(imgZoomIn, imgZoomIn.getWidth() / 2,
          imgZoomIn.getHeight() / 2));
      selectedZoomButton(zoomMenuButtons, zoomin);
      System.out.println("zoomin");
    });

    zoomout.setOnAction(event -> {
      currentMouseMode = Mode.ZOOMOUT;
      selectedZoomButton(zoomMenuButtons, zoomout);
      System.out.println("zoomout");
    });

    zoomReset.setOnAction(event -> {
      currentMouseMode = null;
      zoomMenuBox.setCursor(Cursor.DEFAULT);
      selectedZoomButton(zoomMenuButtons, null);
      zoomReset();
    });

    zoomMove.setOnAction(event -> {
      currentMouseMode = Mode.MOVE;
      selectedZoomButton(zoomMenuButtons, zoomMove);
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


  private void addMouseListeners() {

    lineChart.setOnMouseEntered(event -> {
      borderLeft = yaxis.getWidth();
      borderRight = lineChart.getWidth();
      borderTop = 0;
      borderBottom = lineChart.getHeight() - xaxis.getHeight();
    });


    lineChart.setOnMouseMoved(event -> {
      if (isInChartRange(event) && Mode.MOVE.equals(currentMouseMode)) {
        System.out.println("MOVE");
      } else if (isInChartRange(event) && Mode.ZOOMOUT.equals(currentMouseMode)) {
        System.out.println("ZOOMOUT");
      } else if (isInChartRange(event) && Mode.ZOOMIN.equals(currentMouseMode)) {
        lineChart.setCursor(new ImageCursor(imgZoomIn, imgZoomIn.getWidth() / 2,
            imgZoomIn.getHeight() / 2));
      } else {
        lineChart.setCursor(Cursor.DEFAULT);
      }
    });


    lineChart.setOnMousePressed(event -> {
      if (isInChartRange(event)) {
        if (Mode.MOVE.equals(currentMouseMode)) {
          System.out.println("MOVE");
        }

        if (Mode.ZOOMIN.equals(currentMouseMode)) {
          zoomStartPoint = new Point2D(event.getX(), event.getY());
          zoomRect = new Rectangle(event.getX(), event.getY(), 0, 0);
          zoomRect.setOpacity(0.5);

          anchorPane.getChildren().add(zoomRect);
        }
      }
    });


    lineChart.setOnMouseDragged(event -> {
      if (isInChartRange(event)) {
        if (Mode.MOVE.equals(currentMouseMode)) {
          System.out.println("MOVE");
        }
        if (Mode.ZOOMIN.equals(currentMouseMode)) {
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
        }
      }
    });

    lineChart.setOnMouseReleased(event -> {
      if (isInChartRange(event)) {
        if (Mode.MOVE.equals(currentMouseMode)) {
          translate();
        }

        if (Mode.ZOOMIN.equals(currentMouseMode)) {
          Point2D upperLeft = new Point2D(zoomRect.getX() - borderLeft,
              zoomRect.getY() - borderTop);
          Point2D lowerRight = new Point2D(zoomRect.getX() - borderLeft + zoomRect.getWidth(),
              zoomRect.getY() - borderTop + zoomRect.getHeight());
          if (anchorPane.getChildren().remove(zoomRect)) {
            zoomIn(upperLeft, lowerRight);
          }
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

    System.out.println(onXaxis + " " + onYaxis);
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

  private void zoomReset() {
    xaxis.setAutoRanging(true);
    yaxis.setAutoRanging(true);
  }

  private void translate() {

  }


}
