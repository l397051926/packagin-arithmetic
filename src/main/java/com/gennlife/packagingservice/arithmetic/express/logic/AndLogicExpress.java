package com.gennlife.packagingservice.arithmetic.express.logic;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractLogicExpress;
import com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Chenjinfeng on 2017/10/16.
 */
public class AndLogicExpress extends AbstractLogicExpress {
    private PathNode childPathNode;
    private static final Logger logger = LoggerFactory.getLogger(AndLogicExpress.class);

    public AndLogicExpress(ConditionCheck conditionCheck, JsonArray detail, String operator, PathNode globalPathNode, String path) {
        super(conditionCheck, detail, operator, globalPathNode, path);
        this.findflag = true;
        this.childPathNode = globalPathNode.deepCopy();
    }

    @Override
    protected void parseAfter() {
        if (findflag) {
            tmpNode = childPathNode.getNeedPathNode(getNeedPath());
        }
    }


    @Override
    public boolean isContinue(ExpressInterface express) {
        if (!express.isFindflag()) {
            setNotFind();
            return false;
        }
        PathNode node = express.getFindPathNode();
        childPathNode.mergeForLeaveAndAddNew(node, true);
        return true;
    }

    @Override
    public PathNode getPathNodeForChild() {
        return childPathNode;
    }


}
