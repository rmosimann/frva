package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataFile {

  private final File filename;
  private List<MeasureSequence> measureSequences = new LinkedList<>();

  /**
   * Constructor.
   *
   * @param filename Name of the file
   */
  public DataFile(File filename) {
    this.filename = filename;

    List<String> fileContent = new ArrayList<>();
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(filename));) {
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          if (Character.isDigit(line.charAt(0)) && fileContent.size() > 0) {
            measureSequences.add(new MeasureSequence(fileContent));
            fileContent.clear();
          }
          fileContent.add(line);
        }
      }
      measureSequences.add(new MeasureSequence(fileContent));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<MeasureSequence> getMeasureSequences() {
    return measureSequences;
  }
}
