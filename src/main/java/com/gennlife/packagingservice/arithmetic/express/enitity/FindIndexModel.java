package com.gennlife.packagingservice.arithmetic.express.enitity;


import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractPath;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chenjinfeng on 2017/5/23.
 * json 路径解析
 */

public class FindIndexModel<T> {
    private static Logger logger = LoggerFactory.getLogger(FindIndexModel.class);
    private boolean leaf;
    transient private FindIndexModel p;
    private int index;
    private String key;
    private T value; //值
    private transient LinkedList<PathItem> pathList;

    public FindIndexModel() {
        this.index = -1;
        this.leaf = false;
    }

    public FindIndexModel(FindIndexModel p, String key, T value, boolean leaf) {
        this.p = p;
        this.key = key;
        this.value = value;
        this.leaf = leaf;
        this.index = -1;
    }

    public FindIndexModel(FindIndexModel p, String key, T value, int index) {
        this.p = p;
        this.key = key;
        this.value = value;
        this.leaf = false;
        this.index = index;
    }

    public static LinkedList<FindIndexModel<JsonElement>> getAllValue(String detailkey, List<FindIndexModel<JsonElement>> findIndexModels, LinkedList<FindIndexModel<JsonElement>> init) {
        if (findIndexModels == null || findIndexModels.size() == 0) return null;
        String key = removeUncareTag(detailkey);
        String[] keys = null;
        if (init == null) init = new LinkedList<>();
        for (FindIndexModel<JsonElement> findIndexModelItem : findIndexModels) {
            LinkedList<FindIndexModel<JsonElement>> pathList = new LinkedList<>();
            pathList.push(findIndexModelItem);
            while (findIndexModelItem.getP() != null && !StringUtil.isEmptyStr(findIndexModelItem.getKey())) {
                pathList.push(findIndexModelItem.getP());
                findIndexModelItem = findIndexModelItem.getP();
            }
            int i = -1;
            String newPath = "";
            FindIndexModel<JsonElement> find = null;
            for (FindIndexModel<JsonElement> item : pathList) {
                if (StringUtil.isEmptyStr(item.getKey()) && item.getP() == null) {
                    if (find == null) find = item;//指向根路径
                    continue;
                }
                i++;
                if (keys == null) keys = key.split("\\.");
                SplitStrForKeyAndIndex splitStrForKeyAndIndex = new SplitStrForKeyAndIndex(keys[i]);
                if (splitStrForKeyAndIndex.isEqual(item)) {
                    find = item;
                } else {
                    newPath = keys[i];
                    break;
                }

            }
            if (find == null) continue;
            if (i == -1) {
                newPath = key;
            } else if (i < keys.length - 1) {
                if (StringUtil.isEmptyStr(newPath)) {
                    i++;
                    newPath = keys[i];
                }
                for (int j = i + 1; j < keys.length; j++)
                    newPath = newPath + "." + keys[j];
            }
            if (StringUtil.isEmptyStr(newPath)) {
                init.add(find);
            } else
                init.addAll(JsonAttrUtil.getAllValueWithAnalisePath(newPath, find));
        }
        return init;
    }

    public static LinkedList<FindIndexModel<JsonElement>> getAllValue(String detailkey, List<FindIndexModel<JsonElement>> findIndexModels) {
        return getAllValue(detailkey, findIndexModels, null);
    }

    public static String removeUncareTag(String detailkey) {
        return detailkey.replace("$.", "").replace("[#]", "").replace("[*]", "");
    }

    public static LinkedList<PathItem> getFindvisitindexForTime(LinkedList<FindIndexModel<JsonElement>> data, ConditionCheck globalcheck) {
        LinkedList<PathItem> findindex = globalcheck.getFindIndex(data);
        if (findindex != null) {
            return findindex;
        }
        return null;
    }

    public static String buildVisitPathWhenConditionIsEmpty(String from, String groupName) {
        if (groupName.startsWith("data.visits[") && from.startsWith("visits.")) {
            Matcher matcher = Pattern.compile("data.visits\\[(\\d+)\\]").matcher(groupName);
            if (matcher.find()) {
                from = "visits[" + matcher.group(1) + "]." + from.substring("visits.".length());
            }
        }
        return from;
    }

    public static <T extends AbstractPath> String buildPathByPathItem(String path, List<T> findPathindex) {
        if (findPathindex == null) return path;
        if (path == null) {
            path = path;
        }
        String key = path.replace("$.", "");
        String[] keys = key.split("\\.");
        String newPath = null;
        int i = -1;
        for (T pathItem : findPathindex) {
            i++;
            int find = keys[i].indexOf("[");
            String tmpkey = keys[i];
            if (find == 0) {
                logger.error("error config " + keys[i]);
            } else if (find > 0) tmpkey = keys[i].substring(0, find).trim();
            if (pathItem.getKey().equals(tmpkey)) {
                String tmp = tmpkey;
                if (pathItem.getIndex() >= 0)
                    tmp = tmp + "[" + pathItem.getIndex() + "]";
                if (StringUtil.isEmptyStr(newPath)) {
                    newPath = tmp;
                } else
                    newPath = newPath + "." + tmp;
            } else {
                if (StringUtil.isEmptyStr(newPath)) {
                    newPath = keys[i];
                } else
                    newPath = newPath + "." + keys[i];
                break;
            }
        }
        if (i < keys.length) {
            i++;
            for (int j = i; j < keys.length; j++)
                newPath = newPath + "." + keys[j];
        }
        return newPath;
    }

    public static LinkedList<PathItem> getFindPath(FindIndexModel<JsonElement> data) {
        LinkedList<PathItem> list = new LinkedList<>();
        if (data == null) return null;
        FindIndexModel<JsonElement> p = data.getP();
        while (p != null) {
            String key = p.getKey();
            int index = p.getIndex();
            PathItem pathItem = new PathItem(key, index);
            list.add(pathItem);
            p = p.getP();

        }
        Collections.reverse(list);
        list.removeFirst();
        list.add(new PathItem(data.getKey()));
        return list;
    }

    public String getRef() {
        String str = "";
        FindIndexModel tmpP = this.p;
        String tmpKey = this.key;
        FindIndexModel begin = this;
        while (begin != null && !StringUtil.isEmptyStr(begin.getKey())) {
            String value = begin.getKey();
            if (begin.getIndex() >= 0) value = value + "[" + begin.getIndex() + "]";
            if (StringUtil.isEmptyStr(str)) str = value;
            else str = value + "." + str;
            begin = begin.getP();
        }
        return str;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public FindIndexModel getP() {
        return p;
    }

    public void setP(FindIndexModel p) {
        this.p = p;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public LinkedList<PathItem> getPathItem() {
        if (pathList == null) {
            pathList = new LinkedList<>();
            FindIndexModel tmp = this;
            while (tmp != null) {
                if (StringUtil.isEmptyStr(tmp.getKey())) break;
                pathList.push(new PathItem(tmp.getKey(), tmp.getIndex()));
                tmp = tmp.getP();
            }
        }
        return pathList;
    }

}
