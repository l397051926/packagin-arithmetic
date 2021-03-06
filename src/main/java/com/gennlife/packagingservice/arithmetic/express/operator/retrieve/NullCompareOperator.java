package com.gennlife.packagingservice.arithmetic.express.operator.retrieve;

import com.gennlife.packagingservice.arithmetic.express.abstracts.DyadicOperationRightIsStaticValue;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public class NullCompareOperator extends DyadicOperationRightIsStaticValue {
    @Override
    protected void setTarget(JsonArray jsonArray) {

    }

    @Override
    public void setTarget(String target) {

    }

    @Override
    protected boolean check(JsonElement source) {
        if (JsonAttrUtil.isEmptyJsonElement(source)) return true;
        if (source.isJsonArray()) {
            if (source.getAsJsonArray().size() > 0)
                return false;
            return true;
        }
        return false;
    }

    @Override
    public boolean allEmptyList() {
        return true;
    }

}
