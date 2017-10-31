package controller.util;

import java.util.Iterator;
import java.util.List;
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
   * @param list      list of SdCards containing the data.
   * @param treeView  view on which the data should be attached to
   * @param model     the model of the application
   * @param isPreview registers MeasureSequences in the model if false
   */
  public static void extendTreeView(List<SdCard> list, TreeView<FrvaTreeViewItem> treeView,
                                    FrvaModel model, boolean isPreview) {


    FrvaTreeViewItem root = (FrvaTreeViewItem) treeView.getRoot();

    int sdCardCount = 0;
    FrvaTreeViewItem deviceItem = null;


    //Structurize Data with days/hours
    String currentDevice = "";
    for (SdCard card : list) {

      for (Object item : root.getChildren()) {

        if (((FrvaTreeViewItem) item).getDeviceId().equals(card.getDeviceSerialNr())) {
          deviceItem = (FrvaTreeViewItem) item;
          currentDevice = card.getDeviceSerialNr();
        }
      }
      if (!card.getDeviceSerialNr().equals(currentDevice)) {
        currentDevice = card.getDeviceSerialNr();
        deviceItem = new FrvaTreeViewItem(FrvaTreeViewItem.Type.DEVICE);
        root.getChildren().add(deviceItem);
      }


      FrvaTreeViewItem sdCardItem = new FrvaTreeViewItem(FrvaTreeViewItem.Type.SDCARD);
      deviceItem.getChildren().add(sdCardItem);


      for (DataFile dataFile : card.getDataFiles()) {
        Iterator it = dataFile.getMeasureSequences().iterator();
        String hour = "";
        String date = "000000";
        boolean continueToNextDay = false;
        int dailyCount = 0;
        FrvaTreeViewItem checkBoxTreeDateItem = new FrvaTreeViewItem(FrvaTreeViewItem.Type.DATE);
        int hourlyCount = 0;
        FrvaTreeViewItem checkBoxTreeHourItem = new FrvaTreeViewItem(FrvaTreeViewItem.Type.HOUR);

        while (it.hasNext()) {
          MeasureSequence measureSequence = (MeasureSequence) it.next();
          String currentHour = measureSequence.getTime().substring(0, 2);
          String currentDate = measureSequence.getDate();

          if (!currentDate.equals(date)) {
            checkBoxTreeDateItem.setName(date + " (" + dailyCount + ")");
            dailyCount = 0;
            date = currentDate;
            continueToNextDay = true;
            checkBoxTreeDateItem = new FrvaTreeViewItem(FrvaTreeViewItem.Type.DATE);
            sdCardItem.getChildren().add(checkBoxTreeDateItem);
          }

          if (!currentHour.equals(hour) || continueToNextDay) {
            continueToNextDay = false;
            checkBoxTreeHourItem.setName(hour + ":00-" + currentHour + ":00 "
                + "(" + hourlyCount + ")");
            hourlyCount = 0;
            hour = currentHour;
            checkBoxTreeHourItem = new FrvaTreeViewItem(FrvaTreeViewItem.Type.HOUR);
            checkBoxTreeDateItem.getChildren().add(checkBoxTreeHourItem);
          }
          hourlyCount++;
          dailyCount++;
          sdCardCount++;
          FrvaTreeViewItem checkBoxTreeMeasurementItem = new FrvaTreeViewItem("ID"
              + measureSequence.getId() + " - " + measureSequence.getTime(), measureSequence,
              model, FrvaTreeViewItem.Type.MEASRURESEQUENCE, isPreview);

          checkBoxTreeHourItem.getChildren().add(checkBoxTreeMeasurementItem);

        }
        checkBoxTreeHourItem.setName(hour + ":00-" + (Integer.parseInt(hour) + 1) + ":00"
            + " (" + hourlyCount + ")");
        checkBoxTreeDateItem.setName(date + " (" + dailyCount + ")");
      }
      sdCardItem.setName(card.getName() + " (" + sdCardCount + ")");
      sdCardCount = 0;

      deviceItem.setName(card.getDeviceSerialNr() + " (" + model.getLibrarySize() + ")");

    }
    treeView.setShowRoot(false);
  }

}
