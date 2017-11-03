package com.gennlife.packagingservice.arithmetic.express.abstracts;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
public class AbstractStaticDataWrapper extends AbstractOperandDatasWrapper {
    private JsonElement value;
    private static final Logger logger = LoggerFactory.getLogger(AbstractStaticDataWrapper.class);

    public AbstractStaticDataWrapper(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        super(config, contextNode, conditionCheck);
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
       /* if (JsonAttrUtil.isEmptyJsonElement(value)) {
            logger.warn("value is empty in getStaticValue");
        }*/
        this.value = value;
    }
}
