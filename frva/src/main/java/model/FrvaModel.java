package model;

import model.data.SdCard;

import java.util.ArrayList;
import java.util.List;

public class FrvaModel {



    public List<SdCard> library = new ArrayList<>();



    public List<SdCard> getLibrary() {
        return library;
    }

    public void addSdCard(SdCard sdCard){
        library.add(sdCard);
    }
}
