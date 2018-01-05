package com.gennlife.packagingservice.arithmetic.express.status;

import com.gennlife.packagingservice.arithmetic.express.enitity.FindIndexModel;
import com.google.gson.JsonElement;

import java.util.LinkedList;

/**
 * Created by Chenjinfeng on 2017/12/25.
 */
public abstract class AbsFindIndexModelFilter {
    private LinkedList<FindIndexModel<JsonElement>> matchResult = new LinkedList<>();

    private boolean pause = false;
    private boolean breakFlag = false;
    /**
     * 原始数据是否为空
     * */
    private boolean isDataEmpty = true;

    public void filterAndAdd(FindIndexModel<JsonElement> find) {
        if (breakFlag) return;
        if (find == null) return;
        isDataEmpty = false;
        if (isMatch(find.getValue())) matchResult.add(find);
    }

    public abstract boolean isMatch(JsonElement target);

    public LinkedList<FindIndexModel<JsonElement>> getMatchData() {
        return matchResult;
    }


    public boolean isBreak() {
        return breakFlag;
    }

    public void makeBreak() {
        breakFlag = true;
    }

    public boolean isEmpty() {
        return matchResult == null && matchResult.size() == 0;
    }

    public boolean isPause() {
        return pause;
    }

    public void pauseAction() {
        pause = true;
    }

    public void startAction() {
        pause = false;
    }

    public void setInitList(LinkedList<FindIndexModel<JsonElement>> initList) {
        if (initList == null) return;
        this.matchResult = initList;
    }

    public void clearResult() {
        this.matchResult.clear();
    }

    public boolean isDataEmpty() {
        return isDataEmpty;
    }
}
