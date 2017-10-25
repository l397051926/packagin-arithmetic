package com.gennlife.packagingservice.arithmetic.express.enitity;


import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractPath;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Created by Chenjinfeng on 2017/6/20.
 */
public class SplitStrForKeyAndIndex extends AbstractPath {
    private static final Logger logger = LoggerFactory.getLogger(SplitStrForKeyAndIndex.class);
    private String s;
    private int findex = -1;
    private String tmpKey;
    private boolean isAny=false;

    public SplitStrForKeyAndIndex(String s) {
        this.s = s;
        this.analise();
    }


    public int getIndex() {
        return findex;
    }

    public String getKey() {
        return tmpKey;
    }

    public void analise() {
        int lbracket = s.indexOf("[");
        findex = -1;
        tmpKey = s;
        if (lbracket == 0) {
            logger.error("error config " + s);
        } else if (lbracket > 0) {
            tmpKey = tmpKey.substring(0, lbracket);
            String tmpindexStr = s.substring(lbracket + 1, s.length() - 1).trim();
            if (tmpindexStr.equals("*") || tmpindexStr.equals("#")) {
                findex = -1;
                isAny=true;
            } else findex = Integer.valueOf(tmpindexStr);
        }
    }



    public boolean isEqual(FindIndexModel<JsonElement> item) {
        return this.getKey().equals(item.getKey()) && (this.getIndex() == -1 || this.getIndex() == item.getIndex());
    }

    public static LinkedList<SplitStrForKeyAndIndex> createPaths(String str)
    {
        if(StringUtil.isEmptyStr(str))return null;
        String[] keys=str.split("\\.");
        LinkedList<SplitStrForKeyAndIndex> result=new LinkedList<>();
        for(String key:keys)
        {
            result.add(new SplitStrForKeyAndIndex(key));
        }
        return result;
    }

    public void setIndex(int findex) {
        this.findex = findex;
    }

    public void setKey(String tmpKey) {
        this.tmpKey = tmpKey;
    }
    public boolean hasIndex()
    {
        return this.getIndex()>-1;
    }
    public boolean isAny()
    {
        return isAny;
    }
}
