package model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SdCardTest {

  SdCard sdCard;
  String sdcardname;

  static File testDir;

  @BeforeClass
  public static void setUpOnce() throws Exception {
    File srcDir = new File(SdCardTest.class.getClassLoader().getResource("testCards").getFile());
    testDir = new File(srcDir.getParent().concat("/tmp_testCards"));
    FileUtils.copyDirectory(srcDir, testDir);
  }

  @AfterClass
  public static void tearDownOnce() throws IOException {
    FileUtils.deleteDirectory(testDir);
  }

  @Before
  public void setUp() throws Exception {

    sdcardname = "testSdcard";
    File file = new File(getClass().getClassLoader().getResource("tmp_testCards/SDCARD_OK").getFile());
    sdCard = new SdCard(file, sdcardname);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void constructors() {
    File file = new File(getClass().getClassLoader().getResource("tmp_testCards/SDCARD_OK").getFile());
    sdCard = new SdCard(file, null);

  }


  @Test
  public void isEmpty() {
    sdCard.isEmpty();

    File file = new File(getClass().getClassLoader().getResource("tmp_testCards/SDCARD_EmptyDataFile").getFile());
    sdCard = new SdCard(file, null);


    sdCard.isEmpty();

  }

  @Test
  public void getDeviceSerialNr() {
  }

  @Test
  public void getName() {
    assertEquals(sdcardname, sdCard.getName());

  }

  @Test
  public void getMeasureSequences() {
    List<MeasureSequence> measureSequences = sdCard.getMeasureSequences();
    assertTrue(measureSequences.size() == 38);
  }

  @Test
  public void getCalibrationFile() {
  }

  @Test
  public void getDataFiles() {
  }

  @Test
  public void getSdCardFile() {
  }

  @Test
  public void deleteFile() {
  }

  @Test
  public void setCalibrationFile() {
  }
}