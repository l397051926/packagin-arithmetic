package com.gennlife.packagingservice.arithmetic.express.interfaces;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.google.gson.JsonObject;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public interface InstructionOperatorInterface extends ExpressInterface {

    void count(JsonObject config, PathNode nodeContext, ConditionCheck conditionCheck);

    public default ExpressInterface parse() {
       throw new UnsupportedOperationException("InstructionOperatorInterface is not support ");
    }

    @Override
    public default boolean hasError() {
        return false;
    }

}
