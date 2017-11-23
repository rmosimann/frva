package controller.util;

import controller.util.treeviewitems.FrvaTreeDayItem;
import controller.util.treeviewitems.FrvaTreeDeviceItem;
import controller.util.treeviewitems.FrvaTreeHourItem;
import controller.util.treeviewitems.FrvaTreeMeasurementItem;
import controller.util.treeviewitems.FrvaTreeMonthItem;
import controller.util.treeviewitems.FrvaTreeRootItem;
import controller.util.treeviewitems.FrvaTreeSdCardItem;
import controller.util.treeviewitems.FrvaTreeYearItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.FrvaModel;
import model.data.DataFile;
import model.data.MeasureSequence;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 26.10.17.
 */
public class TreeViewFactory {


  /**
   * Creates a treeview.
   *
   * @param sdCard    SdCard containing the data.
   * @param treeView  view on which the data should be attached to
   * @param model     the model of the application
   * @param isPreview true when used for preview.
   */

  public static void extendTreeView(SdCard sdCard, TreeView<FrvaTreeRootItem> treeView,
                                    FrvaModel model, boolean isPreview) {



    FrvaTreeDeviceItem checkBoxDeviceItem = new FrvaTreeDeviceItem(sdCard.getDeviceSerialNr(),
        sdCard.getDeviceSerialNr());

    int sdCardCount = 0;
    FrvaTreeSdCardItem sdCardItem = new FrvaTreeSdCardItem(sdCard);

    int yearlyCount = 0;
    FrvaTreeYearItem yearItem = new FrvaTreeYearItem("Year");
    int monthlyCount = 0;
    FrvaTreeMonthItem monthItem = new FrvaTreeMonthItem("Month");

    int dailyCount = 0;
    FrvaTreeDayItem dayItem = new FrvaTreeDayItem("Day");

    int hourlyCount = 0;
    FrvaTreeHourItem hourItem = new FrvaTreeHourItem(-1);

    TreeItem root = treeView.getRoot();


    //Checks if Device has already been added (equals of Tree item... comparison over Serial)
    if (root.getChildren().contains(checkBoxDeviceItem)) {
      for (Object child : root.getChildren()) {
        if (checkBoxDeviceItem.equals(child)) {
          checkBoxDeviceItem = (FrvaTreeDeviceItem) child;
        }
      }
    } else {
      root.getChildren().add(checkBoxDeviceItem);
    }

    checkBoxDeviceItem.getChildren().add(sdCardItem);

    for (DataFile df : sdCard.getDataFiles()) {
      for (MeasureSequence ms : df.getMeasureSequences()) {
        boolean newItem = false;

        if (!ms.getYear().equals(yearItem.getYear())) {
          yearItem.addMeasureSequences(yearlyCount);
          yearlyCount = 0;
          yearItem = new FrvaTreeYearItem(ms.getYear());
          sdCardItem.getChildren().add(yearItem);
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
            + ms.getTime(), ms, isPreview));
        sdCardCount++;
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
    sdCardItem.addMeasureSequences(sdCardCount);
    checkBoxDeviceItem.addMeasureSequences(sdCardCount);
    treeView.setShowRoot(false);
  }
}
