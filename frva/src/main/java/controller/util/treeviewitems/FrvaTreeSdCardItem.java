package controller.util.treeviewitems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeSdCardItem extends FrvaTreeItem {

  private SdCard sdCard;

  public FrvaTreeSdCardItem(SdCard sdCard) {
    super(sdCard.getName());
    this.sdCard = sdCard;
  }

  @Override
  public void createChildren() {
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeSdCardItem && this.sdCard.equals(((FrvaTreeSdCardItem) o).sdCard);
  }

  public SdCard getSdCard() {
    return sdCard;
  }
}
