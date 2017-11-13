package com.gennlife.packagingservice.arithmetic.assign;

import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonElement;

import java.util.LinkedList;

/**
 * Created by Chenjinfeng on 2017/11/11.
 */
public class TableClassifier implements Classifier {
    private LinkedList<String> matchList = new LinkedList<>();
    private boolean getAll = false;

    public TableClassifier(LinkedList<String> matchList) {
        this.matchList = matchList;
        JsonAttrUtil.sortForStandard(matchList);
    }

    public boolean isGetAll() {
        return getAll;
    }

    public void setGetAll(boolean getAll) {
        this.getAll = getAll;
    }

    @Override
    public String getClassifierKey(JsonElement source) {
        JsonElement result = getClassifierJsonElem(source);
        if (JsonAttrUtil.isJsonNull(result)) return null;
        return JsonAttrUtil.toJsonStr(result);
    }

    public JsonElement getClassifierJsonElem(JsonElement source) {
        LinkedList<String> match = new LinkedList<>();
        String oneData = source.getAsString();
        for (String itemValue : matchList) {
            if (oneData.contains(itemValue)) {
                if (!match.contains(itemValue)) match.add(itemValue);
            }
        }
        if (match.size() == 0) return null;
        if (getAll) return JsonAttrUtil.toJsonElement(match);
        else return JsonAttrUtil.toJsonElement(match.get(0));
    }
}
