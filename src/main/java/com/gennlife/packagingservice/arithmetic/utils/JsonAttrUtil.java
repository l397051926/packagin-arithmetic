package com.gennlife.packagingservice.arithmetic.utils;

import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractPath;
import com.gennlife.packagingservice.arithmetic.express.enitity.FindIndexModel;
import com.gennlife.packagingservice.arithmetic.express.enitity.SplitStrForKeyAndIndex;
import com.gennlife.packagingservice.arithmetic.express.exceptions.PathError;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Json 字段过滤和保留
 * Created by Chenjinfeng on 2016/9/30.
 */
public class JsonAttrUtil {
    private static JsonParser jsonParser = new JsonParser();
    private static Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(JsonAttrUtil.class);

    public static JsonArray array_combine(JsonArray... arrays) {
        JsonArray result = new JsonArray();
        if (arrays.length == 0) return result;
        for (JsonArray item : arrays)
            if (item != null && item.size() > 0) {
                result.addAll(item);
            }
        return result;
    }

    public static void safelyAdd(JsonObject json, String key, JsonElement fromElem) {
        try {
            json.add(key, fromElem.getAsJsonArray().get(0));
        } catch (Exception e) {
        }
    }

    public static JsonObject analyse(String str, JsonObject json) {
        String[] keys = str.split("\\.");

        if (keys.length == 0) return null;
        for (int i = 0; i < keys.length - 1; i++) {
            if (json == null) return null;
            if (json.has(keys[i])) {
                try {
                    json = json.getAsJsonArray(keys[i]).get(0).getAsJsonObject();
                } catch (Exception e) {
                    logger.error("", e);
                    return null;
                }
            }
        }
        if (json.has(keys[keys.length - 1])) {
            json = json.getAsJsonObject(keys[keys.length - 1]);
            return json;
        } else
            return null;

    }

    public static JsonObject getOneFromJsonArray(String[] leave, JsonElement fromElem) {
        JsonElement from = null;
        try {
            from = fromElem.getAsJsonArray().get(0);
        } catch (Exception e) {
            return null;
        }
        return Leave(leave, from);

    }

    public static JsonObject LeaveWithDefaultValue(String[] leave, JsonElement fromElem, JsonElement defaultvalue) {
        JsonObject from = null;
        try {
            from = fromElem.getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
        JsonObject json = new JsonObject();
        for (String attr : leave) {
            JsonElement value = from.get(attr);
            if (value == null) {
                if (defaultvalue != null)
                    value = defaultvalue;
            }
            if (value != null) json.add(attr, value);
        }
        return json;
    }

    public static JsonObject Leave(String[] leave, JsonElement fromElem) {
        return LeaveWithDefaultValue(leave, fromElem, null);
    }


    /**
     * 批量赋值
     */
    public static JsonArray setAttr(String key, String value, JsonArray array) {
        for (JsonElement item : array)
            item.getAsJsonObject().addProperty(key, value);
        return array;
    }

    /**
     * 批量赋值
     */
    public static JsonArray setAttr(String key, int value, JsonArray array) {
        for (JsonElement item : array)
            item.getAsJsonObject().addProperty(key, value);
        return array;
    }

    /**
     * json联合
     */
    public static JsonArray combineJsonArray(JsonArray a1, JsonArray a2) {
        if (a1 == null && a2 == null) return null;
        if (a1 == null) return a2;
        if (a2 == null) return a1;
        a1.addAll(a2);
        return a1;

    }

    /**
     * 验证多个key是否存在
     */
    public static boolean checkKeys(JsonObject json, String[] keys) {
        for (String key : keys)
            if (json.has(key) == false)
                return false;
        return true;
    }


    public static void add_propery(JsonObject from, JsonObject to, String... keys) {
        for (String key : keys) {
            if (from.has(key)) to.add(key, from.get(key));

        }
    }

    public static boolean isEmptyArray(JsonObject json, String key) {
        if (!json.has(key)) return true;
        JsonElement elem = json.get(key);
        if (elem == null || !elem.isJsonArray()) return true;
        return elem.getAsJsonArray().size() <= 0;
    }

    public static boolean isEmptyJsonElement(JsonElement json) {
        if (json == null || json.isJsonNull()) return true;
        if (json.isJsonPrimitive() && StringUtil.isEmptyStr(json.getAsString())) return true;
        if (json.isJsonObject() && json.getAsJsonObject().entrySet().size() == 0) return true;
        return json.isJsonArray() && json.getAsJsonArray().size() == 0;
    }

    public static JsonElement toJsonTree(Object obj) {
        return gson.toJsonTree(obj);
    }

    public static JsonObject toJsonObjectWithLenient(String str) {
        if (StringUtil.isEmptyStr(str)) return null;
        try {
            return jsonParser.parse(new JsonReader(new StringReader(str))).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            logger.error("", e);
        } catch (IllegalStateException e) {
            logger.error("", e);
        } catch (NullPointerException e) {
            logger.error("", e);
        }
        return null;

    }

    public static JsonObject toJsonObject(Object obj) {
        if (obj != null && obj instanceof JsonObject) return (JsonObject) obj;
        try {
            return (JsonObject) toJsonTree(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static JsonObject toJsonObject(String str) {
        if (StringUtil.isEmptyStr(str)) return null;
        try {
            return jsonParser.parse(str).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            logger.error("", e);
        } catch (IllegalStateException e) {
            logger.error("", e);
        } catch (NullPointerException e) {
            logger.error("", e);
        }
        return null;

    }

    public static boolean has_key(JsonObject json, String key) {
        if (json == null) return false;
        if (!json.has(key)) return false;
        if (json.get(key).isJsonNull()) return false;
        if (json.get(key).isJsonPrimitive()) {
            return !StringUtil.isEmptyStr(json.get(key).getAsString());
        }
        return true;
    }


    public static String toJsonStr(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(JsonElement jsonElement, Type type) {
        if (jsonElement == null) return null;
        return gson.fromJson(jsonElement, type);
    }

    public static <T> T fromJson(JsonElement jsonElement, Class<T> type) {
        if (jsonElement == null) return null;
        return gson.fromJson(jsonElement, type);
    }

    public static <T> T fromJsonByTypeToken(JsonElement jsonElement, Class<T> type) {
        if (jsonElement == null) return null;
        return gson.fromJson(jsonElement, new TypeToken<T>() {
        }.getType());
    }

    public static <T> T fromJson(String json, Type class_type) {
        return gson.fromJson(json, class_type);
    }

    public static JsonObject getJsonObjectfromFile(String jsonfile) {
        return toJsonObject(getJsonStringfromFile(jsonfile));
    }

    public static String getJsonStringfromFile(String jsonfile) {
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonfile), "UTF-8"));
            String line = "";
            line = br.readLine();
            while (line != null) {
                buffer.append(line.trim());
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
        return buffer.toString();
    }

    public static List<JsonElement> getPagingResult(List<JsonElement> datalist, int page_size, int currentPage) {
        Iterator<JsonElement> iter = datalist.iterator();
        int offset = (currentPage - 1) * page_size;
        LinkedList<JsonElement> list = new LinkedList<JsonElement>();
        if (datalist == null || offset >= datalist.size())
            return list;
        if (currentPage == 1 && page_size > datalist.size())
            return datalist;
        while (iter.hasNext() && offset > 0) {
            iter.next();
            offset--;
        }
        int nowsize = page_size;
        while (iter.hasNext() && nowsize > 0) {
            list.add(iter.next());
            nowsize--;
        }
        return list;
    }

    public static LinkedList<JsonElement> jsonArrayToList(JsonArray array) {
        return JsonAttrUtil.fromJson(array, new TypeToken<LinkedList<JsonElement>>() {
        }.getType());
    }

    public static JsonArray strToJsonArray(JsonObject json, String key) {
        JsonArray result = new JsonArray();
        if (json.has(key)) {
            String[] tmp = json.get(key).getAsString().split(" ");
            for (String item : tmp) {
                result.add(item);
            }
        }
        return result;
    }

    public static String getStringValue(String key, JsonObject tmp) {
        String[] keys = key.split("\\.");
        Object obj = getObjValue(keys, tmp);
        if (obj instanceof String) return (String) obj;
        if (obj == null) return "";
        return toJsonStr(obj);
    }

    public static String getStringValueSplit(String[] keys, JsonObject tmp) {
        Object obj = getObjValue(keys, tmp);
        if (obj instanceof String) return (String) obj;
        if (obj == null) return null;
        return obj.toString();
    }

    public static JsonArray getJsonArrayValue(String key, JsonObject tmp) {
        String[] keys = key.split("\\.");
        Object obj = getObjValue(keys, tmp);
        if (obj instanceof JsonArray) return (JsonArray) obj;
        return null;
    }

    public static JsonElement getJsonElement(String key, JsonObject tmp) {
        String[] keys = key.split("\\.");
        Object object = getObjValue(keys, tmp);
        if (object == null) return null;
        if (object instanceof JsonElement) return (JsonElement) object;
        else return new JsonPrimitive((String) object);
    }

    /**
     * 数组取第一项
     */
    public static JsonObject getJsonObjectValue(String key, JsonObject tmp) {
        String[] keys = key.split("\\.");
        Object obj = getObjValue(keys, tmp);
        if (obj instanceof JsonArray) {
            if (((JsonArray) obj).size() > 0)
                try {
                    return ((JsonArray) obj).get(0).getAsJsonObject();
                } catch (Exception e) {
                    logger.error("", e);
                }
            else
                return null;
        } else if (obj instanceof JsonObject)
            return (JsonObject) obj;
        return null;
    }

    public static Object getObjValue(String[] keys, JsonObject tmp) {
        if (tmp == null) return null;
        for (int i = 0; i < (keys.length - 1); i++) {
            if (tmp.has(keys[i])) {
                JsonElement tmpelem = tmp.get(keys[i]);
                if (tmpelem.isJsonArray()) {
                    if (tmpelem.getAsJsonArray().size() == 0) return null;
                    tmp = tmpelem.getAsJsonArray().get(0).getAsJsonObject();
                } else if (tmpelem.isJsonObject())
                    tmp = tmpelem.getAsJsonObject();
                else if (tmpelem.isJsonNull())
                    return null;
            } else {
                SplitStrForKeyAndIndex analise = new SplitStrForKeyAndIndex(keys[i]);
                if (analise.getIndex() > -1) {
                    throw new PathError("unsupport " + keys[i]);
                }
                return null;

            }

        }
        if (tmp.has(keys[keys.length - 1])) {
            JsonElement result = tmp.get(keys[keys.length - 1]);
            if (result.isJsonPrimitive()) return result.getAsString();
            else if (result.isJsonArray()) return result.getAsJsonArray();
            else if (result.isJsonNull()) return null;
            else if (result.isJsonObject()) return result.getAsJsonObject();
            else return result.toString();
        }
        return null;
    }

    public static String getStringValueFromMoreSources(JsonObject visit, String[] source) {
        String result = null;
        for (String key : source) {
            result = JsonAttrUtil.getStringValue(key, visit);
            if (!StringUtil.isEmptyStr(result)) {
                break;
            }
        }
        if (StringUtil.isEmptyStr(result)) return null;
        return result;
    }

    public static String getStringValueMutilSource(String[] sources, JsonObject json) {
        if (json == null) return null;
        if (sources == null || sources.length == 0) return null;
        for (String source : sources) {
            String value = getStringValue(source, json);
            if (!StringUtil.isEmptyStr(value))
                return value;
        }
        return null;
    }

    public static JsonArray getJsonArrayValueMutilSource(String[] sources, JsonObject json) {
        if (json == null) return null;
        if (sources == null || sources.length == 0) return null;
        for (String source : sources) {
            JsonArray value = getJsonArrayValue(source, json);
            if (value != null)
                return value;
        }
        return null;
    }

    //所有数据填充
    public static LinkedList<JsonElement> getJsonArrayAllValue(String key, JsonObject data) {
        return getJsonArrayAllValue(key, data, false);
    }

    public static LinkedList<JsonElement> getJsonArrayAllValue(String key, JsonObject data, boolean addNULL) {
        LinkedList<JsonElement> tmplist = new LinkedList<>();
        int find = -1;
        LinkedList<JsonElement> resultlist = new LinkedList<>();
        resultlist.add(data);
        LinkedList<JsonElement> swap = null;
        String head = null;
        while (!StringUtil.isEmptyStr(key)) {
            find = key.indexOf('.');
            if (find > 0) {
                head = key.substring(0, find);
                key = key.substring(find + 1);
            } else {
                head = key;
                key = null;
            }
            for (JsonElement element : resultlist) {
                if (element.isJsonObject()) {
                    JsonObject tmp = element.getAsJsonObject();
                    if (tmp.has(head)) {
                        JsonElement tmpelement = tmp.get(head);
                        if (tmpelement.isJsonArray())
                            arrayToCollection(tmplist, tmpelement.getAsJsonArray());
                        else
                            tmplist.add(tmpelement);
                    } else {
                        if (addNULL) tmplist.add(null);
                    }
                }
            }
            swap = tmplist;
            tmplist = resultlist;
            resultlist = swap;
            tmplist.clear();
        }

        return resultlist;

    }


    public static void arrayToCollection(Collection collection, JsonArray array) {
        for (JsonElement element : array) {
            collection.add(element);
        }
    }

    public static boolean isNotEmptyArray(JsonObject json, String key) {
        if (json.has(key)) {
            JsonElement element = json.get(key);
            if (element.isJsonArray() && element.getAsJsonArray().size() > 0)
                return true;
        }
        return false;

    }

    public static boolean isNotEmptyKey(JsonObject json, String key) {
        if (json.has(key)) {
            JsonElement element = json.get(key);
            if (element.isJsonArray() && element.getAsJsonArray().size() > 0)
                return true;
            else if (element.isJsonPrimitive() && !StringUtil.isEmptyStr(element.getAsString()))
                return true;
            else if (element.isJsonObject() && element.getAsJsonObject().entrySet().size() > 0)
                return true;
        }
        return false;
    }

    public static LinkedList<JsonElement> getListFromJsonArray(String key, JsonObject json) {
        try {
            return JsonAttrUtil.fromJson(JsonAttrUtil.getJsonArrayValue(key, json), new TypeToken<LinkedList<JsonElement>>() {
            }.getType());
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    public static boolean arrayContain(JsonArray array, String value) {
        if (StringUtil.isEmptyStr(value)) return false;
        if (array == null || array.size() == 0) return false;
        for (JsonElement elem : array) {
            if (value.equals(elem.getAsString())) return true;
        }
        return false;
    }

    public static JsonParser getJsonpParse() {
        return jsonParser;
    }

    public static JsonElement toJsonElement(InputStream content) {
        InputStreamReader inputStream = null;
        JsonElement result = null;
        JsonReader reader = null;
        try {
            inputStream = new InputStreamReader(content, "utf-8");
            reader = new JsonReader(inputStream);
            result = jsonParser.parse(reader);
        } catch (Exception e) {
            logger.error("", e);
            result = null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        return result;
    }

    public static JsonElement toJsonElement(String s) {
        try {
            return jsonParser.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String readPathForStrValue(String path, JsonObject json) {
        JsonElement value = readPath(path, json);
        if (value == null || !value.isJsonPrimitive()) return null;
        return value.getAsString();
    }

    public static JsonElement readPath(String path, JsonObject json) {
        String[] paths = path.split("\\.");
        JsonElement tmp = json;
        try {
            for (String pathItem : paths) {
                int index = -1;
                int findIndex = pathItem.indexOf("[");
                String key = pathItem;
                if (findIndex > 0) {
                    try {
                        if (!pathItem.endsWith("]")) {
                            logger.error("配置错误 " + pathItem);
                            return null;
                        }
                        key = pathItem.substring(0, findIndex);
                        index = Integer.parseInt(pathItem.substring(findIndex + 1, pathItem.length() - 1));
                    } catch (Exception e) {
                        logger.error("配置错误 " + pathItem);
                        return null;
                    }
                }
                if (!tmp.isJsonObject()) {
                    logger.error("read path error : not json " + path);
                    return null;
                }
                JsonObject tmpjson = tmp.getAsJsonObject();
                if (tmpjson.has(key)) {
                    tmp = tmpjson.get(key);
                    if (index >= 0) {
                        if (!tmp.isJsonArray()) {
                            logger.error("read path error : not array " + path);
                            return null;
                        }
                        try {
                            tmp = tmp.getAsJsonArray().get(index);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                } else
                    return null;

            }
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
        return tmp;
    }

    public static JsonElement deepCopy(JsonElement json) {
        return JsonAttrUtil.toJsonElement(gson.toJson(json));
    }

    public static LinkedList<FindIndexModel<JsonElement>> getAllValueWithAnalisePath(String path, JsonElement data) {
        return getAllValueWithAnalisePath(path, data, null);
    }

    public static LinkedList<JsonElement> getLinklistValueWithAnalisePath(String path, JsonElement data) {
        LinkedList<JsonElement> result = new LinkedList<>();
        LinkedList<FindIndexModel<JsonElement>> allvalue = getAllValueWithAnalisePath(path, data, null);
        if (allvalue == null) {
            return null;
        }
        for (FindIndexModel<JsonElement> item : allvalue) {
            JsonElement value = item.getValue();
            result.add(value);
        }
        return result;
    }

    public static LinkedList<FindIndexModel<JsonElement>> getAllValueWithAnalisePath(String path, JsonElement data, String key) {
        FindIndexModel<JsonElement> begin = new FindIndexModel<>();
        begin.setValue(data);
        begin.setLeaf(true);
        begin.setKey(key);
        return getAllValueWithAnalisePath(path, begin);
    }

    public static LinkedList<FindIndexModel<JsonElement>> getAllValueWithAnalisePathByHeadPath(String path, JsonElement data, AbstractPath headPath) {
        FindIndexModel<JsonElement> begin = new FindIndexModel<>();
        begin.setValue(data);
        begin.setLeaf(true);
        begin.setKey(headPath.getKey());
        begin.setIndex(headPath.getIndex());
        return getAllValueWithAnalisePath(path, begin);
    }

    /**
     * 从FindIndexModel<JsonElement> 拿指定路径的数据
     */
    public static <T extends AbstractPath> LinkedList<FindIndexModel<JsonElement>> getAllValueWithAnalisePath(LinkedList<T> pathList, FindIndexModel<JsonElement> begin) {
        String path = AbstractPath.getPath(pathList);
        return getAllValueWithAnalisePath(path, begin);
    }

    public static <T extends AbstractPath> LinkedList<FindIndexModel<JsonElement>> getAllValueWithAnalisePath(LinkedList<T> pathList, LinkedList<FindIndexModel<JsonElement>> begins) {
        LinkedList<FindIndexModel<JsonElement>> result = new LinkedList<>();
        String path = AbstractPath.getPath(pathList);
        for (FindIndexModel<JsonElement> begin : begins) {
            LinkedList<FindIndexModel<JsonElement>> tmp = getAllValueWithAnalisePath(path, begin);
            if (tmp != null) {
                result.addAll(tmp);
            }
        }
        if (result.size() == 0) return null;
        return result;
    }

    public static LinkedList<FindIndexModel<JsonElement>> getAllValueWithAnalisePath(String path, FindIndexModel<JsonElement> begin) {
        LinkedList<FindIndexModel<JsonElement>> tmplist = new LinkedList<>();
        if (begin == null) return tmplist;
        int find = -1;
        LinkedList<FindIndexModel<JsonElement>> resultlist = new LinkedList<>();
        resultlist.add(begin);
        LinkedList<FindIndexModel<JsonElement>> swap = null;
        String head = null;
        while (!StringUtil.isEmptyStr(path)) {
            find = path.indexOf('.');
            if (find > 0) {
                head = path.substring(0, find);
                path = path.substring(find + 1);
            } else {
                head = path;
                path = null;
            }
            for (FindIndexModel<JsonElement> element : resultlist) {
                if (element.getValue().isJsonObject()) {
                    head = oneValueOperate(tmplist, head, element);
                } else if (element.getValue().isJsonArray()) {
                    int i = 0;
                    for (JsonElement item : element.getValue().getAsJsonArray()) {
                        FindIndexModel<JsonElement> entity = new FindIndexModel<>();
                        entity.setIndex(i);
                        entity.setKey(element.getKey());
                        JsonObject json = new JsonObject();
                        json.add(element.getKey(), item);
                        entity.setValue(json);
                        entity.setP(element);
                        head = oneValueOperate(tmplist, head, entity);
                        i++;
                    }
                }
            }
            resultlist.remove(begin);
            swap = tmplist;
            tmplist = resultlist;
            resultlist = swap;
            tmplist.clear();
        }

        return resultlist;
    }

    private static String oneValueOperate(LinkedList<FindIndexModel<JsonElement>> tmplist, String head, FindIndexModel<JsonElement> element) {
        JsonObject tmp = null;
        tmp = element.getValue().getAsJsonObject();
        SplitStrForKeyAndIndex splitStrForKeyAndIndex = new SplitStrForKeyAndIndex(head);
        head = splitStrForKeyAndIndex.getKey();
        if (tmp.has(head)) {
            JsonElement tmpelement = tmp.get(head);
            if (tmpelement.isJsonArray()) {
                int tmpIndex = splitStrForKeyAndIndex.getIndex();
                if (tmpIndex < 0) {
                    int index = 0;
                    for (JsonElement elementtmp : tmpelement.getAsJsonArray()) {
                        FindIndexModel<JsonElement> mode = new FindIndexModel<>();
                        mode.setValue(elementtmp);
                        mode.setKey(head);
                        tmplist.add(mode);
                        mode.setIndex(index);
                        mode.setP(element);
                        index++;
                    }
                } else {
                    JsonElement elementtmp = tmpelement.getAsJsonArray().get(tmpIndex);
                    FindIndexModel<JsonElement> mode = new FindIndexModel<>();
                    mode.setValue(elementtmp);
                    mode.setKey(head);
                    tmplist.add(mode);
                    mode.setIndex(tmpIndex);
                    mode.setP(element);
                }
            } else {
                FindIndexModel<JsonElement> mode = new FindIndexModel<>();
                mode.setValue(tmpelement);
                mode.setLeaf(true);
                mode.setP(element);
                mode.setKey(head);
                tmplist.add(mode);

            }
        }
        return head;
    }

    public static int getMaxValue(LinkedList<JsonElement> data) {
        double max = 0;
        int maxIndex = -1;
        int index = 0;
        for (JsonElement element : data) {
            try {
                double tmp = Double.valueOf(element.getAsString());
                if (maxIndex < 0) {
                    max = tmp;
                    maxIndex = index;
                } else if (max < tmp) {
                    max = tmp;
                    maxIndex = index;
                }
            } catch (Exception e) {
            }
            index++;
        }
        return maxIndex;
    }

    public static Gson getGson() {
        return gson;

    }

    public static JsonElement standardization(JsonElement setvalue, LinkedList<String> enums) {
        sortForStandard(enums);
        String tmpvalue = setvalue.getAsString();
        for (String eachItem : enums) {
            if (eachItem.contains(tmpvalue) || tmpvalue.contains(eachItem)) {
                tmpvalue = eachItem;
                break;
            }
        }
        setvalue = new JsonPrimitive(tmpvalue);
        return setvalue;
    }

    public static void sortForStandard(LinkedList<String> enums) {
        Collections.sort(enums, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() == o2.length()) return 0;
                if (o1.length() > o2.length()) return -1;
                return 1;
            }
        });
    }

    public static LinkedList<String> jsonArray2ListStr(JsonArray array) {
        return fromJson(array, new TypeToken<LinkedList<String>>() {
        }.getType());
    }

    public static LinkedList<String> listJsonElement2ListStr(LinkedList<JsonElement> array) {
        LinkedList<String> result = new LinkedList<>();
        for (JsonElement item : array) {
            result.add(item.getAsString());
        }
        return result;
    }

    public static LinkedList<FindIndexModel<JsonElement>> compare(LinkedList<FindIndexModel<JsonElement>> list, String target, boolean needEqual, boolean needless) {
        return compare(list, target, needEqual, needless, false);
    }

    public static LinkedList<FindIndexModel<JsonElement>> compare(LinkedList<FindIndexModel<JsonElement>> list, String target, boolean needEqual, boolean needless, boolean onlyEqual) {
        LinkedList<FindIndexModel<JsonElement>> result = new LinkedList<>();
        for (FindIndexModel<JsonElement> item : list) {
            try {
                if (StringUtil.isEmptyStr(item.getValue().getAsString())) continue;
                int compare = item.getValue().getAsString().compareTo(target);
                if (compare == 0) {
                    if (needEqual) result.add(item);
                } else if (!onlyEqual) {
                    if (compare < 0) {
                        if (needless) result.add(item);
                    } else {
                        if (!needless) result.add(item);
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return result;
    }

    /**
     * 合并json
     */
    public static JsonObject addAllProp(JsonObject result, JsonObject copyFrom) {
        if (isEmptyJsonElement(result)) return copyFrom;
        if (isEmptyJsonElement(copyFrom)) return result;
        for (Map.Entry<String, JsonElement> item : copyFrom.entrySet()) {
            result.add(item.getKey(), item.getValue());
        }
        return result;
    }

    public static JsonArray addAllProp(JsonArray result, JsonArray copyFrom) {
        if (isEmptyJsonElement(result)) return copyFrom;
        if (isEmptyJsonElement(copyFrom)) return result;
        LinkedList<JsonElement> all = new LinkedList<>();
        for (int i = 0; i < result.size(); i++) {
            if (copyFrom.size() == i) break;
            all.add(addAllProp(result.get(i).getAsJsonObject(), copyFrom.get(i).getAsJsonObject()));
        }
        if (result.size() < copyFrom.size()) {

            for (int i = result.size(); i < copyFrom.size(); i++)
                all.add(copyFrom.get(i));
        }
        return toJsonTree(all).getAsJsonArray();
    }

    public static <T> T deepCopy(Object config, TypeToken<T> type) {
        return fromJson(toJsonStr(config), type.getType());
    }

    public static boolean getBooleanValue(String path, JsonObject paramObj) {
        try {
            return Boolean.valueOf(JsonAttrUtil.getStringValue(path, paramObj));
        } catch (Exception e) {
            return false;
        }
    }

    public static void makeEmpty(JsonArray array) {
        if (array == null) return;
        while (array.size() > 0) array.remove(0);
    }

    public static void mergeJson(JsonObject target, JsonObject source) {
        if (source == null) return;
        if (target == null) return;
        for (Map.Entry<String, JsonElement> item : source.entrySet()) {
            target.add(item.getKey(), item.getValue());
        }
    }


    /**
     * 路径merge
     **/
    public static <T1 extends AbstractPath, T2 extends AbstractPath> LinkedList<T2> mergePathByOne(LinkedList<T1> base, LinkedList<T2> path) {
        Iterator<T2> tmp = path.iterator();
        Iterator<T1> baseIter = base.iterator();
        boolean isEqual = true;
        while (baseIter.hasNext() && tmp.hasNext()) {
            T1 baseNext = baseIter.next();
            T2 tmpNext = tmp.next();
            if (baseNext.getKey().equals(tmpNext)) {
                if (baseNext.getIndex() != tmpNext.getIndex()) {
                    isEqual = false;
                    break;
                }
            }
        }
        if (isEqual) return path;
        return null;
    }
    public static <T1 extends AbstractPath, T2 extends AbstractPath> LinkedList<LinkedList<T2>> mergePath(LinkedList<T1> base, LinkedList<LinkedList<T2>> paths) {
        LinkedList<LinkedList<T2>> result = new LinkedList<>();
        for (LinkedList<T2> path : paths) {
            LinkedList<T2> tmp = mergePathByOne(base, path);
            if (tmp != null && tmp.size() > 0) result.add(tmp);
        }
        if (result.size() == 0) return null;
        return result;
    }

}