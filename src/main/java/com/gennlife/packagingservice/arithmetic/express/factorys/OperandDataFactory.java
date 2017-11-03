package com.gennlife.packagingservice.arithmetic.express.factorys;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractStaticDataWrapper;
import com.gennlife.packagingservice.arithmetic.express.enitity.*;
import com.gennlife.packagingservice.arithmetic.express.interfaces.OperandDatasForEachCheckInterface;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonObject;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public class OperandDataFactory {
    //一元运算数据UNARY  二元 DYADIC
    public static final String DYADIC_TYPE_KEY = "dyadicType";
    public static final String DIRECT_TYPE = "direct";
    public static final String DYADIC_MAP_NAME_KEY = "dyadic_table";
    public static final String DYADIC_REF_ID_KEY = "dyadic_id";

    public static final String UNARY_TYPE_KEY = "unaryType";
    public static final String UNARY_MAP_NAME_KEY = "unary_table";
    public static final String UNARY_REF_ID_KEY = "unary_id";

    public static final String STATIC_TYPE = "static";
    public static final String MAP_STATIC_TYPE = "mapStatic";
    public static OperandDatasForEachCheckInterface getUnary(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        String type = JsonAttrUtil.getStringValue(UNARY_TYPE_KEY, config);
        if (StringUtil.isEmptyStr(type) || type.equalsIgnoreCase(DIRECT_TYPE)) {
            return new DirectOperandDatas(config, contextNode, conditionCheck);
        }
        if (type.equalsIgnoreCase(MAP_STATIC_TYPE)) {
            return new MapStaticUnaryOperandDatas(config, contextNode, conditionCheck);
        }
        return null;
    }

    public static AbstractStaticDataWrapper getStaticValueWrapper(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        String type = JsonAttrUtil.getStringValue(DYADIC_TYPE_KEY, config);
        if (StringUtil.isEmptyStr(type) || type.equalsIgnoreCase(STATIC_TYPE)) {
            return new StaticValueOperandDatas(config, contextNode, conditionCheck);
        }
        if (type.equalsIgnoreCase(MAP_STATIC_TYPE)) {
            return new TableStaticDataWrapper(DYADIC_MAP_NAME_KEY, DYADIC_REF_ID_KEY, config, contextNode, conditionCheck);
        }
        return null;
    }


}
