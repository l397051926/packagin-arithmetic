package com.gennlife.packagingservice.arithmetic.express.format;

import com.gennlife.packagingservice.arithmetic.express.interfaces.FormatArrayItem;
import com.gennlife.packagingservice.arithmetic.utils.DateUtil;

import java.util.Date;

/**
 * Created by Chenjinfeng on 2017/10/25.
 */
public class DateYmdFormatArrayItem implements FormatArrayItem<Object, Date> {
    public static final DateYmdFormatArrayItem INSTANCE = new DateYmdFormatArrayItem();

    @Override
    public <T> Date format(T item) {
        if (item == null) return null;
        String str = getStr(item);
        if (str == null) {
            return null;
        }
        return DateUtil.getDate(str);
    }



}
