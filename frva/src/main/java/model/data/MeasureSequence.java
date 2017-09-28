package model.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeasureSequence {

  private final String[] metadata;
  private final Map<String, double[]> measurements = new HashMap<>();

  /**
   * Constructor for a MeasurementSequence.
   *
   * @param input a StringArray containing the measurements
   */
  public MeasureSequence(List<String> input) {
    metadata = input.get(0).split(";");

    System.out.println("Creating new sequence: " + metadata[0]);
    for (int i = 1; i < input.size(); i++) {
      String[] tmp = input.get(i).split(";");
      measurements.put(tmp[0], Arrays.stream(Arrays.copyOfRange(tmp, 1, tmp.length))
          .mapToDouble(Double::parseDouble)
          .toArray());
    }
  }

  public String[] getMetadata() {
    return metadata;
  }


  public Map<String, double[]> getMeasurements() {
    return measurements;
  }

  /**
   * Prints the content of the MeasureSequence to the console.
   */
  public void print() {
    Arrays.stream(metadata).forEach(a -> System.out.print(a + " "));

    for (Map.Entry<String, double[]> entry : measurements.entrySet()) {
      System.out.println();
      System.out.print(entry.getKey());
      Arrays.stream(entry.getValue()).forEach(a -> System.out.print(a + " "));
    }
  }
}
