package com.gennlife.packagingservice.rws;

import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractLogicExpress;
import com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface;
import com.gennlife.packagingservice.arithmetic.express.enitity.StaticValueOperandDatas;
import com.gennlife.packagingservice.arithmetic.express.enums.ArrayOpEnum;
import com.gennlife.packagingservice.arithmetic.express.enums.NumberOpEnum;
import com.gennlife.packagingservice.arithmetic.express.factorys.ConditionOperatorFactory;
import com.gennlife.packagingservice.arithmetic.express.factorys.OperandDataFactory;
import com.gennlife.packagingservice.arithmetic.pretreatment.enums.InstructionOperatorEnum;
import com.gennlife.packagingservice.arithmetic.pretreatment.enums.LogicExpressEnum;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import static com.gennlife.packagingservice.arithmetic.express.factorys.OperandDataFactory.DYADIC_REF_ID_KEY;
import static com.gennlife.packagingservice.arithmetic.express.factorys.OperandDataFactory.UNARY_REF_ID_KEY;
import static com.gennlife.packagingservice.rws.RwsCountUtils.*;

/**
 * Created by Chenjinfeng on 2017/10/28.
 */
public class RwsConfigTransUtils {
    public static final String RWS_STATIC_MAP_TABLE_NAME = "rws_static_map_table";
    public static final String RIGHT_OP_KEY = "rightCountType";
    public static final String RIGHT_OP_PARAM_KEY = "rightCountParam";
    public static final String RESULTSQL_KEY = "resultsql";
    public static final String REF_ID_LIST_KEY = "idList";
    public static final String IS_TMP_KEY="isTmp";
    public static final String UNIQUE_ID_KEY="unique_id";
    public static final String PROJECT_ID_KEY="projectId";

    public static JsonObject transRwsConditionConfig(JsonArray condition) throws ConfigExcept {
        if (JsonAttrUtil.isEmptyJsonElement(condition)) return null;
        JsonObject configJson = null;
        for (JsonElement element : condition) {
            JsonObject config = new JsonObject();
            transActiveConfig(element.getAsJsonObject(), config);
            if (configJson == null)
                configJson = config;
            else {
                JsonArray detail = JsonAttrUtil.getJsonArrayValue(ExpressInterface.DETAILS_ARRAY_KEY, configJson);
                JsonArray tmpDetail = JsonAttrUtil.getJsonArrayValue(ExpressInterface.DETAILS_ARRAY_KEY, config);
                detail.addAll(tmpDetail);
            }
        }
        return configJson;
    }

    public static Set<String> getRefIdList(JsonObject condition) {
        Set<String> idList = new TreeSet<>();
        traceForRefIdList(condition, idList);
        return idList;
    }

    private static void traceForRefIdList(JsonObject condition, Collection<String> idList) {
        String operator = JsonAttrUtil.getStringValue(ExpressInterface.OPERATOR_KEY, condition);
        LogicExpressEnum logic = AbstractLogicExpress.checkLogicExpress(operator);
        if (logic != null) {
            JsonArray detail = JsonAttrUtil.getJsonArrayValue(ExpressInterface.DETAILS_ARRAY_KEY, condition);
            if (detail == null) return;
            for (JsonElement element : detail) {
                traceForRefIdList(element.getAsJsonObject(), idList);
            }
        } else {
            String id = JsonAttrUtil.getStringValue(UNARY_REF_ID_KEY, condition);
            if (!StringUtil.isEmptyStr(id)) idList.add(id);
            id = JsonAttrUtil.getStringValue(DYADIC_REF_ID_KEY, condition);
            if (!StringUtil.isEmptyStr(id)) idList.add(id);
        }

    }


    public static final String RWS_ATTR_CONDITION_KEY = "attr";

    public static void transConfigItemForCondition(JsonObject configJson) throws ConfigExcept {
        JsonArray attr = JsonAttrUtil.getJsonArrayValue(RWS_ATTR_CONDITION_KEY, configJson);
        Set<String> idList = new TreeSet<>();
        for (JsonElement element : attr) {
            JsonObject attrItem = element.getAsJsonObject();
            JsonObject conditionJson = transRwsConditionConfig(attrItem.getAsJsonArray("conditions"));
            idList.addAll(getRefIdList(conditionJson));
            attrItem.remove("conditions");
            String id=JsonAttrUtil.getStringValue(RwsConfigTransUtils.UNIQUE_ID_KEY,attrItem);
            attrItem.add(RwsCountUtils.CONDTION_KEY, conditionJson);
            if(StringUtil.isEmptyStr(id))idList.add(id);
        }
        configJson.add(REF_ID_LIST_KEY, JsonAttrUtil.toJsonTree(idList));
    }

    private static void transActiveConfig(JsonObject configItem, JsonObject result) throws ConfigExcept {
        String operator = JsonAttrUtil.getStringValue("operatorSign", configItem);
        String needPath = JsonAttrUtil.getStringValue("needPath", configItem);
        JsonArray originDetail = JsonAttrUtil.getJsonArrayValue("detail", configItem);
        LogicExpressEnum logicExpressEnum = AbstractLogicExpress.checkLogicExpress(operator);
        if (logicExpressEnum == null) {
            InstructionOperatorEnum itemEnum = ConditionOperatorFactory.check(operator);
            if (itemEnum == null) {
                String ref = JsonAttrUtil.getStringValue("refRelation", configItem);
                RefEnum refEnum = null;
                try {
                    refEnum = RefEnum.valueOf(ref.toUpperCase());
                } catch (Exception e) {
                    throw new ConfigExcept("配置错误 ref " + ref);
                }
                JsonElement value = JsonAttrUtil.getJsonElement("value", configItem);
                if (JsonAttrUtil.isEmptyJsonElement(value)) {
                    throw new ConfigExcept("配置错误  value is null");
                }
                if (StringUtil.isEmptyStr(operator))
                    throw new ConfigExcept("配置错误  operator is null");

                String[] ops = operator.split("#");
                if (ops.length <= 1) {
                    throw new ConfigExcept("配置错误  operator " + operator + " 最小长度 2");
                }
                String type = ops[0];

                //复合运算
                if (type.equalsIgnoreCase("simpleDate") || type.equalsIgnoreCase("simpleNumber")) {
                    if (refEnum == RefEnum.REF) {
                        throw new ConfigExcept("配置错误 不应该有右引用 ");
                    }
                    if (type.equalsIgnoreCase("simpleDate") && ops[1].equalsIgnoreCase("scope")) {
                        result.addProperty(ExpressInterface.OPERATOR_KEY, "and");
                        result.addProperty(ExpressInterface.NEED_PATH_KEY, needPath);
                        JsonArray array = value.getAsJsonArray();
                        LinkedList<JsonObject> newDetail = new LinkedList<>();
                        if (!JsonAttrUtil.isEmptyJsonElement(array.get(0))) {
                            JsonPrimitive newValue = new JsonPrimitive(">;=;" + array.get(0).getAsString());
                            JsonObject newChildJson = new JsonObject();
                            setSimpleProp(configItem, newChildJson, type, "", newValue);
                            newDetail.add(newChildJson);
                        }
                        if (!JsonAttrUtil.isEmptyJsonElement(array.get(1))) {
                            JsonPrimitive newValue = new JsonPrimitive("<;=;" + array.get(1).getAsString());
                            JsonObject newChildJson = new JsonObject();
                            setSimpleProp(configItem, newChildJson, type, "", newValue);
                            newDetail.add(newChildJson);
                        }
                        result.add(ExpressInterface.DETAILS_ARRAY_KEY, JsonAttrUtil.toJsonTree(newDetail));
                    } else {
                        JsonPrimitive newValue = new JsonPrimitive(ops[1] + ";" + value.getAsString());
                        setSimpleProp(configItem, result, type, needPath, newValue);
                    }
                } else {
                    if (refEnum != RefEnum.REF) {
                        throw new ConfigExcept("配置错误 必须有右引用 ");
                    }
                    String newop = null;
                    if (type.equalsIgnoreCase("refDateScope")) newop = "simpleDate";
                    else if (type.equalsIgnoreCase("refNumberScope")) newop = "simpleNumber";
                    else throw new ConfigExcept("配置错误  operator unknown " + operator);
                    String countType = ops[1];
                    boolean equal = false;
                    if (ops.length == 3 && ops[2].equalsIgnoreCase("=")) equal = true;
                    if (newop.equalsIgnoreCase("simpleDate")) equal = true;
                    JsonArray array = value.getAsJsonArray();
                    LinkedList<JsonObject> newDetail = new LinkedList<>();
                    if (!JsonAttrUtil.isEmptyJsonElement(array.get(0))) {
                        String newValue = ">;";
                        if (equal) {
                            newValue = newValue + "=";
                        }
                        JsonObject newChildJson = new JsonObject();
                        newChildJson.addProperty(RIGHT_OP_KEY, countType);
                        newChildJson.add(RIGHT_OP_PARAM_KEY, array.get(0));
                        setSimpleProp(configItem, newChildJson, newop, "", new JsonPrimitive(newValue));
                        newDetail.add(newChildJson);
                    }
                    if (!JsonAttrUtil.isEmptyJsonElement(array.get(1))) {
                        String newValue = "<;";
                        if (equal) {
                            newValue = newValue + "=";
                        }
                        JsonObject newChildJson = new JsonObject();
                        newChildJson.addProperty(RIGHT_OP_KEY, countType);
                        newChildJson.add(RIGHT_OP_PARAM_KEY, array.get(1));
                        setSimpleProp(configItem, newChildJson, newop, "", new JsonPrimitive(newValue));
                        newDetail.add(newChildJson);
                    }
                    result.addProperty(ExpressInterface.OPERATOR_KEY, "and");
                    result.addProperty(ExpressInterface.NEED_PATH_KEY, needPath);
                    result.add(ExpressInterface.DETAILS_ARRAY_KEY, JsonAttrUtil.toJsonTree(newDetail));
                }

            } else {
                JsonElement value = JsonAttrUtil.getJsonElement("value", configItem);
                if (JsonAttrUtil.isEmptyJsonElement(value)) {
                    throw new ConfigExcept("配置错误  value is null for operator "+operator);
                }
                setSimpleProp(configItem, result, operator, needPath, value);
            }


        } else {
            result.addProperty(ExpressInterface.OPERATOR_KEY, operator);
            result.addProperty(ExpressInterface.NEED_PATH_KEY, needPath);
            LinkedList<JsonObject> detail = new LinkedList<>();
            for (JsonElement element : originDetail) {
                JsonObject innerConfig = new JsonObject();
                detail.add(innerConfig);
                transActiveConfig(element.getAsJsonObject(), innerConfig);
            }
            result.add(ExpressInterface.DETAILS_ARRAY_KEY, JsonAttrUtil.toJsonTree(detail));
        }
    }

    private static void setSimpleProp(JsonObject configItem, JsonObject result, String operator, String needPath, JsonElement value) throws ConfigExcept {
        String ref = JsonAttrUtil.getStringValue("refRelation", configItem);
        RefEnum refEnum = null;
        try {
            refEnum = RefEnum.valueOf(ref.toUpperCase());
        } catch (Exception e) {
            throw new ConfigExcept("配置错误 ref " + ref);
        }
        if (JsonAttrUtil.isEmptyJsonElement(value)) {
            throw new ConfigExcept("配置错误  value is null");
        }
        result.add(StaticValueOperandDatas.VALUE_OLD_KEY, value);
        result.addProperty(ExpressInterface.NEED_PATH_KEY, needPath);
        result.addProperty(ExpressInterface.OPERATOR_KEY, operator);
        if (refEnum == RefEnum.DIRECT) {
            String unaryKey = JsonAttrUtil.getStringValue("sourceTagName", configItem);
            if (StringUtil.isEmptyStr(unaryKey)) {
                throw new ConfigExcept("配置错误  sourceTagName is null");
            }
            result.addProperty(ExpressInterface.DETAIL_KEY, unaryKey);
        } else {
            String refId = JsonAttrUtil.getStringValue("refActiveId", configItem);
            if (StringUtil.isEmptyStr(refId)) {
                throw new ConfigExcept("配置错误  refActiveId is null");
            }
            setRefProp(result, refEnum, refId);
        }
    }

    private static void setRefProp(JsonObject result, RefEnum refEnum, String refId) {
        if (refEnum == RefEnum.LEFTREF) {
            result.addProperty(OperandDataFactory.UNARY_TYPE_KEY, OperandDataFactory.MAP_STATIC_TYPE);
            result.addProperty(UNARY_REF_ID_KEY, refId);
            result.addProperty(OperandDataFactory.UNARY_MAP_NAME_KEY, RWS_STATIC_MAP_TABLE_NAME);
        } else if (refEnum == RefEnum.REF) {
            result.addProperty(OperandDataFactory.DYADIC_TYPE_KEY, OperandDataFactory.MAP_STATIC_TYPE);
            result.addProperty(DYADIC_REF_ID_KEY, refId);
            result.addProperty(OperandDataFactory.DYADIC_MAP_NAME_KEY, RWS_STATIC_MAP_TABLE_NAME);
        }
    }

    public static void transConfigItemForNewNeedPath(JsonObject configJson, String needPath) {
        JsonArray attr = JsonAttrUtil.getJsonArrayValue(RWS_ATTR_CONDITION_KEY, configJson);
        for (JsonElement element : attr) {
            JsonObject attrItem = element.getAsJsonObject();
            JsonObject conditionJson = attrItem.get(RwsCountUtils.CONDTION_KEY).getAsJsonObject();
            traceForNeedPath(conditionJson, needPath);
        }
    }

    private static void traceForNeedPath(JsonObject condition, String needPath) {
        String operator = JsonAttrUtil.getStringValue(ExpressInterface.OPERATOR_KEY, condition);
        LogicExpressEnum logic = AbstractLogicExpress.checkLogicExpress(operator);
        if (logic != null) {
            JsonArray detail = JsonAttrUtil.getJsonArrayValue(ExpressInterface.DETAILS_ARRAY_KEY, condition);
            if (detail == null) return;
            String oldNeedPath = JsonAttrUtil.getStringValue(ExpressInterface.NEED_PATH_KEY, condition);
            if (".".equals(oldNeedPath)) {
                condition.addProperty(ExpressInterface.NEED_PATH_KEY, needPath);
            }
            for (JsonElement element : detail) {
                traceForNeedPath(element.getAsJsonObject(), needPath);
            }
        } else {
            String oldNeedPath = JsonAttrUtil.getStringValue(ExpressInterface.NEED_PATH_KEY, condition);
            if (".".equals(oldNeedPath)) {
                condition.addProperty(ExpressInterface.NEED_PATH_KEY, needPath);
            }
        }

    }

    public static void transActiveConfig(JsonObject configJson) throws ConfigExcept {
        checkActiveConfig(configJson);
        String countPath = JsonAttrUtil.getStringValue(ACTIVE_RESULT_KEY, configJson);
        transConfigItemForCondition(configJson);
        transConfigItemForNewNeedPath(configJson, countPath);


    }

    public static void checkFilterPatientConfig(JsonObject configJson) throws ConfigExcept {
        String project_id = JsonAttrUtil.getStringValue(PROJECT_ID_KEY, configJson);
        if (StringUtil.isEmptyStr(project_id))
            throw new ConfigExcept("project_id null");
        /*JsonArray match = JsonAttrUtil.getJsonArrayValue("match", configJson);
        if (JsonAttrUtil.isEmptyJsonElement(match))
            throw new ConfigExcept("match is null");*/
        String resultsql = JsonAttrUtil.getStringValue(RESULTSQL_KEY, configJson);
        if (StringUtil.isEmptyStr(resultsql)) {
            throw new ConfigExcept("resultsql is null");
        }
    }

    public static void transFilterPatientConfig(JsonObject configJson) throws ConfigExcept {
        checkFilterPatientConfig(configJson);
        JsonArray match = JsonAttrUtil.getJsonArrayValue("match", configJson);
        JsonArray filter = JsonAttrUtil.getJsonArrayValue("filter", configJson);
        Set<String> idList = new TreeSet<>();
        JsonObject matchJson = transRwsConditionConfig(match);
        configJson.add("match", matchJson);
        traceForRefIdList(matchJson, idList);
        if (filter != null) {
            JsonObject filterJson = transRwsConditionConfig(filter);
            configJson.add("filter", filterJson);
            traceForRefIdList(filterJson, idList);
        }
        configJson.add(REF_ID_LIST_KEY, JsonAttrUtil.toJsonTree(idList));
    }

    public static void checkActiveConfig(JsonObject configJson) throws ConfigExcept {
        String countPath = JsonAttrUtil.getStringValue(ACTIVE_RESULT_KEY, configJson);
        String resultsql = JsonAttrUtil.getStringValue(RESULTSQL_KEY, configJson);
        if (StringUtil.isEmptyStr(resultsql)) {
            throw new ConfigExcept("resultsql is null");
        }
        if (StringUtil.isEmptyStr(countPath)) {
            throw new ConfigExcept("count path is null");
        }
        String projectId = JsonAttrUtil.getStringValue(PROJECT_ID_KEY, configJson);
        String unique_id = JsonAttrUtil.getStringValue(UNIQUE_ID_KEY, configJson);
        if (StringUtil.isEmptyStr(projectId)) {
            throw new ConfigExcept("projectId is null");
        }
        if (StringUtil.isEmptyStr(unique_id)) {
            throw new ConfigExcept("unique_id is null");
        }
        String method = JsonAttrUtil.getStringValue(RWS_CONF_METHOD_KEY, configJson);
        if (StringUtil.isEmptyStr(method)) {
            throw new ConfigExcept("method is null");
        }
        JsonArray attr = JsonAttrUtil.getJsonArrayValue(RwsConfigTransUtils.RWS_ATTR_CONDITION_KEY, configJson);
        if (JsonAttrUtil.isEmptyJsonElement(attr)) {
            throw new ConfigExcept("attr is null");
        }
        int activeType = JsonAttrUtil.getJsonElement(ACTIVE_TYPE_KEY, configJson).getAsInt();
        String sortKey = JsonAttrUtil.getStringValue(SORT_PATH_KEY, configJson);
        if (isActivity(activeType)) {
            if (StringUtil.isEmptyStr(sortKey)) {
                throw new ConfigExcept("sortKey is null");
            }
        }
        if (!isStaticMethod(method)) {
            method = method.toUpperCase();
            try {
                NumberOpEnum.valueOf(method);
            } catch (Exception e) {
                try {
                    ArrayOpEnum.valueOf(method);
                } catch (Exception e2) {
                    throw new ConfigExcept("unknown method " + method);
                }
            }
        }
    }
}