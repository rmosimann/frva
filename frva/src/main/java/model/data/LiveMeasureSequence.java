package model.data;

import java.util.HashMap;
import java.util.Map;


public class LiveMeasureSequence extends MeasureSequence {

  private final Map<MeasureSequence.SequenceKeyName, double[]> data = new HashMap<>();

  public LiveMeasureSequence() {
    super();
  }

  public void addData(MeasureSequence.SequenceKeyName bla, double[] content) {
    data.put(bla, content);
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
}
