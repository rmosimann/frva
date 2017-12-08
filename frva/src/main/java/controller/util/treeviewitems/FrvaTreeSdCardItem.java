package controller.util.treeviewitems;

import java.util.ArrayList;
import java.util.List;
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
   *
   * @param sdCard    the sdCard the item is referring to.
   * @param isPreview true if import scenario.
   */
  public FrvaTreeSdCardItem(SdCard sdCard, boolean isPreview) {
    super(sdCard.getName());
    this.sdCard = sdCard;
    loaded = false;
    if (!isPreview) {
      addListeners();
    }
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
    this.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                          Boolean newValue) {
        List sdCards = new ArrayList();
        sdCards.add(sdCard);
        if (newValue && !loaded) {
          createChildren(sdCards);
          getChildren().forEach(o -> ((FrvaTreeItem) o).setSelected(true));
          loaded = true;
        }
      }
    });
  }

  /**
   * Creates its Children depending on list.
   *
   * @param list of SdCards which is input data for creating Treeview.
   */
  public void createChildren(List<SdCard> list) {
    this.getChildren().clear();

    FrvaTreeYearItem yearItem = new FrvaTreeYearItem("Year");

    FrvaTreeMonthItem monthItem = new FrvaTreeMonthItem("Month");


    FrvaTreeDayItem dayItem = new FrvaTreeDayItem("Day");


    FrvaTreeHourItem hourItem = new FrvaTreeHourItem(-1);


    for (DataFile df : sdCard.getDataFiles()) {
      for (MeasureSequence ms : df.getMeasureSequences()) {
        boolean newItem = false;
        boolean alreadyExisting = false;

        if (!ms.getYear().equals(yearItem.getYear())) {

          yearItem = new FrvaTreeYearItem(ms.getYear());
          for (Object child : getChildren()) {
            if (yearItem.equals(child)) {
              yearItem = (FrvaTreeYearItem) child;
              alreadyExisting = true;
            }
          }
          if (!alreadyExisting) {
            getChildren().add(yearItem);
            alreadyExisting = false;
          }
          newItem = true;
        }

        if (!ms.getMonth().equals(monthItem.getMonth()) || newItem) {

          monthItem = new FrvaTreeMonthItem(ms.getMonth());
          yearItem.getChildren().add(monthItem);
          newItem = true;
        }

        if (!ms.getDate().equals(dayItem.getDay()) || newItem) {

          dayItem = new FrvaTreeDayItem(ms.getDate());
          monthItem.getChildren().add(dayItem);
          newItem = true;
        }

        if (ms.getHour() != (hourItem.getHour()) || newItem) {

          hourItem = new FrvaTreeHourItem(ms.getHour());
          dayItem.getChildren().add(hourItem);
        }

        hourItem.getChildren().add(new FrvaTreeMeasurementItem("ID" + ms.getId() + " - "
            + ms.getTime(), ms));
        hourItem.addMeasureSequence();

      }
    }

  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeSdCardItem && this.sdCard.equals(((FrvaTreeSdCardItem) o).sdCard);
  }

  public SdCard getSdCard() {
    return sdCard;
  }
}
