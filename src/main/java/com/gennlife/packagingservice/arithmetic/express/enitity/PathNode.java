package com.gennlife.packagingservice.arithmetic.express.enitity;


import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractPath;
import com.gennlife.packagingservice.arithmetic.express.exceptions.PathNodeError;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Chenjinfeng on 2017/10/16.
 */
public class PathNode {
    private transient LinkedList<PathItem> pPath;
    private LinkedHashMap<String, PathNode> map;
    private LinkedHashMap<Integer, PathNode> arrayItems;

    public static List<FindIndexModel<JsonElement>> getFindIndexModels(JsonObject patient, PathNode find, String countPath) {
        LinkedList<LinkedList<PathItem>> pathItems = getPathItem(find, countPath);
        List<FindIndexModel<JsonElement>> resultList = null;
        for (LinkedList<PathItem> pathitem : pathItems) {
            LinkedList<FindIndexModel<JsonElement>> tmp = JsonAttrUtil.getAllValueWithAnalisePath(AbstractPath.getPath(pathitem, countPath), patient);
            if (resultList == null) resultList = tmp;
            else {
                if (tmp == null) continue;
                resultList.addAll(tmp);
            }
        }
        return resultList;
    }

    public LinkedList<PathItem> getpPath() {
        return pPath;
    }

    public void setpPath(LinkedList<PathItem> pPath) {
        this.pPath = pPath;
    }

    public void add(PathNode node) {
        if (this == node) return;
        if (node == null || !node.isNotEmpty()) return;
        this.map = mergePathNodeMapByAdd(node.map, this.map);
        this.arrayItems = mergePathNodeMapByAdd(node.arrayItems, this.arrayItems);
    }

    private static <T> LinkedHashMap<T, PathNode> mergePathNodeMapByAdd(LinkedHashMap<T, PathNode> from, LinkedHashMap<T, PathNode> to) {
        if (to == null) return from;
        for (Map.Entry<T, PathNode> item : from.entrySet()) {
            if (!to.containsKey(item.getKey()))
                to.put(item.getKey(), item.getValue());
            else {
                PathNode target = to.get(item.getKey());
                if (target == null || !target.isNotEmpty()) {
                    to.put(item.getKey(), item.getValue());
                } else {
                    target.add(item.getValue());
                }
            }
        }
        return to;
    }

    public Map<Integer, PathNode> getArrayItems() {
        return arrayItems;
    }

    private void check() {
        if (this.arrayItems != null && this.arrayItems.size() > 0) {
            if (this.map != null && this.map.size() > 0) {
                throw new PathNodeError("error pathNode");
            }
        }
    }

    public static final String EMPTY_PATH = ".";

    public PathNode getNeedPathNode(String path) {
        if (StringUtil.isEmptyStr(path)) return this;
        if (path.equalsIgnoreCase(EMPTY_PATH)) return new PathNode();
        LinkedList<LinkedList<PathItem>> list = PathNode.getPathItem(this, path);
        return PathNode.getPathNodeFromPath(list);
    }


    public PathNode getJsonNode(String key) {
        return map.get(key);
    }

    public void setArrayItems(LinkedHashMap<Integer, PathNode> arrayItems) {
        this.arrayItems = arrayItems;
    }

    public boolean hasJsonObj() {
        return map != null && map.size() > 0;
    }

    public static LinkedList<LinkedList<PathItem>> getPathItem(PathNode headNode, String base) {
        if (headNode == null || StringUtil.isEmptyStr(base)) return null;
        LinkedList<SplitStrForKeyAndIndex> baseList = SplitStrForKeyAndIndex.createPaths(base);
        LinkedList<PathNode> preList = new LinkedList<>();
        SplitStrForKeyAndIndex first = baseList.removeFirst();
        if (headNode.hasJsonNodeKey(first.getKey())) {
            addMatchNode(headNode, preList, first, null);
        } else {
            LinkedList<LinkedList<PathItem>> resultList = new LinkedList<>();
            resultList.add(null);
            return resultList;
        }
        LinkedList<PathNode> nowList = new LinkedList<>();
        LinkedList<PathNode> swapList;
        if (preList != null && preList.size() > 0)
            for (SplitStrForKeyAndIndex item : baseList) {
                for (PathNode node : preList) {
                    LinkedList<PathItem> pPath = new LinkedList<>(node.getpPath());
                    addMatchNode(node, nowList, item, pPath);
                }
                if (nowList.size() == 0) break;
                else {
                    swapList = preList;
                    preList = nowList;
                    nowList = swapList;
                    nowList.clear();
                }
            }

        if (preList == null || preList.size() == 0) {
            return null;
        }
        LinkedList<LinkedList<PathItem>> resultPathList = new LinkedList<>();
        for (PathNode node : preList) {
            resultPathList.add(node.getpPath());
        }
        return resultPathList;
    }

    private static void addMatchNode(PathNode node, LinkedList<PathNode> nodeList, SplitStrForKeyAndIndex indexItem, LinkedList<PathItem> pPath) {
        if (node.hasJsonNodeKey(indexItem.getKey())) {
            PathNode tmp = node.getJsonNode(indexItem.getKey());
            if (tmp == null) return;
            if (indexItem.getIndex() < 0) {
                if (!tmp.hasArrayValue())
                    addNodeToList(pPath, nodeList, indexItem.getKey(), indexItem.getIndex(), tmp);
                else {
                    Map<Integer, PathNode> arrayItems = tmp.getArrayItems();
                    for (Map.Entry<Integer, PathNode> item : arrayItems.entrySet()) {
                        PathNode itemValue = item.getValue();
                        if (itemValue == null) {
                            itemValue = new PathNode();
                            arrayItems.put(item.getKey(), itemValue);
                        }
                        LinkedList<PathItem> newPpath = new LinkedList<>();
                        if (pPath != null) newPpath.addAll(pPath);
                        addNodeToList(newPpath, nodeList, indexItem.getKey(), item.getKey(), itemValue);
                    }
                }
            } else {
                int index = indexItem.getIndex();
                tmp = tmp.getArrayIndexValue(index);
                if (tmp != null) {
                    addNodeToList(pPath, nodeList, indexItem.getKey(), index, tmp);
                }
            }
        }
    }

    private static void addNodeToList(LinkedList<PathItem> pPath, LinkedList<PathNode> addList, String key, int index, PathNode tmp) {
        if (tmp == null)
            return;
        if (pPath == null) pPath = new LinkedList<>();
        pPath.add(new PathItem(key, index));
        tmp.setpPath(pPath);
        addList.add(tmp);
    }

    public boolean hasJsonNodeKey(String key) {
        if (!hasJsonObj()) return false;
        else return map.containsKey(key);
    }

    public void addJsonNode(String key, PathNode pathNode) {
        if (map == null) map = new LinkedHashMap<>();
        map.put(key, pathNode);
    }

    public boolean isNotEmpty() {

        if ((arrayItems == null || arrayItems.isEmpty()) && !hasJsonObj()) {
            return false;
        }
        return true;
    }

    public static <T extends AbstractPath> PathNode getPathNodeFromPath(LinkedList<LinkedList<T>> indexs) {
        if (indexs == null || indexs.size() == 0) return null;
        indexs.removeIf(item -> item == null || item.size() == 0);
        PathNode head = new PathNode();
        for (LinkedList<T> items : indexs) {
            PathNode p = getPathNodeFromPathByOne(items);
            if (p != null && p.isNotEmpty()) {
                head.add(p);
            }
        }
        return head;
    }

    public static <T extends AbstractPath> PathNode getPathNodeFromPathByOne(LinkedList<T> index) {
        if (index == null || index.size() == 0) return null;
        PathNode head = new PathNode();
        PathNode p = head;
        for (T item : index) {
            String key = item.getKey();
            if (!p.hasJsonNodeKey(key)) {
                PathNode tmp = new PathNode();
                p.addJsonNode(key, tmp);
            }
            p = p.getJsonNode(key);
            if (item.getIndex() > -1) {
                if (!p.hasArrayIndexValue(item.getIndex())) {
                    p.addArrayValue(item.getIndex(), new PathNode());
                }
                p = p.getArrayIndexValue(item.getIndex());
            }
        }
        return head;
    }

    private PathNode getArrayIndexValue(int index) {
        if (hasArrayValue()) {
            return arrayItems.get(index);
        }
        return null;
    }

    private void addArrayValue(int index, PathNode pathNode) {
        if (arrayItems == null) arrayItems = new LinkedHashMap<>();
        arrayItems.put(index, pathNode);
    }

    private boolean hasArrayIndexValue(int index) {
        if (!hasArrayValue()) return false;
        return arrayItems.containsKey(index);
    }

    /**
     * 保留公共组
     *
     * @Param addUnExists  true:添加node里面特有的元素
     */
    private static final Logger logger = LoggerFactory.getLogger(PathNode.class);

    public void mergeForLeaveAndAddNew(PathNode node, boolean addUnExists) {
        if (this == node) return;
        if (node == null || !node.isNotEmpty()) {
            return;
        }
        if (node.hasJsonObj() && this.hasJsonObj()) {
            for (Map.Entry<String, PathNode> item : node.map.entrySet()) {
                String key = item.getKey();
                if (map.containsKey(key)) {
                    PathNode target = map.get(key);
                    if (target == null || !target.isNotEmpty()) {
                        //pass
                    } else {
                        target.mergeForLeaveAndAddNew(item.getValue(), addUnExists);
                    }
                } else if (addUnExists) {
                    map.put(item.getKey(), item.getValue());
                }
            }
        } else if (addUnExists) {
            if (!this.hasJsonObj()) this.map = node.map;
        }
        if (node.hasArrayValue() && this.hasArrayValue()) {
            Set<Integer> keys = node.arrayItems.keySet();
            Set<Integer> baseKeys = this.arrayItems.keySet();
            LinkedList<Integer> removeKeys = new LinkedList<>();
            for (Integer i : baseKeys) if (!keys.contains(i)) removeKeys.add(i);
            for (Integer i : removeKeys) this.arrayItems.remove(i);
            for (Map.Entry<Integer, PathNode> item : arrayItems.entrySet()) {
                PathNode nextNode = node.getArrayItems().get(item.getKey());
                if (item.getValue() != null) {
                    item.getValue().mergeForLeaveAndAddNew(nextNode, addUnExists);
                } else
                    arrayItems.put(item.getKey(), nextNode);
            }

        } else if (addUnExists) {
            if (!this.hasArrayValue()) this.arrayItems = node.arrayItems;
        }
    }


    public boolean hasArrayValue() {
        return this.arrayItems != null && this.arrayItems.size() > 0;
    }

    public Map<String, PathNode> getMap() {
        return map;
    }

    public void setMap(LinkedHashMap<String, PathNode> map) {
        this.map = map;
    }

    public PathNode deepCopy() {
        PathNode copy = new PathNode();
        LinkedHashMap<String, PathNode> map = null;
        LinkedHashMap<Integer, PathNode> arrayItems = null;
        if (this.hasJsonObj()) {
            map = new LinkedHashMap<>();
            addDeepCopyIntoMap(map, this.getMap());
            copy.setMap(map);
        }
        if (this.hasArrayValue()) {
            arrayItems = new LinkedHashMap<>();
            addDeepCopyIntoMap(arrayItems, this.getArrayItems());
            copy.setArrayItems(arrayItems);
        }

        return copy;
    }

    public <T> void addDeepCopyIntoMap(Map<T, PathNode> map, Map<T, PathNode> source) {
        if (source == null)
            return;
        for (Map.Entry<T, PathNode> entity : source.entrySet()) {
            PathNode child = null;
            child = entity.getValue();
            if (child == null)
                map.put(entity.getKey(), child);
            else
                map.put(entity.getKey(), child.deepCopy());
        }
    }

    public static PathNode getPathNodeFromJson(LinkedList<FindIndexModel<JsonElement>> lists) {
        if (lists == null || lists.size() == 0) return null;
        PathNode result = new PathNode();
        for (FindIndexModel<JsonElement> model : lists) {
            result.add(getPathNodeFromJsonByOne(model));
        }
        return result;
    }

    public static PathNode getPathNodeFromJsonByOne(FindIndexModel<JsonElement> model) {
        if (model == null || (StringUtil.isEmptyStr(model.getKey()) && JsonAttrUtil.isEmptyJsonElement(model.getValue())))
            return null;
        while (model.getP() != null) {
            model = model.getP();
        }
        JsonElement value = model.getValue();
        PathNode next = getNextPathNode(value);
        String key = model.getKey();
        if (!StringUtil.isEmptyStr(key)) {
            PathNode init = new PathNode();
            int index = model.getIndex();
            if (index < 0) {
                init.addJsonNode(key, next);
            } else {
                init.addArrayValue(index, next);
            }
            return init;
        } else
            return next;

    }

    private static PathNode getNextPathNode(JsonElement value) {
        PathNode next = null;
        if (value == null) {
            next = null;
        } else if (value.isJsonObject()) {
            JsonObject json = value.getAsJsonObject();

            for (Map.Entry<String, JsonElement> item : json.entrySet()) {
                if (!isLeaf(item.getValue())) {
                    if (next == null) next = new PathNode();
                    next.addJsonNode(item.getKey(), getNextPathNode(item.getValue()));
                }
            }
        } else if (value.isJsonArray()) {
            JsonArray array = value.getAsJsonArray();
            if (array.size() > 0) {
                int i = 0;
                for (JsonElement element : array) {
                    if (!isLeaf(element)) {
                        if (next == null) next = new PathNode();
                        next.addArrayValue(i, getNextPathNode(element));
                    }
                    i++;
                }
            }
        }
        return next;
    }

    private static boolean isLeaf(JsonElement value) {
        return value == null || value.isJsonNull() || value.isJsonPrimitive();
    }

    public static PathNode getNeedPathNode(PathNode pathNode, String needPath) {
        if (pathNode == null) return null;
        return pathNode.getNeedPathNode(needPath);
    }

    public static boolean isNotEmpty(PathNode node) {
        return node != null && node.isNotEmpty();
    }

    public Set<String> getGroupName() {
        if (!isNotEmpty()) return null;
        if (!hasJsonObj()) {
            logger.error("must be json node ");
            return null;
        }
        Set<String> resultPathList = new TreeSet<>();
        LinkedList<PathNode> preList = new LinkedList<>();
        LinkedList<PathNode> nowList = new LinkedList<>();
        LinkedList<PathNode> swapList;
        preList.add(this);
        LinkedList<PathItem> pPath, newPath;
        while (preList.size() > 0) {
            for (PathNode node : preList) {
                if (node.hasJsonObj()) {
                    pPath = node.getpPath();
                    if (pPath == null) pPath = new LinkedList<>();
                    for (Map.Entry<String, PathNode> jsonItem : node.getMap().entrySet()) {
                        PathNode next = jsonItem.getValue();
                        String key = jsonItem.getKey();
                        if (isNotEmpty(next)) {
                            if (next.hasArrayValue()) {
                                boolean add = false;
                                LinkedList<PathNode> tmpArray = new LinkedList<>();
                                for (Map.Entry<Integer, PathNode> nextItem : next.getArrayItems().entrySet()) {
                                    PathNode nextItemNode = nextItem.getValue();
                                    if (isNotEmpty(nextItemNode)) {
                                        newPath = buildNewPath(pPath, key, nextItem.getKey());
                                        nextItemNode.setpPath(newPath);
                                        tmpArray.add(nextItemNode);
                                    } else {
                                        if (!add) {
                                            add = true;
                                            newPath = buildNewPath(pPath, key, nextItem.getKey());
                                            String groupName = AbstractPath.getGroupName(newPath);
                                            resultPathList.add(groupName);
                                        }
                                    }
                                }
                                if (!add) nowList.addAll(tmpArray);
                            } else {
                                newPath = buildNewPath(pPath, key, null);
                                next.setpPath(newPath);
                                nowList.add(next);
                            }
                        } else {
                            if (pPath != null && pPath.size() > 0) {
                                String groupName = AbstractPath.getGroupName(pPath);
                                resultPathList.add(groupName);
                            }
                        }
                    }
                }
            }
            swapList = preList;
            preList = nowList;
            nowList = swapList;
            nowList.clear();
        }
        return resultPathList;
    }

    private LinkedList<PathItem> buildNewPath(LinkedList<PathItem> pPath, String key, Integer index) {
        LinkedList<PathItem> newPath;
        newPath = new LinkedList<>();
        newPath.addAll(pPath);
        PathItem pathItem = null;
        if (index != null && index > -1)
            pathItem = new PathItem(key, index);
        else pathItem = new PathItem(key);
        newPath.add(pathItem);
        return newPath;
    }


}
