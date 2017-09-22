package model.data;

import java.util.Arrays;

public class Wavelength {
  private final double[] wavelength;


  /**
   * Constructor.
   * @param input Array of strings containing the wavelength
   */
  public Wavelength(String[] input) {
    wavelength = Arrays.stream(input)
        .mapToDouble(Double::parseDouble)
        .toArray();
  }

  public double[] getWavelength() {
    return wavelength;
  }

}
