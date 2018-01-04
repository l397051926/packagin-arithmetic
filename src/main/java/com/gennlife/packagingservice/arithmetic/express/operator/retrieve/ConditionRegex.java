package com.gennlife.packagingservice.arithmetic.express.operator.retrieve;


import com.gennlife.packagingservice.arithmetic.express.abstracts.DyadicOperationRightIsStaticValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chenjinfeng on 2017/6/26.
 */
public class ConditionRegex extends DyadicOperationRightIsStaticValue {
    private String regex;
    private static final Logger logger = LoggerFactory.getLogger(ConditionRegex.class);

    @Override
    protected void setTarget(JsonArray jsonArray) {
        throw new UnsupportedOperationException("array is not for regex");
    }

    public void setTarget(String target) {
        this.regex = target;

    }

    protected boolean checkItem(String source) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(source);
        return m.find();
    }

    protected boolean check(JsonElement source) {
        try {
            if (source == null) return false;
            if (source.isJsonPrimitive()) {
                return checkItem(source.getAsString());
            } else if (source.isJsonArray()) {
                boolean findflag = false;
                for (JsonElement item : source.getAsJsonArray()) {
                    findflag = check(item);
                    if (findflag) return true;
                }
            }
            return false;
        } catch (Exception e) {
            setHasError(true);
            //logger.error("错误的正则 " + regex);
            return false;
        }
    }

    @Override
    public boolean allEmptyList() {
        return false;
    }
}
