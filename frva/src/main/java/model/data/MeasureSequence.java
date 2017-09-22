package model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MeasureSequence {

  private final String[] metadata;
  private final List<Measurement> measurements = new ArrayList<>();

  /**
   * Constructor for a MeasurementSequence.
   * @param input a StringArray containing the measurements
   */
  public MeasureSequence(String[] input) {
    metadata = input[0].split(";");

    measurements.addAll(
        Arrays.stream(input)
        .skip(1)
        .map(input1 -> new Measurement(input1))
            .collect(Collectors.toList())
    );
  }

  public String[] getMetadata() {
    return metadata;
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }
}
