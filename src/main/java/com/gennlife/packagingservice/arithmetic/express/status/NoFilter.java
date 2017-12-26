package com.gennlife.packagingservice.arithmetic.express.status;

import com.google.gson.JsonElement;

/**
 * Created by Chenjinfeng on 2017/12/25.
 */
public class NoFilter extends AbsFindIndexModelFilter {

    @Override
    public boolean isMatch(JsonElement target) {
        return true;
    }
}
