package model;

import model.Parser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParserTest {
  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void csvReader() throws Exception {
    Parser.csvReader("SDCARD/FLAMEradioVEG_2017-08-03.csv",";");
    Parser.csvReader("SDCARD/YYMMDD/F075926.CSV",";");
  }

}