package com.gennlife.packagingservice.rws;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.*;
import com.gennlife.packagingservice.arithmetic.express.enums.ArrayOpEnum;
import com.gennlife.packagingservice.arithmetic.express.enums.NumberOpEnum;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.ObtainUtils;
import com.gennlife.packagingservice.rws.entity.CountValueEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static com.gennlife.packagingservice.rws.RwsConfigTransUtils.RWS_STATIC_VALUE_KEY;

/**
 * Created by Chenjinfeng on 2017/10/28.
 */
public class RwsCountUtils {
    public static final int ACTIVITY = 1;//事件
    public static final int QUOTA = 2;//指标
    public static final int FILTER=3;//入排
    public static String SORT_PATH_KEY = "sortKey";
    public static String METHOD_PARAM_KEYS = "functionParam";
    public static final String CONDTION_KEY = "condtion";
    public static final String ACTIVE_RESULT_KEY = "activeResult";
    public static final String RWS_CONF_METHOD_KEY = "function";
    public static final String ACTIVE_TYPE_KEY = "activeType";
    public static final String COUNT_CONFIG_KEY = "count";
    public static final String FILTER_CONFIG_KEY = "filter";
    private static final Logger logger = LoggerFactory.getLogger(RwsCountUtils.class);

    public static CountValueEntity count(JsonObject patient, JsonObject config, String countPath, MapSourceDataWrapper rwsRef, boolean checkFlag) throws ConfigExcept {
        String method = JsonAttrUtil.getStringValue(RWS_CONF_METHOD_KEY, config);
        if (checkFlag) RwsConfigTransUtils.checkActiveConfig(config);
        JsonArray attr = JsonAttrUtil.getJsonArrayValue(RwsConfigTransUtils.RWS_ATTR_CONDITION_KEY, config);
        method = method.toUpperCase();
        if (isStaticMethod(method)) {
            return getStaticValue(patient, rwsRef, attr);
        }

        JsonObject valueConfig = attr.get(0).getAsJsonObject();
        int activeType = JsonAttrUtil.getJsonElement(ACTIVE_TYPE_KEY, config).getAsInt();
        if(activeType!=ACTIVITY && activeType!=QUOTA)
        {
            throw new ConfigExcept("activeType must be 1 or 2 ,now: "+activeType);
        }
        JsonObject condtion = JsonAttrUtil.getJsonObjectValue(CONDTION_KEY, valueConfig);
        ConditionCheck conditionCheck = new ConditionCheck(condtion);
        conditionCheck.addData(RwsConfigTransUtils.RWS_STATIC_MAP_TABLE_NAME, rwsRef);
        PathNode find = conditionCheck.getPathItemsByPathNode(patient);
        if (find == null) {
            if (isActivity(activeType))
                return getEmptyUnMatchForActive();
            else
                return getMatchNa();
        }

        String sortKey = JsonAttrUtil.getStringValue(SORT_PATH_KEY, config);
        List<FindIndexModel<JsonElement>> countList = null;
        if (isActivity(activeType)) {
            countList = ObtainUtils.getSortResultByAscDate(patient, find, sortKey, countPath);
        } else
            countList = PathNode.getFindIndexModels(patient, find, countPath);
        if (countList == null || countList.size() == 0) {
            return getEmptyMatch(activeType);
        }
        CountValueEntity numberResult = getNumberCountValueEntity(method, countList);
        if (numberResult != null) return numberResult;
        ArrayOpEnum arrayOpEnum = null;
        try {
            arrayOpEnum = ArrayOpEnum.valueOf(method);
        } catch (Exception e) {

        }
        String intValue = JsonAttrUtil.getStringValue(METHOD_PARAM_KEYS, config);
        Integer numberParam = null;
        try {
            numberParam = Integer.valueOf(intValue);
        } catch (Exception e) {
            numberParam = null;
        }
        List<FindIndexModel<JsonElement>> match = ObtainUtils.obtainList(countList, arrayOpEnum, numberParam);
        if (match == null || match.size() == 0) {
            return getEmptyMatch(activeType);
        } else {
            if (isActivity(activeType)) {
                LinkedList<LinkedList<PathItem>> matchPathItem = new LinkedList<>();
                for (FindIndexModel<JsonElement> matchItem : match) {
                    matchPathItem.add(matchItem.getPathItem());
                }
                PathNode mergeNode = PathNode.getPathNodeFromPath(matchPathItem);
                find.mergeForLeaveAndAddNew(mergeNode, true);
                return new CountValueEntity(match.size(), true).setPath(find);

            } else {
                JsonElement valueElem = match.get(0).getValue();
                if (!valueElem.isJsonPrimitive()) {

                    return getMatchNa();
                }
                return new CountValueEntity(valueElem, true);
            }

        }
    }

    private static CountValueEntity getMatchNa() {
        return new CountValueEntity("", false);
    }

    public static boolean isStaticMethod(String method) {
        return "static".equalsIgnoreCase(method);
    }

    private static CountValueEntity getStaticValue(JsonObject patient, MapSourceDataWrapper rwsRef, JsonArray attr) {
        JsonElement value = null;
        JsonArray resultValue = new JsonArray();
        JsonElement emptyValue = null;
        for (JsonElement element : attr) {
            JsonObject staticConfig = element.getAsJsonObject();
            JsonObject condtion = JsonAttrUtil.getJsonObjectValue(CONDTION_KEY, staticConfig);
            value = JsonAttrUtil.getJsonElement(RWS_STATIC_VALUE_KEY, staticConfig);
            if (JsonAttrUtil.isEmptyJsonElement(condtion)) {
                emptyValue = value;
                continue;
            }
            ConditionCheck conditionCheck = new ConditionCheck(condtion);
            conditionCheck.addData(RwsConfigTransUtils.RWS_STATIC_MAP_TABLE_NAME, rwsRef);
            PathNode find = conditionCheck.getPathItemsByPathNode(patient);
            if (find != null) {
                resultValue.add(value);
            }
        }
        if (resultValue.size() == 0) resultValue.add(emptyValue);
        return new CountValueEntity(resultValue, true);
    }

    private static CountValueEntity getEmptyUnMatchForActive() {
        return new CountValueEntity().setValue(0).setPath(null).setMatch(false);
    }

    private static CountValueEntity getEmptyMatch(int activeType) {
        if (isActivity(activeType)) {
            return new CountValueEntity().setValue(0).setPath(null).setMatch(false);
        } else
            return getMatchNa();
    }

    public static boolean isActivity(int activeType) {
        return activeType == ACTIVITY;
    }

    private static CountValueEntity getNumberCountValueEntity(String method, List<FindIndexModel<JsonElement>> resultList) {
        try {
            //数字类型
            NumberOpEnum numberOpEnum = null;
            numberOpEnum = NumberOpEnum.valueOf(method);
            NumberResultEntity numberResult = ObtainUtils.obtainNum(resultList, numberOpEnum);
            if (numberResult == null) return getMatchNa();
            return new CountValueEntity(numberResult.getValue(), true);
        } catch (Exception e) {

        }
        return null;
    }
}
