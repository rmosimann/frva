package model.data;

import controller.LiveViewController;
import java.util.HashMap;
import java.util.Map;


public class LiveMeasureSequence extends MeasureSequence {

  private final Map<MeasureSequence.SequenceKeyName, double[]> data = new HashMap<>();
  private LiveViewController listener;
  private boolean complete = false;

  public LiveMeasureSequence(LiveViewController listener) {
    super();
    this.listener = listener;
  }

  public void addData(MeasureSequence.SequenceKeyName keyName, double[] content) {
    data.put(keyName, content);
    updated();
  }

  private void updated() {
    if (listener != null) {
      listener.redrawGraph(this);
    }
  }

  @Override
  public String toString() {
    if (getMetadata() != null) {
      return getId();
    }
    return "Measuring...";
  }

  @Override
  public Map<SequenceKeyName, double[]> getData() {
    return data;
  }

  @Override
  public Map<SequenceKeyName, double[]> getRadiance() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public Map<SequenceKeyName, double[]> getReflectance() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public ReflectionIndices getIndices() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public double[] getWavlengthCalibration() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public DataFile getDataFile() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }

  @Override
  public SdCard getContainingSdCard() {
    throw new UnsupportedOperationException("Not Implemented in the live view!");
  }


  public void setComplete(boolean complete) {
    this.complete = complete;
    listener = null;
  }

  public boolean isComplete() {
    return complete;
  }
}
