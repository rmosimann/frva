package controller.util.liveviewparser;

import java.io.InputStream;
import java.io.OutputStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.FrvaModel;

public class LiveDataParser {
  private final FrvaModel model;
  private final InputStream inputStream;
  private final OutputStream outputStream;

  private final ObjectProperty<DataParserState> state = new SimpleObjectProperty<>();

  /**
   * Creates a LiveViewDataParser on the Streams of the FloxRox-Connection.
   * Handles all commands and parses all output of the device.
   *
   * @param model        The one and only Model.
   * @param inputStream  inputstrem on the Connection of the device.
   * @param outputStream outpustrem on the Connection of the device.
   */
  public LiveDataParser(FrvaModel model, InputStream inputStream, OutputStream outputStream) {
    this.model = model;
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    state.setValue(new DataParserStateInit());
  }

}
