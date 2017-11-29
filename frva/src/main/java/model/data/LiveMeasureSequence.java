package model.data;

import java.util.HashMap;
import java.util.Map;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    return getSequenceUuid();
  }

  @Override
  public Map<SequenceKeyName, double[]> getData() {
    return data;
  }

  @Override
  public Map<SequenceKeyName, double[]> getRadiance() {
    throw new NotImplementedException();
  }

  @Override
  public Map<SequenceKeyName, double[]> getReflectance() {
    throw new NotImplementedException();
  }

  @Override
  public ReflectionIndices getIndices() {
    throw new NotImplementedException();
  }

  @Override
  public double[] getWavlengthCalibration() {
    throw new NotImplementedException();
  }

  @Override
  public DataFile getDataFile() {
    throw new NotImplementedException();
  }

  @Override
  public SdCard getContainingSdCard() {
    throw new NotImplementedException();
  }
}
