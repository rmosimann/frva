package controller.util.liveviewparser;

import controller.LiveViewController;
import controller.util.FakeDataStream;
import model.FrvaModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class LiveDataParserTest {
  FakeDataStream fds;
  LiveDataParser ldp;

  @Before

  public void setUp() throws Exception {
    fds = new FakeDataStream();
    FrvaModel model = new FrvaModel();
    ldp = new LiveDataParser(new LiveViewController(model), model);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void startParsing() throws Exception {
    ldp.startParsing(fds, FakeDataStream.getOutputStream());
    Thread.sleep(10000);

  }

  @Test
  public void addComandToQueue() throws Exception {
  }

  @Test
  public void sendCommand() throws Exception {
  }

}