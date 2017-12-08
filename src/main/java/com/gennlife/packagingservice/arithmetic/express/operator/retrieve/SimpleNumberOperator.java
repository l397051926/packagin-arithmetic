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
 * Created by Chenjinfeng on 2017/9/15.
 */
public class SimpleNumberOperator extends DyadicOperationRightIsStaticValue {
    private static Logger logger = LoggerFactory.getLogger(SimpleNumberOperator.class);
    private double number;
    private boolean needLess;
    private boolean needLarge;
    private boolean needEqual;

    @Override
    protected void setTarget(JsonArray jsonArray) {
        throw new UnsupportedOperationException();
    }


    public void setTarget(String target) {
        CompareAnalise<Double> result = CompareAnalise.getFormatNumberData(target);
        if (result.isHasError()) setHasError(true);
        else {
            this.number = result.getData();
            this.needEqual = result.isNeedEqual();
            this.needLarge = result.isNeedLarge();
            this.needLess = result.isNeedLess();
        }
    }


    public boolean check(String source) {
        if (StringUtil.isEmptyStr(source)) return false;
        double sourcenum = 0;
        try {
            sourcenum = Double.valueOf(source);
        } catch (Exception e) {
            //logger.error("error in SimpleNumberOperator " + source);
            setHasError(true);
            return false;
        }
        if (needEqual && sourcenum == number) return true;
        if (needLess && sourcenum < number) return true;
        return needLarge && sourcenum > number;
    }

    @Override
    protected boolean check(JsonElement source) {
        if (source == null) return false;
        if (source.isJsonPrimitive()) {
            return check(source.getAsString());
        }
        setHasError(true);
       // logger.error("unsupport for not number ");
        return false;
    }

    @Override
    public boolean allEmptyList() {
        return false;
    }

    @Override
    protected boolean needMerge() {
        return true;
    }

    @Override
    protected JsonElement merge(JsonElement value, JsonElement staticValue) {
        try {
            if (value == null || value.isJsonNull()) {
                return staticValue;
            }
            return new JsonPrimitive(value.getAsString() + ";" + staticValue.getAsString());
        } catch (Exception e) {
           // logger.error("merge error " + value + " " + staticValue);
            setHasError(true);
        }
        return null;
    }
}
