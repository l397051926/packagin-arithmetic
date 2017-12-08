package com.gennlife.packagingservice.arithmetic.express.abstracts;


import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Created by Chenjinfeng on 2017/9/13.
 * 对于数组满足其中一个即可
 */
public abstract class AbstractListOperator extends DyadicOperationRightIsStaticValue {
    protected LinkedList<String> target = new LinkedList<>();
    private static final Logger logger = LoggerFactory.getLogger(AbstractListOperator.class);
    private boolean isMatchAll = false;


    public void setMatchAll(boolean matchAll) {
        isMatchAll = matchAll;
    }


    public final void setTarget(String target) {
        if (StringUtil.isEmptyStr(target)) {
            //logger.error("can't be null ");
            setHasError(true);
            return;
        }
        this.target.add(target);
        JsonAttrUtil.sortForStandard(this.target);
    }

    public final void setTarget(JsonArray target) {
        for (JsonElement element : target) {
            setTarget(element.getAsString());
        }
    }

    @Override
    public boolean check(JsonElement source) {
        if (isMatchAll) {
            return matchAll(source);
        } else {
            return matchOne(source);
        }
    }

    private boolean matchOne(JsonElement source) {
        boolean flag = false;
        if (source == null) {
            return false;
        }
        if (source.isJsonPrimitive()) {
            String sourceStr = source.getAsString();
            for (String item : target) {
                flag = checkItem(sourceStr, item);
                if (flag == true) {
                    break;
                }
            }
        } else if (source.isJsonArray()) {
            for (JsonElement sourceItem : source.getAsJsonArray()) {
                flag = matchOne(sourceItem);
                if (flag == true) {
                    break;
                }
            }
        } else {
            return false;
        }
        return flag;
    }

    private boolean matchAll(JsonElement source) {
        boolean flag = false;
        if (source == null) {
            return false;
        }
        if (source.isJsonPrimitive()) {
            String sourceStr = source.getAsString();
            for (String item : target) {
                flag = checkItem(sourceStr, item);
                if (flag == false) {
                    break;
                }
            }
        } else if (source.isJsonArray()) {
            for (JsonElement sourceItem : source.getAsJsonArray()) {
                flag = matchAll(sourceItem);
                if (flag == false) {
                    break;
                }
            }
        } else {
            return false;
        }
        return flag;
    }

    protected abstract boolean checkItem(String source, String item);


    @Override
    public boolean allEmptyList() {
        return false;
    }

}
