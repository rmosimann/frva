package controller.util;

import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

public class ZoomWithSquare implements ZoomLineChart {

  private final Logger logger = Logger.getLogger("FRVA");

  private final LineChart<Double, Double> lineChart;
  private final NumberAxis yaxis;
  private final NumberAxis xaxis;
  private Rectangle zoomRect;
  private double xmax;
  private double xmin;
  private double ymin;
  private double ymax;
  Point2D zoomStartPoint;

  /**
   * Creates and adds Zoomfunctionality  to a LineChart and its axes.
   *
   * @param zommableNode Linechart to Zoom.
   * @param xaxis        xAxis to Zoom.
   * @param yaxis        xAxis to Zoom.
   */
  public ZoomWithSquare(LineChart<Double, Double> zommableNode,
                        NumberAxis xaxis, NumberAxis yaxis) {
    this.lineChart = zommableNode;
    this.xaxis = xaxis;
    this.yaxis = yaxis;

    addMouseListeners();
  }

  private void addMouseListeners() {
    lineChart.setOnMousePressed(event -> {
      Double inset = 10.0;
      xmax = lineChart.getWidth() - inset;
      xmin = yaxis.getWidth() + inset;
      ymin = 0 + inset;
      ymax = lineChart.getHeight() - xaxis.getHeight() - inset;


      zoomStartPoint = new Point2D(event.getX(), event.getY());

      zoomRect = new Rectangle(event.getX(), event.getY(), 0, 0);
      zoomRect.setOpacity(0.5);

      AnchorPane a = (AnchorPane) lineChart.getParent();
      if (event.getX() < xmax && event.getX() > xmin
          && event.getY() < ymax && event.getY() > ymin) {
        a.getChildren().add(zoomRect);
      }

    });

    lineChart.setOnMouseDragged(event -> {
      if (event.getX() < xmax && event.getX() > xmin
          && event.getY() < ymax && event.getY() > ymin) {
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
    });

    lineChart.setOnMouseReleased(event -> {
      AnchorPane a = (AnchorPane) lineChart.getParent();
      a.getChildren().remove(zoomRect);

    });

  }


}
