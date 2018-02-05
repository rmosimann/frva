/*
 * Copyright 2018 Andreas Hüni
 *
 * This file is part of FRVA.
 *
 *     FRVA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FRVA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU LesserGeneral Public License
 *     along with FRVA.  If not, see <http://www.gnu.org/licenses/>.
 */

package model.data;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by patrick.wigger on 12.01.18.
 */
public class FileInOutTest {
  private SdCard testSdCard;
  private File testLibraryPath;


  @Before
  public void setUp() throws Exception {
    File sdCardResourcePath = new File(getClass().getClassLoader().getResource("testCards/SDCARD_OK").getFile());
    testLibraryPath = new File(getClass().getClassLoader().getResource("TESTLIBRARY").getFile());
    if (!testLibraryPath.exists()) {
      testLibraryPath.mkdir();
    }
    File sdCardTestPath = new File(testLibraryPath.getPath() + File.separator + "test");
    FileUtils.copyDirectory(sdCardResourcePath, sdCardTestPath);
    testSdCard = new SdCard(sdCardTestPath, "test");
  }

  @After
  public void tearDown() throws Exception {
    FileUtils.deleteDirectory(testLibraryPath);
    new File(testLibraryPath + File.separator).mkdir();
  }

  @Test
  public void writeDatabaseFile() throws Exception {
    FileInOut.writeDatabaseFile(testSdCard, testLibraryPath.getPath());
    File sdCard = new File(testLibraryPath + File.separator + "test");
    assert sdCard.exists();
    File[] files = sdCard.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return "db.csv".equals(name);
      }
    });
    assert files.length == 1;

    BufferedReader br = new BufferedReader(new FileReader(files[0]));
    int i = 0;
    while (br.readLine() != null) {
      i++;
    }
    assert i == testSdCard.getMeasureSequences().size();
  }


  @Test
  public void readDatafilesLazyWithoutDbFile() throws Exception {
    List<DataFile> dataFiles = FileInOut.readDatafilesLazy(testSdCard);
    assert dataFiles.size() == 1;
    DataFile df = dataFiles.get(0);
    assert df.getMeasureSequences().size() == 38;
  }

  @Test
  public void readDatafilesLazyWithDbFile() throws Exception {
    FileInOut.writeDatabaseFile(testSdCard, testLibraryPath.getPath());
    List<DataFile> dataFiles = FileInOut.readDatafilesLazy(testSdCard);
    assert dataFiles.size() == 1;
    DataFile df = dataFiles.get(0);
    assert df.getMeasureSequences().size() == 38;
  }


  @Test
  public void readCalibrationFile() throws Exception {
    CalibrationFile cf = FileInOut.readCalibrationFile(testSdCard, "cal.csv");
    assert cf != null;
    InputStream is = getClass().getClassLoader().getResourceAsStream("TESTLIBRARY" + File.separator + "test" + File.separator + "cal.csv");
    String cal = IOUtils.toString(is, "UTF-8");
    assert cf.calibrationAsString().equals(cal);
  }


  @Test
  public void readInMetadataOfMeasureSequences() throws Exception {
    List<MeasureSequence> list = FileInOut.readInMetadataOfMeasureSequences(testSdCard
        .getDataFiles().get(0));
    assert list.size() == 38;
    InputStream is = getClass().getClassLoader().getResourceAsStream("TESTLIBRARY"
        + File.separator + "test" + File.separator + "651221" + File.separator + "F153459.CSV");
    String dfile = IOUtils.toString(is, "UTF-8");
    assert dfile.startsWith(list.get(0).getMetadataAsString());
  }

  @Test
  public void removeMeasureSequences() throws Exception {
    List<MeasureSequence> ms = testSdCard.getMeasureSequences();
    List<MeasureSequence> testMs = new ArrayList<>();
    testMs.add(ms.get(0));
    testMs.add(ms.get(5));
    testMs.add(ms.get(8));
    testMs.add(ms.get(37));


    FileInOut.removeMeasureSequences(testSdCard
        .getDataFiles().get(0), testMs);

    InputStream is = getClass().getClassLoader().getResourceAsStream("TESTLIBRARY"
        + File.separator + "test" + File.separator + "651221" + File.separator + "F153459.CSV");
    String dfile = IOUtils.toString(is, "UTF-8");
    assert dfile.startsWith("2;651221");
    assert !dfile.contains(ms.get(5).getMetadataAsString());
    assert !dfile.contains(ms.get(8).getMetadataAsString());
    assert !dfile.contains(ms.get(37).getMetadataAsString());
  }


  @Test
  public void readInMeasurement() throws Exception {
    Map<MeasureSequence.SequenceKeyName, double[]> map
        = FileInOut.readInMeasurement(testSdCard.getMeasureSequences().get(0));

    InputStream is = getClass().getClassLoader().getResourceAsStream("TESTLIBRARY"
        + File.separator + "test" + File.separator + "651221" + File.separator + "F153459.CSV");

    String dfile = IOUtils.toString(is, "UTF-8");
    String[] arr = dfile.split("\n");

    String[] wr = arr[1].split(";");
    String[] veg = arr[2].split(";");
    String[] wr2 = arr[3].split(";");
    String[] dcwr = arr[4].split(";");
    String[] dcveg = arr[5].split(";");
    for (int i = 1; i < wr.length; i++) {
      assert Double.parseDouble(wr[i]) == map.get(MeasureSequence.SequenceKeyName.WR)[i - 1];
      assert Double.parseDouble(veg[i]) == map.get(MeasureSequence.SequenceKeyName.VEG)[i - 1];
      assert Double.parseDouble(wr2[i]) == map.get(MeasureSequence.SequenceKeyName.WR2)[i - 1];
      assert Double.parseDouble(dcwr[i]) == map.get(MeasureSequence.SequenceKeyName.DC_WR)[i - 1];
      assert Double.parseDouble(dcveg[i]) == map.get(MeasureSequence.SequenceKeyName.DC_VEG)[i - 1];
    }
  }


  @Test
  public void createFiles() throws Exception {
    Path exportPath = new File(testLibraryPath.getPath() + File.separator + "export").toPath();
    FileInOut.createFiles(testSdCard.getMeasureSequences(), exportPath,
        null);
    assert exportPath.toFile().exists();
    File export = new File(exportPath.toFile().getPath() + File.separator + "test");
    assert export.listFiles((dir, name) -> name.equals("cal.csv"))[0] != null;
    File dataFolder = export.listFiles((dir, name) -> name.equals("651221"))[0];

    assert dataFolder.exists();
    assert dataFolder.isDirectory();

    File dataFile = dataFolder.listFiles((dir, name) -> name.equals("F153459.CSV"))[0];
    assert dataFile.exists();
    assert dataFile.length() != 0;

    InputStream is = getClass().getClassLoader().getResourceAsStream("TESTLIBRARY"
        + File.separator + "test" + File.separator + "651221" + File.separator + "F153459.CSV");
    String origFile = IOUtils.toString(is, "UTF-8");
    InputStream is2 = getClass().getClassLoader().getResourceAsStream("TESTLIBRARY"
        + File.separator + "export" + File.separator + "test" + File.separator + "651221" + File.separator + "F153459.CSV");
    String exportFile = IOUtils.toString(is2, "UTF-8");
    assertEquals(origFile, exportFile);
  }


  @Test
  public void writeLiveMeasurements() throws Exception {
    File liveSdCard = new File(testLibraryPath.getPath() + File.separator + "LIVESDCARD");
    MeasureSequence ms = testSdCard.getMeasureSequences().get(0);
    Map<MeasureSequence.SequenceKeyName, double[]> sampleData = ms.getData();

    LiveMeasureSequence lms = new LiveMeasureSequence(testSdCard.getCalibrationFile());
    lms.addData(MeasureSequence.SequenceKeyName.WR, sampleData.get(MeasureSequence.SequenceKeyName.WR));
    lms.addData(MeasureSequence.SequenceKeyName.WR2, sampleData.get(MeasureSequence.SequenceKeyName.WR2));
    lms.addData(MeasureSequence.SequenceKeyName.DC_VEG, sampleData.get(MeasureSequence.SequenceKeyName.DC_VEG));
    lms.addData(MeasureSequence.SequenceKeyName.DC_WR, sampleData.get(MeasureSequence.SequenceKeyName.DC_WR));
    lms.addData(MeasureSequence.SequenceKeyName.VEG, sampleData.get(MeasureSequence.SequenceKeyName.VEG));
    lms.setMetadata(ms.getMetadata());

    FileInOut.writeLiveMeasurements(lms, testSdCard.getCalibrationFile(), liveSdCard);
    assert liveSdCard.exists();
    assert liveSdCard.listFiles((dir, name) -> name.equals("cal.csv"))[0] != null;

    File dataFolder = liveSdCard.listFiles((dir, name) -> name.equals("LiveSDFolder"))[0];
    assert dataFolder.exists();
    assert dataFolder.isDirectory();
    File dataFile = dataFolder.listFiles((dir, name) -> name.equals("liveSDFile.csv"))[0];
    assert dataFile.exists();

    long size = dataFile.length();
    assert size > 0;

    FileInOut.writeLiveMeasurements(lms, testSdCard.getCalibrationFile(), liveSdCard);

    assert size < dataFile.length();
  }


  @Test
  public void getLineCount() throws Exception {
    FileInOut.writeDatabaseFile(testSdCard, testLibraryPath.getPath());

    File sdCard = new File(testLibraryPath + File.separator + "test");
    File[] files = sdCard.listFiles((dir, name) -> "db.csv".equals(name));
    long lineCount = FileInOut.getLineCount(files[0]);
    assert lineCount == 38;
  }


  @Test
  public void checkForEmptyFiles() throws Exception {
    File emptySdCardResourcePath1 = new File(getClass().getClassLoader().getResource("testCards/SDCARD_EmptyDataFile").getFile());
    File sdCardTestPath1 = new File(testLibraryPath.getPath() + File.separator + "empty1");
    FileUtils.copyDirectory(emptySdCardResourcePath1, sdCardTestPath1);
    File emptySdCardResourcePath2 = new File(getClass().getClassLoader().getResource("testCards/SDCARD_NoDataFile").getFile());
    File sdCardTestPath2 = new File(testLibraryPath.getPath() + File.separator + "empty2");
    FileUtils.copyDirectory(emptySdCardResourcePath2, sdCardTestPath2);
    FileInOut.checkForEmptyFiles(testLibraryPath.getPath());

    assert testLibraryPath.listFiles((dir, name) -> name.equals("empty1")).length == 0;
    assert testLibraryPath.listFiles((dir, name) -> name.equals("empty2")).length == 0;

  }
}