package model.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataFile {

  private File originalFile;
  private final SdCard sdCard;
  private List<MeasureSequence> measureSequences = new ArrayList<>();
  private MeasureSequence lastAddedMeasurement;
  private int measurementLength = 5;

  /**
   * Creates a Datafile by reading in the db.csv.
   *
   * @param sdCard    SDCARD containing the Datafile.
   * @param filename  the file.
   * @param metadatas content of db.csv corresponding to this Data-file.
   */
  public DataFile(SdCard sdCard, File filename, List<String[]> metadatas) {
    this.sdCard = sdCard;
    this.originalFile = filename;
    for (String[] metadata : metadatas) {
      measureSequences.add(new MeasureSequence(metadata, this));
    }
  }

  /**
   * Constructor for reading in metadata (in case db.csv does not exist).
   *
   * @param filename Name of the file
   * @param sdCard   The SDCARD the datafile belongs to
   */
  public DataFile(SdCard sdCard, File filename) {
    System.out.println("db.csv does not exist");
    this.originalFile = filename;
    this.sdCard = sdCard;
    measureSequences = FileInOut.readInMetadataOfMeasureSequences(this);
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

  /**
   * returns true if Datafile has no measuresequences (all deleted).
   * @return a boolean.
   */
  public boolean isEmpty() {
    if (measureSequences.isEmpty()) {
      this.sdCard.deleteFile(originalFile);
      System.out.println("delete " + originalFile + " because it is empty");
      return true;
    }
    return false;
  }

  public File getOriginalFile() {
    return this.originalFile;
  }

  public MeasureSequence getLastAddedMeasurement() {
    return lastAddedMeasurement;
  }

  public int getMeasurementLength() {
    return measurementLength;
  }
}
