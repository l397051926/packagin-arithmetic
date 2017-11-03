package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.interfaces.SourceDataWrapperInterface;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
public class MapSourceDataWrapper implements SourceDataWrapperInterface<Map<String, JsonElement>> {
    private Map<String, JsonElement> data;

    @Override
    public Map<String, JsonElement> getData() {
        return data;
    }

    public void addData(String id, JsonElement element) {
        if (this.data == null) this.data = new HashMap<>();
        this.data.put(id, element);
    }

    public JsonElement getItem(String id) {
        if (data == null) return null;
        return data.get(id);
    }

    public boolean hasData(String id) {
        if (data == null) return false;
        return data.containsKey(id);
    }

}
