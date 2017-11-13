package com.gennlife.packagingservice.arithmetic.assign;

import com.google.gson.JsonElement;

/**
 * Created by Chenjinfeng on 2017/11/11.
 */
public final class DirectClassifier implements Classifier {
    @Override
    public String getClassifierKey(JsonElement source) {
        return source.getAsString();
    }
}
