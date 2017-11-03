package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractStaticDataWrapper;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public class StaticValueOperandDatas extends AbstractStaticDataWrapper {

    public static final String VALUE_KEY = "staticValue";
    public static final String VALUE_OLD_KEY="value";
    public StaticValueOperandDatas(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        super(config, contextNode, conditionCheck);
        JsonElement value = JsonAttrUtil.getJsonElement(VALUE_OLD_KEY, config);
        if (JsonAttrUtil.isEmptyJsonElement(value)) {
            value = JsonAttrUtil.getJsonElement(VALUE_KEY, config);
        }
        super.setValue(value);
    }

}
