package model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MeasureSequenceTest {

  /*
  Metadata explained:
    0 Counter
    1 Date? YYMMDD
    2 hhmmss (internal clock)
    3 Mode (auto/manual/app)
    4 Integration time microseconds IT WR
    5 Integration time microsceconds IT VEG
    6 Time for one measurement miliseconds
    More see https://docs.google.com/document/d/1kyKZe7tlKG4Wva3zGr00dLTMva1NG_ins3nsaOIfGDA/edit#
  */


  MeasureSequence ms;
  String[] metadata;
  DataFile df = mock(DataFile.class);


  LocalDateTime localDateTime = LocalDateTime.now();

  String id = "24";
  String serial = "RoX V1.0 JB-104-ETH";


  @Before
  public void setUp() throws Exception {

    metadata = new String[32];

    metadata[0] = id;
    metadata[1] = localDateTime.format(DateTimeFormatter.ofPattern("yyMMdd"));
    metadata[2] = localDateTime.format(DateTimeFormatter.ofPattern("kkmmss"));
    metadata[14] = serial;

    ms = new MeasureSequence(metadata, df);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void getData() {
  }

  @Test
  public void getCsv() {
  }

  @Test
  public void getId() {
    assertEquals(id, ms.getId());
  }

  @Test
  public void getTime() {

    String[] time = ms.getTime().split(":");

    assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("kk")), time[0]);
    assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("mm")), time[1]);
    assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("ss")), time[2]);

    assertEquals(localDateTime.getHour(), ms.getHour());

    metadata[2] = "232323";
    ms.setMetadata(metadata);
    time = ms.getTime().split(":");

    assertEquals("23", time[0]);
    assertEquals("23", time[1]);
    assertEquals("23", time[2]);

    assertEquals(23, ms.getHour());

    metadata[2] = "10101";
    ms.setMetadata(metadata);
    time = ms.getTime().split(":");

    assertEquals("01", time[0]);
    assertEquals("01", time[1]);
    assertEquals("01", time[2]);

    assertEquals(1, ms.getHour());
  }

  @Test(expected = IllegalArgumentException.class)
  public void getTimeException() {
    metadata[2] = "";
    ms.setMetadata(metadata);
    ms.getTime();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getHourException() {
    metadata[2] = "";
    ms.setMetadata(metadata);
    ms.getHour();
  }


  @Test
  public void getSerial() {
    assertEquals(serial, ms.getSerial());
  }

  @Test
  public void getDate() {

    String[] date = ms.getDate().split("-");

    assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("yy")), date[0]);
    assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("MM")), date[1]);
    assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("dd")), date[2]);
  }

  @Test
  public void getRadiance() {
  }

  @Test
  public void getReflectance() {
  }

  @Test
  public void getIndices() {
  }

  @Test
  public void getWavlengthCalibration() {
  }

  @Test
  public void getSequenceUuid() {
    MeasureSequence measureSequence = new MeasureSequence();
    assertNotEquals(ms.getSequenceUuid(), measureSequence.getSequenceUuid());

  }


  @Test
  public void getDataFile() {
    assertEquals(df, ms.getDataFile());
  }

  @Test
  public void getYear() {
    assertEquals(String.valueOf(localDateTime.getYear()), ms.getYear());
  }

  @Test
  public void getMonth() {
    assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("MMM")).toUpperCase(), ms.getMonth());
  }

  @Test
  public void getMetadataAsString() {
    String metadata = "1;651221;153549;auto_mode;IT_WR[us]=;1000000;IT_VEG[us]=;1000000;cycle_duration[ms]=;13140;mainboard_temp[C]=;25.60;mainboard_humidity=;26.30;RoX V1.0 JB-104-ETH;GPS_TIME_UTC=;235952.;GPS_date=;050180;GPS_lat=;0.00000 ;GPS_lon=;0.00000 ;voltage=12.67;gps_CPU=;11852;wr_CPU=;42663;veg_CPU=;40029;averages_FLAME=;1;";

    ms.setMetadata(metadata.split(";"));

    assertEquals(metadata, ms.getMetadataAsString());
  }

  @Test
  public void getContainingSdCard() {
  }


  @Test
  public void deleting() {
    assertFalse(ms.isDeleted());
    ms.setDeleted(true);
    assertTrue(ms.isDeleted());
  }
}