package com.gennlife.packagingservice.arithmetic.express.enitity;


import com.gennlife.packagingservice.arithmetic.express.interfaces.FindIndexModelDataWrapperInterface;
import com.google.gson.JsonElement;

import java.util.LinkedList;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public class DirectSourceDataWrapper implements FindIndexModelDataWrapperInterface {
    private LinkedList<FindIndexModel<JsonElement>> data;
    public void setData(LinkedList<FindIndexModel<JsonElement>> data) {
        this.data = data;
    }

    @Override
    public LinkedList<FindIndexModel<JsonElement>> getData() {
        return data;
    }
}
