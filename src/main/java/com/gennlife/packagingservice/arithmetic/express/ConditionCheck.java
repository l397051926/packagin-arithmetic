package com.gennlife.packagingservice.arithmetic.express;

import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractLogicExpress;
import com.gennlife.packagingservice.arithmetic.express.enitity.*;
import com.gennlife.packagingservice.arithmetic.express.exceptions.ConfigError;
import com.gennlife.packagingservice.arithmetic.express.exceptions.ExpressNoOriginDataError;
import com.gennlife.packagingservice.arithmetic.express.interfaces.SourceDataWrapperInterface;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface.DETAILS_ARRAY_KEY;
import static com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface.OPERATOR_KEY;

/**
 * Created by Chenjinfeng on 2017/6/15.
 * 条件过滤查询
 */
public class ConditionCheck {
    protected JsonObject toDoCondition;
    protected JsonObject globalcondition;
    protected String operator;
    protected JsonArray detail;
    protected JsonObject condition;
    private static final String ORIGIN_DATA_PATH_KEY = "ORIGIN_DATA_PATH";
    private String lastPath;
    private static final String ORIGIN_DATA_KEY = "ORIGIN_DATA";
    private Map<String, SourceDataWrapperInterface> dataMap;
    protected static Logger logger = LoggerFactory.getLogger(ConditionCheck.class);
    public static final String REFKEY = "ref";
    private boolean init = false;

    public ConditionCheck(JsonObject toDoCondition, JsonObject globalcondition) {
        if (toDoCondition != null) this.toDoCondition = JsonAttrUtil.deepCopy(toDoCondition).getAsJsonObject();
        else this.toDoCondition = null;
        this.globalcondition = globalcondition;
        if (toDoCondition == null) {
            this.condition = null;
            return ;
        }
        if (!toDoCondition.has("condition")) {
            this.condition = toDoCondition;
        } else {
            this.condition = toDoCondition.getAsJsonObject("condition");
        }
        initCondition();
    }

    public String getLastPath() {
        return lastPath;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    public ConditionCheck(JsonObject condition) {
        this.condition = condition;
    }

    public void initCondition() {
        init = true;
        JsonArray addition = null;
        if (condition.has("addition")) addition = condition.getAsJsonArray("addition");
        if (condition.has(REFKEY)) {
            condition = getRef(condition);
        }
        operator = condition.get(OPERATOR_KEY).getAsString();
        detail = condition.getAsJsonArray(DETAILS_ARRAY_KEY);
        boolean refInAddtion = true;
        if (condition.has("refInAddition")) {
            refInAddtion = condition.get("refInAddition").getAsBoolean();
        }
        if (addition != null) {
            if (refInAddtion)
                detail.addAll(addition);
            else {
                JsonArray array = new JsonArray();
                array.addAll(addition);
                array.addAll(detail);
                detail = array;
            }
        }
        LinkedList<JsonElement> refArray = new LinkedList<>();
        LinkedList<JsonElement> otherArray = new LinkedList<>();
        for (JsonElement jsonElement : detail) {
            if (jsonElement == null || jsonElement.isJsonNull()) continue;
            if (!jsonElement.isJsonObject()) {
                logger.error("detail has error not json ");
            }
            String isRef = JsonAttrUtil.getStringValue("isRef", jsonElement.getAsJsonObject());
            if ("true".equalsIgnoreCase(isRef)) {
                refArray.add(jsonElement);
            } else otherArray.add(jsonElement);
        }
        otherArray.addAll(refArray);
        detail = JsonAttrUtil.toJsonTree(otherArray).getAsJsonArray();
    }

    public boolean hasCondition() {
        return this.condition != null;
    }

    public boolean isFindflag(LinkedList<FindIndexModel<JsonElement>> detailVisit) {
        if (!hasCondition()) return true;
        LinkedList<PathItem> findindexs = getFindIndex(detailVisit);
        return findindexs != null;
    }

    public JsonObject getRef(JsonObject conditionJson) {
        if (!globalcondition.has(conditionJson.get(REFKEY).getAsString())) {
            logger.error("no ref " + conditionJson.get(REFKEY).getAsString());
        }
        conditionJson = JsonAttrUtil.deepCopy(globalcondition.getAsJsonObject(conditionJson.get(REFKEY).getAsString())).getAsJsonObject();
        return conditionJson;
    }

    /**
     * 获取满足条件的一个
     */
    public LinkedList<PathItem> getFindIndex(LinkedList<FindIndexModel<JsonElement>> lists) {
        JsonArray detail = this.detail;
        String operator = this.operator;
        LinkedList<LinkedList<PathItem>> tmp = getPathItems(lists, detail, operator, null);
        if (tmp == null || tmp.size() == 0) {
            return null;
        }
        return tmp.getFirst();
    }

    /**
     * 获取满足条件的多个
     */
    public LinkedList<LinkedList<PathItem>> getAllPathItemS(LinkedList<FindIndexModel<JsonElement>> lists) {
        JsonArray detail = this.detail;
        String operator = this.operator;
        LinkedList<LinkedList<PathItem>> index = getPathItems(lists, detail, operator, null);
        return index;
    }

    public LinkedList<LinkedList<PathItem>> getAllPathItemS(LinkedList<FindIndexModel<JsonElement>> lists, LinkedList<PathItem> tmpfindindex) {
        JsonArray detail = this.detail;
        String operator = this.operator;
        LinkedList<LinkedList<PathItem>> init = null;
        if (tmpfindindex != null && tmpfindindex.size() > 0) {
            init = new LinkedList<>();
            init.add(tmpfindindex);
        }
        LinkedList<LinkedList<PathItem>> index = getPathItems(lists, detail, operator, init);
        return index;
    }

    public LinkedList<FindIndexModel<JsonElement>> filter(LinkedList<FindIndexModel<JsonElement>> lists) {
        return filterByPathNode(lists, null);
    }

    public <T extends JsonElement> LinkedList<FindIndexModel<JsonElement>> filterInJson(LinkedList<T> lists) {
        return filter(exchange(lists));
    }

    public <T extends JsonElement> LinkedList<FindIndexModel<JsonElement>> exchange(LinkedList<T> lists) {
        LinkedList<FindIndexModel<JsonElement>> result = new LinkedList<>();
        for (JsonElement element : lists) {
            FindIndexModel<JsonElement> findIndexModel = new FindIndexModel<>();
            findIndexModel.setValue(element);
            result.add(findIndexModel);
        }
        return result;
    }


    //for crf

    /**
     * only for crf
     */

    public LinkedList<LinkedList<PathItem>> getPathItems(LinkedList<FindIndexModel<JsonElement>> lists, JsonArray detail, String operator, LinkedList<LinkedList<PathItem>> tmpfindindex) {
        this.detail = detail;
        this.operator = operator;
        PathNode pathNode = PathNode.getPathNodeFromPath(tmpfindindex);
        PathNode result = getPathItemsByPathNode(lists, pathNode);
        if (result == null) {
            return null;
        }
        return PathNode.getPathItem(result, getLastPath());
    }

    public PathNode getPathItemsByPathNode(JsonObject patient) {
        LinkedList<FindIndexModel<JsonElement>> lists = new LinkedList<>();
        FindIndexModel findIndexModel = new FindIndexModel();
        findIndexModel.setValue(patient);
        findIndexModel.setKey(null);
        findIndexModel.setP(null);
        findIndexModel.setLeaf(true);
        lists.add(findIndexModel);
        return getPathItemsByPathNode(lists, null);
    }

    public PathNode getPathItemsByPathNode(LinkedList<FindIndexModel<JsonElement>> lists, PathNode pathNode) {
        setOrigindata(lists);
        if (init == false) initCondition();
        if (pathNode == null) pathNode = new PathNode();
        AbstractLogicExpress logicexpress = AbstractLogicExpress.buildLogicExpress(this, detail, operator, pathNode, condition);
        if (logicexpress == null) throw new ConfigError("error operator " + operator);
        logicexpress.parse();
        PathNode tmpPath = logicexpress.getFindPathNode();
        return tmpPath;
    }

    public SourceDataWrapperInterface getDataWrapper(String key) {
        if (dataMap != null) {
            return dataMap.get(key);
        }
        return null;
    }


    public LinkedList<FindIndexModel<JsonElement>> getOriginData() {
        DirectSourceDataWrapper result = (DirectSourceDataWrapper) getDataWrapper(ORIGIN_DATA_KEY);
        if (result == null || result.getData() == null || result.getData().size() == 0) {
            throw new ExpressNoOriginDataError();
        }
        return result.getData();
    }

    public PathNode getOriginDataPath() {
        PathNodeDataWrapper result = (PathNodeDataWrapper) getDataWrapper(ORIGIN_DATA_PATH_KEY);
        if (result == null || result.getData() == null) {
            throw new ExpressNoOriginDataError();
        }
        return result.getData();
    }

    public void setOrigindata(LinkedList<FindIndexModel<JsonElement>> origindata) {
        DirectSourceDataWrapper directDataWrapper = new DirectSourceDataWrapper();
        directDataWrapper.setData(origindata);
        addData(ORIGIN_DATA_KEY, directDataWrapper);
        PathNode dataPath = PathNode.getPathNodeFromJson(origindata);
        PathNodeDataWrapper pathNodeDataWrapper = new PathNodeDataWrapper(dataPath);
        addData(ORIGIN_DATA_PATH_KEY, pathNodeDataWrapper);
    }


    public void addData(String key, SourceDataWrapperInterface data) {
        if (dataMap == null) dataMap = new HashMap<>();
        dataMap.put(key, data);
    }

    public LinkedList<FindIndexModel<JsonElement>> filterByPathNode(LinkedList<FindIndexModel<JsonElement>> lists, PathNode tmpPathNode) {
        if (lists == null) return null;
        PathNode findIndex;
        LinkedList<FindIndexModel<JsonElement>> result = new LinkedList<>();
        for (FindIndexModel<JsonElement> item : lists) {
            LinkedList<FindIndexModel<JsonElement>> tmpList = new LinkedList<>();
            tmpList.add(item);
            findIndex = getPathItemsByPathNode(tmpList, tmpPathNode);
            if (findIndex != null) result.add(item);
        }
        return result;
    }

    public void setGlobalCondition(JsonObject globalCondition) {
        this.globalcondition = globalCondition;
    }


}
