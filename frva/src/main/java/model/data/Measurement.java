package model.data;

import java.util.Arrays;

public class Measurement {

  private final String name;
  private final double[] data;

  /**
   * Constructor.
   * @param input a String
   */
  public Measurement(String input) {
    String[] temp = input.split(";");

    name = temp[0];
    data = Arrays.stream(temp)
        .skip(1)
        .mapToDouble(Double::parseDouble)
        .toArray();
  }

  public String getName() {
    return name;
  }

  public double[] getData() {
    return data;
  }
}
