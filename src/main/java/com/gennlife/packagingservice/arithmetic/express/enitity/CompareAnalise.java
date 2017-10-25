package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.interfaces.FormatInterface;
import com.gennlife.packagingservice.arithmetic.utils.DateUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
public class CompareAnalise<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompareAnalise.class);
    private T data;
    private boolean needLess;
    private boolean needLarge;
    private boolean needEqual;
    private boolean hasError;
    private FormatInterface<T> format;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isNeedLess() {
        return needLess;
    }

    public void setNeedLess(boolean needLess) {
        this.needLess = needLess;
    }

    public boolean isNeedLarge() {
        return needLarge;
    }

    public void setNeedLarge(boolean needLarge) {
        this.needLarge = needLarge;
    }

    public boolean isNeedEqual() {
        return needEqual;
    }

    public void setNeedEqual(boolean needEqual) {
        this.needEqual = needEqual;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public void setFormat(FormatInterface<T> format) {
        this.format = format;
    }

    public void parse(String target) {
        String[] items = target.replace(" ", "").split(";");
        if (items.length < 2) throw new RuntimeException("length must >=2 " + target);
        for (String item : items) {
            if (item.equals("小于") || item.equals("<")) {
                needLess = true;
            } else if (item.equals("大于") || item.equals(">")) {
                needLarge = true;
            } else if (item.equals("等于") || item.equals("=")) {
                needEqual = true;
            } else if (item.equals("不等于") || item.equals("!=")) {
                needEqual = false;
            } else
                try {
                    data = format.format(item);
                } catch (Exception e) {
                    logger.error("", e);
                    setHasError(true);
                    break;
                }
        }
    }

    public final static FormatInterface<String> FORMATFORTIME = new FormatInterface<String>() {
        @Override
        public String format(String source) throws Exception {
            if (StringUtil.isEmptyStr(source))
                throw new Exception("source must not null");
            DateUtil.getDate(source);
            return source;
        }
    };
    public final static FormatInterface<Double> FORMATFORNUMBER = new FormatInterface<Double>() {
        @Override
        public Double format(String source) throws Exception {
            if (StringUtil.isEmptyStr(source))
                throw new Exception("source must not null");
            return Double.valueOf(source);
        }
    };

    public static <T> CompareAnalise getFormatData(String str, FormatInterface<T> format) {
        CompareAnalise<T> compareAnalise = new CompareAnalise<T>();
        compareAnalise.setFormat(format);
        compareAnalise.parse(str);
        return compareAnalise;
    }

    public static CompareAnalise getFormatDateData(String str) {
        return getFormatData(str, FORMATFORTIME);
    }
    public static CompareAnalise getFormatNumberData(String str) {
        return getFormatData(str, FORMATFORNUMBER);
    }
}
