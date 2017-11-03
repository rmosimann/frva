package controller.util;

import controller.util.TreeviewItems.FrvaTreeRootItem;
import java.util.Iterator;
import java.util.List;
import javafx.scene.control.TreeView;
import model.FrvaModel;
import model.data.MeasureSequence;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 26.10.17.
 */
public class TreeViewFactory {


  /**
   * Creates a treeview.
   *
   * @param list      list of SdCards containing the data.
   * @param treeView  view on which the data should be attached to
   * @param model     the model of the application
   * @param isPreview registers MeasureSequences in the model if false
   */
  public static void extendTreeView(List<SdCard> list, TreeView<FrvaTreeRootItem> treeView,
                                    FrvaModel model, boolean isPreview) {
/*

    FrvaTreeRootItem root = (FrvaTreeRootItem) treeView.getRoot();

    int sdCardCount = 0;
    FrvaTreeRootItem deviceItem = null;


    //Structurize Data with days/hours
    String currentDevice = "";
    for (SdCard card : list) {

      for (Object item : root.getChildren()) {

        if (((FrvaTreeRootItem) item).getDeviceId().equals(card.getDeviceSerialNr())) {
          deviceItem = (FrvaTreeRootItem) item;
          currentDevice = card.getDeviceSerialNr();
        }
      }
      if (!card.getDeviceSerialNr().equals(currentDevice)) {
        currentDevice = card.getDeviceSerialNr();
        deviceItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.DEVICE);
        root.getChildren().add(deviceItem);
      }


      FrvaTreeRootItem sdCardItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.SDCARD);
      deviceItem.getChildren().add(sdCardItem);


      Iterator it = card.getMeasureSequences().iterator();
      String year = "";
      String hour = "";
      String date = "";
      String month = "";
      boolean continueToNextDay = false;
      int dailyCount = 0;
      FrvaTreeRootItem checkBoxTreeDateItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.DAY);
      int hourlyCount = 0;
      FrvaTreeRootItem checkBoxTreeHourItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.HOUR);
      int yearlyCount = 0;
      FrvaTreeRootItem checkBoxTreeYearItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.YEAR);
      int monthlyCount = 0;
      FrvaTreeRootItem checkBoxTreeMonthItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.MONTH);


      while (it.hasNext()) {
        MeasureSequence measureSequence = (MeasureSequence) it.next();
        String currentHour = measureSequence.getTime().substring(0, 2);
        String currentDate = measureSequence.getDate();
        String currentYear = measureSequence.getYear();
        String currentMonth = measureSequence.getMonth();

        if (!currentYear.equals(year)) {
          checkBoxTreeYearItem.setName(year + " (" + yearlyCount + ")");
          yearlyCount = 0;
          year = currentYear;
          continueToNextDay = true;
          checkBoxTreeYearItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.YEAR);
          sdCardItem.getChildren().add(checkBoxTreeYearItem);
        }


        if (!currentMonth.equals(month)) {
          checkBoxTreeMonthItem.setName(month + " (" + monthlyCount + ")");
          monthlyCount = 0;
          month = currentMonth;
          continueToNextDay = true;
          checkBoxTreeMonthItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.MONTH);
          checkBoxTreeYearItem.getChildren().add(checkBoxTreeMonthItem);
        }

        if (!currentDate.equals(date)) {
          checkBoxTreeDateItem.setName(date + " (" + dailyCount + ")");
          dailyCount = 0;
          date = currentDate;
          continueToNextDay = true;
          checkBoxTreeDateItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.DAY);
          checkBoxTreeMonthItem.getChildren().add(checkBoxTreeDateItem);
        }

        if (!currentHour.equals(hour) || continueToNextDay) {
          continueToNextDay = false;
          checkBoxTreeHourItem.setName(hour + ":00-" + currentHour + ":00 "
              + "(" + hourlyCount + ")");
          hourlyCount = 0;
          hour = currentHour;
          checkBoxTreeHourItem = new FrvaTreeRootItem(FrvaTreeRootItem.Type.HOUR);
          checkBoxTreeDateItem.getChildren().add(checkBoxTreeHourItem);
        }
        hourlyCount++;
        dailyCount++;
        sdCardCount++;
        yearlyCount++;
        monthlyCount++;
        FrvaTreeRootItem checkBoxTreeMeasurementItem = new FrvaTreeRootItem("ID"
            + measureSequence.getId() + " - " + measureSequence.getTime(), measureSequence,
            model, FrvaTreeRootItem.Type.MEASURESEQUENCE, measureSequence.getContainingFile(), isPreview);

        checkBoxTreeHourItem.getChildren().add(checkBoxTreeMeasurementItem);

      }
      checkBoxTreeHourItem.setName(hour + ":00-" + (Integer.parseInt(hour) + 1) + ":00"
          + " (" + hourlyCount + ")");
      checkBoxTreeDateItem.setName(date + " (" + dailyCount + ")");
      checkBoxTreeYearItem.setName(year + " (" + yearlyCount + ")");
      checkBoxTreeMonthItem.setName(month + " (" + monthlyCount + ")");


      sdCardItem.setName(card.getName() + " (" + sdCardCount + ")");
      sdCardCount = 0;

      deviceItem.setName(card.getDeviceSerialNr() + " (" + model.getLibrarySize() + ")");

    }
    treeView.setShowRoot(false);
*/
  }





}
