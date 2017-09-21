package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
  public static void parseCalibrationFiles() {

  }

  /**
   * Reads the CSV File.
   * @param pathtocsvfile path to CSV File.
   * @param delitimier Split the lines at this char.
   */
  public static void csvReader(String pathtocsvfile, String delitimier) {

    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    BufferedReader br = null;
    String line = "";

    try {

      br = new BufferedReader(new FileReader(classloader.getResource(pathtocsvfile).getPath()));
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          // use comma as separator
          String[] data = line.split(delitimier);

          System.out.println(data[0]);
        }


      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
