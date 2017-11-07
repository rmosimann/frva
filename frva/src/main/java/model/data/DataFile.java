package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataFile {

  private final File originalFile;
  private final SdCard sdCard;
  private List<MeasureSequence> measureSequences = new ArrayList<>();
  private boolean hasBeenChanged;

  /**
   * Constructor.
   *
   * @param filename Name of the file
   * @param sdCard   The SDCARD the datafile belongs to
   */
  public DataFile(SdCard sdCard, File filename) {
    this.originalFile = filename;
    this.sdCard = sdCard;

    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(filename));) {
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          if (Character.isDigit(line.charAt(0))) {

            measureSequences.add(new MeasureSequence(line, this));
            int i=0;
            while((line=br.readLine())!=null&&i<4){
              i++;
            }
          }
        }
      }
     // measureSequences.add(new MeasureSequence(line, this));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<MeasureSequence> getMeasureSequences() {
    return measureSequences;
  }

  public String getOriginalFileName() {
    return originalFile.getName();
  }

  public String getFolderName() {
    return originalFile.getParentFile().getName();
  }

  public String getDataFileName() {
    return originalFile.getName();
  }

  public SdCard getSdCard() {
    return sdCard;
  }

  public boolean isEmpty() {
    return measureSequences.isEmpty();
  }

  public void setHasBeenChanged(boolean b) {
    this.hasBeenChanged = b;
  }

  public boolean hasBeenChanged() {
    return this.hasBeenChanged;
  }
}
