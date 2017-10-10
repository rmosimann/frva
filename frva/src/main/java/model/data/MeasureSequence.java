package model.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

public class MeasureSequence {


  //Metadata explained
  /*
  *
0 Counter
1 Date? YYMMDD
2 hhmmss (internal clock)
3 Mode (auto/manual/app)
4 Integration time microseconds IT WR
5 Integration time microsceconds IT VEG
6 Time for one measurement miliseconds
More see https://docs.google.com/document/d/1kyKZe7tlKG4Wva3zGr00dLTMva1NG_ins3nsaOIfGDA/edit#
*/
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

  public String getId() {
    return metadata[0];
  }

  public Date getDate() {
    return new Date(Long.parseLong(metadata[1]));
  }


  public String getTime() {
    String timestamp = metadata[2];

    if (timestamp.length() == 5) {
      return "0" + timestamp.substring(0, 1) + ":" + timestamp.substring(1, 3) + ":" + timestamp.substring(3, 5);
    }
    if (timestamp.length() == 6) {
      return timestamp.substring(0, 2) + ":" + timestamp.substring(2, 4) + ":" + timestamp.substring(4, 6);
    }
    throw new IllegalArgumentException();
  }


  public String getSerial() {
    return metadata[18];
  }

}
