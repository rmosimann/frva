package controller.util.liveviewparser;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import controller.LiveViewController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import model.FrvaModel;
import org.junit.Before;
import org.junit.Test;

public class LiveDataParserTest {

  LiveDataParser liveDataParser;
  FrvaModel model;
  LiveViewController mockLiveViewController;

  @Before
  public void setUp() throws Exception {
    model = mock(FrvaModel.class);
    mockLiveViewController = mock(LiveViewController.class);
    liveDataParser = new LiveDataParser(mockLiveViewController, model);

  }

  @Test
  public void startParsing() throws InterruptedException {

    InputStream input = new ByteArrayInputStream("; ; App?".getBytes());
    OutputStream output = new ByteArrayOutputStream();

    liveDataParser.startParsing(input, output);

    Thread.sleep(50);

    assertTrue(liveDataParser.getCommandQueue().removeIf(commandInterface -> {
          return commandInterface instanceof CommandAutoMode;
        }
    ));
  }
}