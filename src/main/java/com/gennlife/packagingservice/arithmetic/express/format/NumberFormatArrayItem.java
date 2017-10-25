package com.gennlife.packagingservice.arithmetic.express.format;

import com.gennlife.packagingservice.arithmetic.express.interfaces.FormatArrayItem;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/25.
 */
public class NumberFormatArrayItem implements FormatArrayItem<Object, Double> {
    private static final Logger logger = LoggerFactory.getLogger(NumberFormatArrayItem.class);
    public static final NumberFormatArrayItem INSTANSE = new NumberFormatArrayItem();

    @Override
    public <T1> Double format(T1 item) {
        if (item instanceof Double) return (Double) item;
        else {
            if (item instanceof JsonElement) {
                String itemStr = getStr(item);
                try {
                    return Double.valueOf(itemStr);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        logger.error(" unknown type change to double " + item.getClass());
        return null;

    }
}
