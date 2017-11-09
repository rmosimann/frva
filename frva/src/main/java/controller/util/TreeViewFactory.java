package controller.util;

import controller.util.TreeviewItems.FrvaTreeDayItem;
import controller.util.TreeviewItems.FrvaTreeDeviceItem;
import controller.util.TreeviewItems.FrvaTreeHourItem;
import controller.util.TreeviewItems.FrvaTreeMeasurementItem;
import controller.util.TreeviewItems.FrvaTreeMonthItem;
import controller.util.TreeviewItems.FrvaTreeRootItem;
import controller.util.TreeviewItems.FrvaTreeSdCardItem;
import controller.util.TreeviewItems.FrvaTreeYearItem;
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
   * @param sdCard   SdCard containing the data.
   * @param treeView view on which the data should be attached to
   * @param model    the model of the application
   */

  public static void extendTreeView(SdCard sdCard, TreeView<FrvaTreeRootItem> treeView,
                                    FrvaModel model, boolean isPreview) {


    String currentDevice = "";


    FrvaTreeDeviceItem checkBoxDeviceItem = new FrvaTreeDeviceItem(sdCard.getDeviceSerialNr(), sdCard.getDeviceSerialNr());

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
      for (Object child : root.getChildren()
          ) {
        if (checkBoxDeviceItem.equals(child)) {
          System.out.println("added to existing Device");
          checkBoxDeviceItem = (FrvaTreeDeviceItem) child;
        }
      }
    } else {
      root.getChildren().add(checkBoxDeviceItem);
    }

    //Checks if SDCard has already been added (equals of Tree item... comparison over Serial)

    if (checkBoxDeviceItem.getChildren().contains(sdCardItem)) {
      for (Object child : checkBoxDeviceItem.getChildren()
          ) {
        if (sdCardItem.equals(child)) {
          System.out.println("added to existing SDCard");
          sdCardItem = (FrvaTreeSdCardItem) child;
        }
      }
    } else {
      checkBoxDeviceItem.getChildren().add(sdCardItem);
    }



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

        hourItem.getChildren().add(new FrvaTreeMeasurementItem("ID" + ms.getId() + " - " + ms.getTime(), ms, ms.getId(), ms.getDataFile().getOriginalFile(), model, isPreview));

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
    sdCardItem.addMeasureSequences(sdCardCount);

    sdCardItem.addMeasureSequences(sdCardCount);
    treeView.setShowRoot(false);


  }


}
