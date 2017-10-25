package com.gennlife.packagingservice.arithmetic.express.interfaces;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/25.
 */
public interface FormatArrayItem<T ,R extends Comparable> {
    static final Logger logger= LoggerFactory.getLogger(FormatArrayItem.class);
    public  <T> R format(T item) ;
    public default  <T> String getStr(T item) {
        String str = null;
        if (item instanceof String)
            str = (String) item;
        else if (item instanceof JsonElement) {
            str = ((JsonElement) item).getAsString();
        } else {
            logger.error("unknown  " + item.getClass().getName());
        }
        return str;
    }
}
