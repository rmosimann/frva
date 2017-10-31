package model.data;

/**
 * Represents specific indices of a measurement seuqence.
 */
public class ReflectionIndices {
  private final double[] reflections;
  private final double[] wavlengthCalibration;

  double tcari;
  double pri;
  double ndvi;


  /**
   * Creates an Object containing some specific indices.
   * Following calculations are used:
   * TCARI : 3 × ((R700 – R760) – 0.2 × (R700 – R550) × (R700/R670))
   * PRI: (R531 -R570 )/(R531 +R570 )
   * NDVI: (R920 - R696) / (R920 + R696)
   *
   * @param reflections          Double array containing the reflection.
   * @param wavlengthCalibration Double array containing the Wavelength.
   */
  public ReflectionIndices(double[] reflections, double[] wavlengthCalibration) {

    this.reflections = reflections;
    this.wavlengthCalibration = wavlengthCalibration;

    calculateIdices();
  }


  private void calculateIdices() {
    double r531 = getValueOnWavelength(531.0);
    double r550 = getValueOnWavelength(550.0);
    double r570 = getValueOnWavelength(570.0);
    double r670 = getValueOnWavelength(670.0);
    double r696 = getValueOnWavelength(696.0);
    double r700 = getValueOnWavelength(700.0);
    double r760 = getValueOnWavelength(760.0);
    double r920 = getValueOnWavelength(920.0);

    tcari = 3 * ((r700 - r760) - 0.2 * (r700 - r550) * (r700 / r670));
    pri = (r531 - r570) / (r531 + r570);
    ndvi = (r920 - r696) / (r920 + r696);
  }


  /**
   * Calculates the Vvalue of a specific Wavelength with a linear algorithm.
   *
   * @param wavelength Wavelength to get Value from.
   * @return the Value on the given wavelength.
   */
  private double getValueOnWavelength(double wavelength) {
    double upperWavelength = wavlengthCalibration[0];
    double lowerWavelength;

    int i = 0;

    while (upperWavelength < wavelength && i < wavlengthCalibration.length - 1) {
      i++;
      upperWavelength = wavlengthCalibration[i];
    }
    lowerWavelength = wavlengthCalibration[i - 1];

    double deltaX = upperWavelength - lowerWavelength;
    double deltaY = reflections[i] - reflections[i - 1];

    return ((deltaY / deltaX) * (wavelength - lowerWavelength)) + reflections[i - 1];
  }

  public double getTcari() {
    return tcari;
  }

  public double getPri() {
    return pri;
  }

  public double getNdvi() {
    return ndvi;
  }
}
