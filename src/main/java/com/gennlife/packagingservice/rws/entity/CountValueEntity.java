package com.gennlife.packagingservice.rws.entity;

import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;

/**
 * Created by Chenjinfeng on 2017/10/28.
 */
public class CountValueEntity {
    private Object value;
    private PathNode path;
    private boolean isMatch;

    public CountValueEntity(Object value, boolean isMatch) {
        this.value = value;
        this.setMatch(isMatch);
    }

    public CountValueEntity() {
    }

    public Object getValue() {
        return value;
    }

    public CountValueEntity setValue(Object value) {
        this.value = value;
        return this;
    }

    public PathNode getPath() {
        return path;
    }

    public CountValueEntity setPath(PathNode path) {
        this.path = path;
        return this;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public CountValueEntity setMatch(boolean match) {
        isMatch = match;
        return this;
    }
}
