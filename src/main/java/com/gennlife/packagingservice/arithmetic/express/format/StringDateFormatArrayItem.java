package com.gennlife.packagingservice.arithmetic.express.format;

import com.gennlife.packagingservice.arithmetic.express.interfaces.FormatArrayItem;

import java.util.Date;

/**
 * Created by Chenjinfeng on 2017/10/25.
 */
public class StringDateFormatArrayItem implements FormatArrayItem<Object, String> {
    public static final StringDateFormatArrayItem INSTACE = new StringDateFormatArrayItem();

    @Override
    public <T> String format(T item) {
        String itemStr=getStr(item);
        Date result = DateYmdFormatArrayItem.INSTANCE.format(itemStr);
        if (result == null) return null;
        else
            return itemStr;
    }
}
