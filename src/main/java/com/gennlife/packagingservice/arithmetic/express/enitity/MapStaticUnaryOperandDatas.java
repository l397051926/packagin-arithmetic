package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractDirectOperandCheck;
import com.gennlife.packagingservice.arithmetic.express.interfaces.OperandDatasForEachCheckInterface;
import com.google.gson.JsonObject;

import static com.gennlife.packagingservice.arithmetic.express.factorys.OperandDataFactory.STATIC_UNARY_MAP_NAME_KEY;
import static com.gennlife.packagingservice.arithmetic.express.factorys.OperandDataFactory.STATIC_UNARY_REF_ID_KEY;

/**
 * Created by Chenjinfeng on 2017/10/27.
 */
public class MapStaticUnaryOperandDatas extends TableStaticDataWrapper implements OperandDatasForEachCheckInterface {

    private PathNode findPathNode;

    public MapStaticUnaryOperandDatas(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        super(STATIC_UNARY_MAP_NAME_KEY, STATIC_UNARY_REF_ID_KEY, config, contextNode, conditionCheck);
    }

    @Override
    public PathNode getFindPathNode() {
        return findPathNode;
    }

    @Override
    public void parse(AbstractDirectOperandCheck op) {
        if (op == null) return;
        boolean match = op.isMatch(getValue());
        if (match) this.findPathNode = contextNode;
        else this.findPathNode = null;

    }
}
