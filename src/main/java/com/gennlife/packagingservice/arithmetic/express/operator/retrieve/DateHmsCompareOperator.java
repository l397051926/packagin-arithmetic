package com.gennlife.packagingservice.arithmetic.express.operator.retrieve;

import com.gennlife.packagingservice.arithmetic.utils.StringUtil;

/**
 * Created by Chenjinfeng on 2017/10/10.
 */
public class DateHmsCompareOperator extends SimpleDateOperator {
    public boolean check(String source) {
        if (StringUtil.isEmptyStr(source)) return false;
        String tmpdate = source.split(" ")[0];
        if (StringUtil.isEmptyStr(tmpdate)) return false;
        tmpdate = tmpdate + " " + getHmsTime();
        if (tmpdate.length() != source.length()) return false;
        return getCompareResult(source, tmpdate);
    }


}
