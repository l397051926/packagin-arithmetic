package com.gennlife.packagingservice.arithmetic.express.abstracts;

import com.gennlife.packagingservice.arithmetic.express.enitity.FindIndexModel;
import com.gennlife.packagingservice.arithmetic.express.enitity.SplitStrForKeyAndIndex;
import com.gennlife.packagingservice.arithmetic.express.exceptions.PathError;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chenjinfeng on 2017/7/18.
 */
public abstract class AbstractPath {
    private static Logger logger= LoggerFactory.getLogger(AbstractPath.class);

    public static <T extends AbstractPath> String getPath(List<T> list) {
        String path = "";
        if (list == null || list.size() == 0) return path;
        for (AbstractPath pathItem : list) {
            if (!StringUtil.isEmptyStr(path))
                path = path + ".";
            if (pathItem.getIndex() < 0) {
                path = path + pathItem.getKey();
            } else
                path = path + pathItem.getKey() + "[" + pathItem.getIndex() + "]";
        }
        return path;
    }

    public static <T extends AbstractPath> String getPath(List<T> indexs, JsonObject elemJson, String key) throws Exception {
        return getPath(indexs, JsonAttrUtil.getStringValue(key, elemJson));
    }

    public static <T extends AbstractPath> String getPath(List<T> indexs, String from) {
        from = FindIndexModel.removeUncareTag(from);
        String resultfrom = "";
        String[] froms = from.split("\\.");
        if ( froms.length == 0) {
            throw new PathError("unsupport " + from);
        }
        int i = 0;
        if (indexs != null) {
            for (AbstractPath index : indexs) {
                if(froms.length<=i)
                    break;
                SplitStrForKeyAndIndex spindex = new SplitStrForKeyAndIndex(froms[i]);
                if (spindex.isEqual(index)) {
                    String tmp = index.getIndex() >= 0 ? spindex.getKey() + "[" + index.getIndex() + "]" : spindex.getKey();
                    if (StringUtil.isEmptyStr(resultfrom)) {
                        resultfrom = tmp;
                    } else resultfrom = resultfrom + "." + tmp;
                } else break;
                i++;
            }
        }
        for (int j = i; j < froms.length; j++) {
            String tmp = froms[j];
            if (StringUtil.isEmptyStr(resultfrom)) resultfrom = tmp;
            else resultfrom = resultfrom + "." + froms[j];
        }
        return resultfrom;
    }

    public static <T extends AbstractPath>int getVisitIndexInPath(List<T> indexs) {
        if (indexs == null) return -1;
        for (AbstractPath pathItem : indexs)
            if ("visits".equals(pathItem.getKey())) return pathItem.getIndex();
        return -1;
    }

    public abstract String getKey();

    public abstract int getIndex();
    public boolean isEqual(AbstractPath pathItem) {
        return this.getKey().equals(pathItem.getKey()) && (this.getIndex() == -1 || this.getIndex() == pathItem.getIndex());
    }
    public static <T extends AbstractPath> String getGroupName(LinkedList<T> indexs)  {
        if(indexs==null||indexs.size()==0)return null;
        T last=indexs.removeLast();
        StringBuffer buffer=new StringBuffer();
        for(T item:indexs)
        {
            buffer.append(item.getKey()).append(".");
        }
        buffer.append(last.getKey());
        return buffer.toString();
    }


}
