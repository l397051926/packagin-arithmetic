package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractPath;

/**
 * Created by Chenjinfeng on 2017/10/20.
 */
public class PathItem extends AbstractPath {
    String key;
    int index = -1;

    public PathItem(String key, int index) {
        this.key = key;
        this.index = index;
    }

    public PathItem(String key) {
        this.key = key;
        this.index = -1;
    }

    public PathItem(AbstractPath item) {
        this.key = item.getKey();
        this.index = item.getIndex();
    }

    public String getKey() {
        return key;
    }

    public int getIndex() {
        return index;
    }


}
