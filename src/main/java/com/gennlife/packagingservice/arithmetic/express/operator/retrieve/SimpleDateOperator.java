package com.gennlife.packagingservice.arithmetic.express.operator.retrieve;

import com.gennlife.packagingservice.arithmetic.express.abstracts.DyadicOperationRightIsStaticValue;
import com.gennlife.packagingservice.arithmetic.express.enitity.CompareAnalise;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
public class SimpleDateOperator extends DyadicOperationRightIsStaticValue {
    private static final Logger logger = LoggerFactory.getLogger(DateHmsCompareOperator.class);
    /**
     * 当天 时分秒对比
     */
    private String hmsTime;
    private boolean needLess;
    private boolean needLarge;
    private boolean needEqual;

    @Override
    protected void setTarget(JsonArray jsonArray) {
        throw new UnsupportedOperationException("DateHmsCompareOperator unspport for  array");
    }

    @Override
    public void setTarget(String target) {
        CompareAnalise<String> result = CompareAnalise.getFormatDateData(target);
        if (result.isHasError()) setHasError(true);
        else {
            this.hmsTime = result.getData();
            this.needEqual = result.isNeedEqual();
            this.needLarge = result.isNeedLarge();
            this.needLess = result.isNeedLess();
        }
    }


    public boolean check(String source) {
        if (StringUtil.isEmptyStr(source)) return false;
        return getCompareResult(source, hmsTime);
    }

    @Override
    protected boolean check(JsonElement source) {
        if (source == null) return false;
        if (source.isJsonPrimitive()) {
            return check(source.getAsString());
        }
        logger.error("only support for String");
        setHasError(true);
        return false;
    }

    public boolean getCompareResult(String source, String tmpdate) {
        int compare = source.compareTo(tmpdate);
        if (isNeedEqual() && compare == 0) return true;
        if (isNeedLess() && compare < 0) return true;
        return isNeedLarge() && compare > 0;
    }

    @Override
    public boolean allEmptyList() {
        return false;
    }

    public String getHmsTime() {
        return hmsTime;
    }

    public boolean isNeedLess() {
        return needLess;
    }

    public boolean isNeedLarge() {
        return needLarge;
    }

    public boolean isNeedEqual() {
        return needEqual;
    }

    @Override
    protected boolean needMerge() {
        return true;
    }

    @Override
    protected JsonElement merge(JsonElement value, JsonElement staticValue) {
        try {
            return new JsonPrimitive(value.getAsString() + ";" + staticValue.getAsString());
        } catch (Exception e) {
            logger.error("config error "+value+" "+staticValue);
            setHasError(true);
        }
        return null;
    }
}
