package controller.util.treeviewitems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeSdCardItem extends FrvaTreeItem {

  private SdCard sdCard;
  private boolean loaded;

  /**
   * Creates a SdCard item.
   * @param sdCard the sdCard the item is referring to.
   */
  public FrvaTreeSdCardItem(SdCard sdCard) {
    super(sdCard.getName());
    this.sdCard = sdCard;
    loaded = false;
    addListeners();
  }

  private void addListeners() {
    this.expandedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable,
                          Boolean oldValue, Boolean newValue) {
        List sdCards = new ArrayList();
        sdCards.add(sdCard);
        if (newValue && !loaded) {
          createChildren(sdCards);
          loaded = true;
        }

      }
    });

  }

  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";"
        + sdCard.getPath().getAbsolutePath() + ";";
  }

  @Override
  public int getDepth() {
    return 2;
  }

  @Override
  public void createChildren(List<SdCard> list) {
    this.getChildren().clear();

    int yearlyCount = 0;
    FrvaTreeYearItem yearItem = new FrvaTreeYearItem("Year");

    int monthlyCount = 0;
    FrvaTreeMonthItem monthItem = new FrvaTreeMonthItem("Month");

    int dailyCount = 0;
    FrvaTreeDayItem dayItem = new FrvaTreeDayItem("Day");

    int hourlyCount = 0;
    FrvaTreeHourItem hourItem = new FrvaTreeHourItem(-1);

    for (DataFile df : sdCard.getDataFiles()) {
      for (MeasureSequence ms : df.getMeasureSequences()) {
        boolean newItem = false;

        if (!ms.getYear().equals(yearItem.getYear())) {
          yearItem.addMeasureSequences(yearlyCount);
          yearlyCount = 0;
          yearItem = new FrvaTreeYearItem(ms.getYear());
          getChildren().add(yearItem);
          newItem = true;
        }

        if (!ms.getMonth().equals(monthItem.getMonth()) || newItem) {
          monthItem.addMeasureSequences(monthlyCount);
          monthlyCount = 0;
          monthItem = new FrvaTreeMonthItem(ms.getMonth());
          yearItem.getChildren().add(monthItem);
          newItem = true;
        }

        if (!ms.getDate().equals(dayItem.getDay()) || newItem) {
          dayItem.addMeasureSequences(dailyCount);
          dailyCount = 0;
          dayItem = new FrvaTreeDayItem(ms.getDate());
          monthItem.getChildren().add(dayItem);
          newItem = true;
        }

        if (ms.getHour() != (hourItem.getHour()) || newItem) {
          hourItem.addMeasureSequences(hourlyCount);
          hourlyCount = 0;
          hourItem = new FrvaTreeHourItem(ms.getHour());
          dayItem.getChildren().add(hourItem);
        }

        hourItem.getChildren().add(new FrvaTreeMeasurementItem("ID" + ms.getId() + " - "
            + ms.getTime(), ms, false));
        yearlyCount++;
        monthlyCount++;
        dailyCount++;
        hourlyCount++;
      }
    }
    hourItem.addMeasureSequences(hourlyCount);
    dayItem.addMeasureSequences(dailyCount);
    monthItem.addMeasureSequences(monthlyCount);
    yearItem.addMeasureSequences(yearlyCount);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeSdCardItem && this.sdCard.equals(((FrvaTreeSdCardItem) o).sdCard);
  }

  public SdCard getSdCard() {
    return sdCard;
  }
}
