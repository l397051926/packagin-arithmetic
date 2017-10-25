package com.gennlife.packagingservice.arithmetic.express.abstracts;

import com.gennlife.packagingservice.arithmetic.express.ArithmeticExpress;
import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.express.logic.AndLogicExpress;
import com.gennlife.packagingservice.arithmetic.express.logic.OrLogicExpress;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Chenjinfeng on 2017/10/16.
 */
public abstract class AbstractLogicExpress implements ExpressInterface {
    private static final Logger logger= LoggerFactory.getLogger(AbstractLogicExpress.class);
    private JsonArray detail;
    private String operator;
    protected PathNode tmpNode;
    protected PathNode globalPathNode;
    private ConditionCheck conditionCheck;
    protected boolean findflag;
    private String needPath;

    public AbstractLogicExpress(ConditionCheck conditionCheck, JsonArray detail, String operator, PathNode globalPathNode,String path) {
        this.needPath=path;
        this.detail = detail;
        this.operator = operator;
        this.globalPathNode = globalPathNode;
        this.conditionCheck = conditionCheck;
    }

    public String getNeedPath() {
        return needPath;
    }

    protected void setNotFind() {
        this.findflag = false;
        this.globalPathNode = null;
        this.tmpNode = null;
    }

    @Override
    public ExpressInterface parse() {
        if (globalPathNode == null) throw new RuntimeException("global Path node must not null");
        ExpressInterface express = null;
        for (JsonElement detailItem : detail) {
            JsonObject detailItemJson = detailItem.getAsJsonObject();
            if (detailItemJson.has(conditionCheck.REFKEY)) {
                detailItemJson = conditionCheck.getRef(detailItemJson);
            }
            String detailoperator = JsonAttrUtil.getStringValue(OPERATOR_KEY, detailItemJson);
            JsonArray detail = JsonAttrUtil.getJsonArrayValue(DETAILS_ARRAY_KEY, detailItemJson);
            AbstractLogicExpress logicexpress = buildLogicExpress(this.conditionCheck, detail, detailoperator, getPathNodeForChild(),detailItemJson);
            if (logicexpress != null) {
                express = logicexpress.parse();
            } else {
                ArithmeticExpress arithmeticExpress = new ArithmeticExpress(this.conditionCheck, getPathNodeForChild(), detailoperator, detailItemJson).parse();
                if (arithmeticExpress.hasError()) {
                    this.findflag = false;
                    break;
                }
                express = arithmeticExpress;
            }
            boolean isContinue = this.isContinue(express);
            if (!isContinue) {
                break;
            }
        }
        parseAfter();
        return this;
    }

    protected abstract void parseAfter();

    public abstract boolean isContinue(ExpressInterface express);

    public static AbstractLogicExpress buildLogicExpress(ConditionCheck conditionCheck, JsonArray detail, String operator, PathNode globalPathNode,JsonObject config) {
        if (operator != null) {
            String path=JsonAttrUtil.getStringValue(NEED_PATH_KEY,config);
            if (operator.equalsIgnoreCase("or")) {
                return new OrLogicExpress(conditionCheck, detail, operator, globalPathNode, path);
            }
            if (operator.equalsIgnoreCase("and")) {
                return new AndLogicExpress(conditionCheck, detail, operator, globalPathNode, path);
            }
            if (operator.equalsIgnoreCase("not")) {
                throw new UnsupportedOperationException("unsupported for not ");
            }
        }
        return null;
    }

    @Override
    public boolean hasError() {
        return false;
    }


    @Override
    public boolean isFindflag() {
        return findflag;
    }


    @Override
    public PathNode getFindPathNode() {
        return tmpNode;
    }

    public JsonArray getDetail() {
        return detail;
    }

    public abstract PathNode getPathNodeForChild();

    public PathNode getGlobalPathNode() {
        return globalPathNode;
    }

    public ConditionCheck getConditionCheck() {
        return conditionCheck;
    }
}
