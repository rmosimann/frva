package controller.util;

import model.data.MeasureSequence;

/**
 * Created by patrick.wigger on 12.10.17.
 */
public class FrvaTreeViewItem {
  MeasureSequence measureSequence;
  String name;

  public FrvaTreeViewItem(String name, MeasureSequence ms) {
    this.measureSequence = ms;
    this.name = name;

  }

  public String toString() {
    return name;
  }

  public MeasureSequence getMeasureSequence() {
    return this.measureSequence;
  }
}
