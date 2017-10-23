package controller.util;

import java.util.logging.Logger;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class ZoomByMouseScroll implements LineChartZoom {

  private final Logger logger = Logger.getLogger("FRVA");

  private final LineChart<Double, Double> datachart;
  private final NumberAxis yaxis;
  private final NumberAxis xaxis;


  /**
   * Creates and adds Zoomfunctionality  to a LineChart and its axes.
   *
   * @param zommableNode Linechart to Zoom.
   * @param xaxis        xAxis to Zoom.
   * @param yaxis        xAxis to Zoom.
   */
  public ZoomByMouseScroll(LineChart<Double, Double> zommableNode,
                           NumberAxis xaxis, NumberAxis yaxis) {
    this.datachart = zommableNode;
    this.xaxis = xaxis;
    this.yaxis = yaxis;

    datachart.setOnScroll(event -> {
      if (event.getDeltaY() < 0) {
        zoomOut(event.getX(), event.getY());
      } else {
        zoomIn(event.getX(), event.getY());
      }
    });
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
