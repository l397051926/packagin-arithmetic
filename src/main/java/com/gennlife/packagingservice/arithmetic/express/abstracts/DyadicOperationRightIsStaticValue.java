package com.gennlife.packagingservice.arithmetic.express.abstracts;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.express.factorys.OperandDataFactory;
import com.gennlife.packagingservice.arithmetic.express.interfaces.OperandDatasForEachCheckInterface;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gennlife.packagingservice.arithmetic.express.factorys.OperandDataFactory.getStaticValueWrapper;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
public abstract class DyadicOperationRightIsStaticValue extends AbstractDirectOperandCheck {
    private static final Logger logger = LoggerFactory.getLogger(DyadicOperationRightIsStaticValue.class);
    @Override
    public final void count(JsonObject config, PathNode nodeContext, ConditionCheck conditionCheck) {
        AbstractStaticDataWrapper staticValue = getStaticValueWrapper(config, nodeContext, conditionCheck);
        if(staticValue==null)
        {
            logger.error("static value is null ,error config " + JsonAttrUtil.toJsonStr(config));
            return;
        }
        JsonElement value = staticValue.getValue();
        if (JsonAttrUtil.isEmptyJsonElement(value)) setTarget("");
        else if (value.isJsonArray()) setTarget(value.getAsJsonArray());
        else if (value.isJsonPrimitive()) setTarget(value.getAsString());
        AbstractOperandDatasWrapper datasource = OperandDataFactory.getUnary(config, nodeContext, conditionCheck);
        countByDataSource(config, datasource);
    }

    protected void countByDataSource(JsonObject config, AbstractOperandDatasWrapper datasource) {
        if (datasource != null && datasource instanceof OperandDatasForEachCheckInterface) {
            OperandDatasForEachCheckInterface datasourceCanCheck = (OperandDatasForEachCheckInterface) datasource;
            datasourceCanCheck.parse(this);
            PathNode findPathNode=datasourceCanCheck.getFindPathNode();
            setFindFlag(findPathNode != null);
            setFindPathNode(findPathNode);
        } else {
            logger.error("error config " + JsonAttrUtil.toJsonStr(config));
        }
    }

    protected abstract void setTarget(JsonArray jsonArray);

    protected abstract void setTarget(String s);

}
