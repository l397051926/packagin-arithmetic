package com.gennlife.packagingservice.rws.entity;

import com.google.gson.JsonElement;

/**
 * Created by Chenjinfeng on 2017/11/17.
 */
public class RwsObjId {
    private String id;
    private JsonElement param;
    private String countType;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonElement getParam() {
        return param;
    }

    public void setParam(JsonElement param) {
        this.param = param;
    }

    public String getCountType() {
        return countType;
    }

    public void setCountType(String countType) {
        this.countType = countType;
    }
}
