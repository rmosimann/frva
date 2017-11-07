package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import model.FrvaModel;

public class DataFile {

  private File originalFile;
  private final SdCard sdCard;
  private List<MeasureSequence> measureSequences = new ArrayList<>();
  private boolean hasBeenChanged;

  public MeasureSequence getLastAddedMeasurement() {
    return lastAddedMeasurement;
  }

  private MeasureSequence lastAddedMeasurement;

  /**
   * Constructor for reading in full file
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
            int i = 0;
            while ((line = br.readLine()) != null && i < 4) {
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


  /**
   * Constructor for reading in single file
   *
   * @param sdCard   The SDCARD the datafile belongs to
   */
  public DataFile(SdCard sdCard, File containingFile, String id) {
    this.originalFile = containingFile;
    this.sdCard = sdCard;

    boolean found = false;
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(containingFile))) {
      while ((line = br.readLine()) != null) {

        //   System.out.println(line);
        if (line.length() > 1 && Character.isDigit(line.charAt(0))) {
          if (line.split(";")[0].equals(id)) {
            found = true;
            lastAddedMeasurement = new MeasureSequence(line,this);
            this.measureSequences.add(lastAddedMeasurement);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

   if(!found){ throw new NoSuchElementException("Element with ID " + id + " has not been found in file " + containingFile);}


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

  public File getOriginalFile() {
    return this.originalFile;
  }

  public void setPathToLibrary() {
    originalFile = new File(FrvaModel.LIBRARYPATH + File.separator + sdCard.getName() + File.separator + originalFile.getParent()+File.separator+originalFile.getName());
    System.out.println("*****Set path to: "+originalFile.getAbsolutePath());
  }

}
