package model.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SdCardTest {

  private SdCard sd;

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void atest() {
    System.out.println(getClass().getResource("/SDCARD"));
    sd = new SdCard(getClass().getResource("/SDCARD"));

    sd.getDataFiles().get(0).getMeasureSequences().get(2).print();

  }
}