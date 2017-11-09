package controller.util.TreeviewItems;

import java.io.File;
import model.FrvaModel;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeSdCardItem extends FrvaTreeItem {
  private SdCard sdCard;


  public FrvaTreeSdCardItem(String name, File sdCard, FrvaModel model) {
    super(name);
    //   System.out.println("in FrvaTreeSdCardItem"+ sdCard.getAbsolutePath());

    this.sdCard = new SdCard(sdCard, name, model, false);
  }

  public FrvaTreeSdCardItem(SdCard sdCard) {
    super(sdCard.getName());
    this.sdCard = sdCard;
  }


  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + sdCard.getPath().getAbsolutePath() + ";";

  }

  @Override
  public int getDepth() {
    return 2;
  }

  @Override
  public void setPathToLibrary() {
    this.sdCard.setPathToLibrary();
    setValue(sdCard.getName());
  }

  @Override
  public boolean equals(Object o){
   return o instanceof FrvaTreeSdCardItem && this.sdCard.equals(((FrvaTreeSdCardItem) o).sdCard);
  }
}
