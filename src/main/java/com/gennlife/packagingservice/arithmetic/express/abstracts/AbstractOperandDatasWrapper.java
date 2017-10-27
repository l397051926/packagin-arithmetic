package com.gennlife.packagingservice.arithmetic.express.abstracts;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.google.gson.JsonObject;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public abstract class AbstractOperandDatasWrapper  {
    protected JsonObject config;
    protected PathNode contextNode;
    protected ConditionCheck conditionCheck;


    public AbstractOperandDatasWrapper(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        this.config = config;
        this.contextNode = contextNode;
        this.conditionCheck = conditionCheck;

    }

    public PathNode getContextNode() {
        return contextNode;
    }


}
