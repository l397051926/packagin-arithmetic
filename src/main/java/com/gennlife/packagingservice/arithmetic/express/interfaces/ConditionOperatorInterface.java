package com.gennlife.packagingservice.arithmetic.express.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;

/**
 * Created by Chenjinfeng on 2017/6/22.
 */
public interface ConditionOperatorInterface {
    void setTarget(String target);

    default boolean checkList(Collection list) {
        return list != null && list.size() > 0;
    }

    default void setTarget(JsonArray target) {
        throw new UnsupportedOperationException(" unsupport  setTarget(JsonArray target)");
    }

    default void setTarget(JsonObject target) {
        throw new UnsupportedOperationException(" unsupport  setTarget(JsonObject target)");
    }

    boolean check(String source);

    default boolean check(JsonElement source) throws Exception {
        if (source == null) throw new NullPointerException("source is null");
        if (source.isJsonPrimitive()) return check(source.getAsString());
        if (source.isJsonNull()) return false;
        if (source.isJsonArray()) {
            boolean flag = false;
            for (JsonElement jsonElement : source.getAsJsonArray()) {
                flag = check(jsonElement);
                if (flag) return true;
            }
            return false;
        }
        throw new Exception("unsupport for jsonobject");
    }


}
