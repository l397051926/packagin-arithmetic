package com.gennlife.packagingservice.arithmetic.express.factorys;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractOperandDatasWrapper;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractStaticDataWrapper;
import com.gennlife.packagingservice.arithmetic.express.enitity.DirectOperandDatasInterface;
import com.gennlife.packagingservice.arithmetic.express.enitity.MapOperandDatas;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.express.enitity.StaticValueOperandDatas;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonObject;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public class OperandDataFactory {
    //一元运算数据
    public static final String DYADIC_KEY = "dyadicType";
    public static final String UNARY_KEY = "unaryType";
    public static final String DIRECT_TYPE = "direct";
    public static final String STATIC_TYPE = "static";
    public static final String MAP_STATIC_TYPE = "mapStatic";

    public static AbstractOperandDatasWrapper getUnary(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        String type = JsonAttrUtil.getStringValue(UNARY_KEY, config);
        if (StringUtil.isEmptyStr(type) || type.equalsIgnoreCase(DIRECT_TYPE)) {
            return new DirectOperandDatasInterface(config, contextNode, conditionCheck);
        }
        return null;
    }

    //二元运算数据
    public static AbstractOperandDatasWrapper getDyadic(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        String type = JsonAttrUtil.getStringValue(DYADIC_KEY, config);
        return getAbstractOperandDatasWrapperByType(config, contextNode, conditionCheck, type);
    }

    private static AbstractOperandDatasWrapper getAbstractOperandDatasWrapperByType(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck, String type) {
        if (StringUtil.isEmptyStr(type) || type.equalsIgnoreCase(STATIC_TYPE)) {
            return new StaticValueOperandDatas(config, contextNode, conditionCheck);
        }
        return null;
    }

    public static AbstractStaticDataWrapper getStaticValueWrapper(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        String type = JsonAttrUtil.getStringValue(DYADIC_KEY, config);
        if (StringUtil.isEmptyStr(type) || type.equalsIgnoreCase(STATIC_TYPE)) {
            return new StaticValueOperandDatas(config, contextNode, conditionCheck);
        }
        if(type.equalsIgnoreCase(MAP_STATIC_TYPE))
        {
            return new MapOperandDatas(config, contextNode, conditionCheck);
        }
        return null;
    }


}
