package com.gennlife.packagingservice.arithmetic.utils;

import com.gennlife.packagingservice.arithmetic.assign.Classifier;
import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractPath;
import com.gennlife.packagingservice.arithmetic.express.enitity.FindIndexModel;
import com.gennlife.packagingservice.arithmetic.express.enitity.NumberResultEntity;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathItem;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.express.enums.ArrayOpEnum;
import com.gennlife.packagingservice.arithmetic.express.enums.NumberOpEnum;
import com.gennlife.packagingservice.arithmetic.express.format.NumberFormatArrayItem;
import com.gennlife.packagingservice.arithmetic.express.format.StringDateFormatArrayItem;
import com.gennlife.packagingservice.arithmetic.express.interfaces.FormatArrayItem;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Chenjinfeng on 2017/10/20.
 */
public class ObtainUtils {
    public static final Logger logger = LoggerFactory.getLogger(ObtainUtils.class);

    public static List<FindIndexModel<JsonElement>> obtainList(List<FindIndexModel<JsonElement>> list, ArrayOpEnum type, Integer... index) {
        if (list == null || list.size() == 0) return list;
        if (type == ArrayOpEnum.ALL)
            return list;
        LinkedList<FindIndexModel<JsonElement>> result = new LinkedList<>();
        if (type == ArrayOpEnum.LAST) {
            if (list instanceof LinkedList) {
                result.add(((LinkedList<FindIndexModel<JsonElement>>) list).getLast());
            } else
                result.add(list.get(list.size() - 1));
            return result;
        }
        if (type == ArrayOpEnum.FIRST) {
            result.add(list.get(0));
            return result;
        }
        LinkedList<Integer> integers = new LinkedList<>();
        if (index != null) {
            for (int i : index) {
                if (i > 0) integers.add(i);
            }
        }
        if (integers.size() == 0) {
            logger.error("not find param");
            return null;
        }
        if (type == ArrayOpEnum.INDEX || type == ArrayOpEnum.REVERSEINDEX) {
            int need = index[0];
            if (need > list.size()) return null;
            if (type == ArrayOpEnum.INDEX)
                result.add(list.get(need - 1));
            else {
                result.add(list.get(list.size() - 1 - need));
            }
            return result;
        }
        if (type == ArrayOpEnum.AFTERLIST || type == ArrayOpEnum.PREVIOUSLIST) {
            int length = list.size();
            int need = index[0];
            if (length <= need) {
                result.addAll(list);
                return result;
            } else {
                if (type == ArrayOpEnum.PREVIOUSLIST) {
                    return list.subList(0, need);
                } else {
                    return list.subList(list.size() - need, list.size());

                }
            }

        }
        return null;

    }

    public static <T> List<FindIndexModel<T>> formatForDoubleByList(List<FindIndexModel<T>> list) {
        if (list == null || list.size() == 0) return null;
        LinkedList<FindIndexModel<T>> countList = new LinkedList<>();
        for (FindIndexModel<T> item : list) {
            T value = item.getValue();
            Double d = formatForDouble(value);
            if (d != null) countList.add(item);
        }
        return countList;
    }

    public static <T> LinkedList<FindIndexModel<T>> sort(LinkedList<FindIndexModel<T>> list, final boolean asc, FormatArrayItem formatArrayItem) {
        if (list == null || list.size() == 0) return list;
        if (formatArrayItem == null) return list;
        Set<FindIndexModel<T>> sort = new TreeSet<>(new Comparator<FindIndexModel<T>>() {
            @Override
            public int compare(FindIndexModel<T> o1, FindIndexModel<T> o2) {
                Comparable o1Vlaue = formatArrayItem.format(o1.getValue());
                Comparable o2Vlaue = formatArrayItem.format(o2.getValue());
                if (asc) return o1Vlaue.compareTo(o2Vlaue);
                else return o2Vlaue.compareTo(o1Vlaue);
            }
        });
        List<FindIndexModel<T>> empty = new LinkedList<>();
        for (FindIndexModel<T> item : list) {
            T value = item.getValue();
            Comparable d = formatArrayItem.format(value);
            if (d != null) sort.add(item);
            else empty.add(item);
        }
        LinkedList<FindIndexModel<T>> result = new LinkedList<>();
        result.addAll(sort);
        result.addAll(empty);
        return result;
    }

    public static List<FindIndexModel<JsonElement>> formatForDateByList(List<FindIndexModel<JsonElement>> list) {
        if (list == null || list.size() == 0) return null;
        LinkedList<FindIndexModel<JsonElement>> countList = new LinkedList<>();
        for (FindIndexModel<JsonElement> item : list) {
            JsonElement value = item.getValue();
            if (value == null || !value.isJsonPrimitive()) continue;
            Date d = DateUtil.getDate(value.getAsString());
            if (d != null) countList.add(item);
        }
        return countList;
    }

    public static List<FindIndexModel<JsonElement>> formatForNotNullByList(List<FindIndexModel<JsonElement>> list) {
        if (list == null || list.size() == 0) return null;
        LinkedList<FindIndexModel<JsonElement>> countList = new LinkedList<>();
        for (FindIndexModel<JsonElement> item : list) {
            JsonElement value = item.getValue();
            if (JsonAttrUtil.isEmptyJsonElement(value)) continue;
            countList.add(item);
        }
        return countList;
    }


    public static <T> NumberResultEntity obtainNum(List<FindIndexModel<T>> source, NumberOpEnum type) {
        if (source == null || source.size() == 0) return null;
        LinkedList<Double> doubles = new LinkedList<>();
        for (FindIndexModel<T> itemModel : source) {
            T item = itemModel.getValue();
            Double doubletmp = formatForDouble(item);
            if (doubletmp != null) {
                doubles.add(doubletmp);
            } else
                return null;
        }
        if (doubles == null || doubles.size() == 0) return null;
        NumberResultEntity numberResultEntity = new NumberResultEntity();
        if (type == NumberOpEnum.AVG) {
            double avg = getAvg(doubles);
            numberResultEntity.setList(source);
            numberResultEntity.setValue(avg);
            return numberResultEntity;
        }
        if (type == NumberOpEnum.SUM) {
            double sum = getSum(doubles);
            numberResultEntity.setList(source);
            numberResultEntity.setValue(sum);
            return numberResultEntity;
        }
        if (type == NumberOpEnum.MAX) {
            int index = getMaxIndex(doubles);
            numberResultEntity.setList(source.subList(index, index + 1));
            numberResultEntity.setValue(doubles.get(index));
            return numberResultEntity;
        }
        if (type == NumberOpEnum.MIN) {
            int index = getMinIndex(doubles);
            numberResultEntity.setList(source.subList(index, index + 1));
            numberResultEntity.setValue(doubles.get(index));
            return numberResultEntity;
        }
        if (type == NumberOpEnum.MODE_MAX) {
            HashMap<Double, LinkedList<FindIndexModel<T>>> map = new HashMap<>();
            Iterator<Double> valueIter = doubles.iterator();
            Iterator<FindIndexModel<T>> sourceIter = source.iterator();
            while (valueIter.hasNext()) {
                Double valueItem = valueIter.next();
                if (!map.containsKey(valueItem)) map.put(valueItem, new LinkedList<FindIndexModel<T>>());
                map.get(valueItem).add(sourceIter.next());
            }
            Double maxValue = null;
            int maxSize = 0;
            LinkedList<FindIndexModel<T>> matchList = null;
            for (Map.Entry<Double, LinkedList<FindIndexModel<T>>> entry : map.entrySet()) {
                Double mapKey = entry.getKey();
                LinkedList<FindIndexModel<T>> mapValue = entry.getValue();
                if (mapValue.size() > maxSize) {
                    maxValue = mapKey;
                    maxSize = mapValue.size();
                    matchList = mapValue;
                }
            }
            numberResultEntity.setValue(maxValue);
            numberResultEntity.setList(matchList);
            return numberResultEntity;
        }
        return null;
    }

    private static double getSum(List<Double> doubles) {
        double sum = 0;
        for (Double value : doubles) {
            sum += value;
        }
        return sum;
    }

    private static double getAvg(List<Double> doubles) {
        double sum = getSum(doubles);
        return sum / doubles.size();
    }

    private static int getMaxIndex(List<Double> doubles) {
        int index = -1;
        Double max = null;
        int i = 0;
        for (Double value : doubles) {
            if (max == null) {
                max = value;
                index = i;
            } else if (value > max) {
                max = value;
                index = i;
            }
            i++;
        }
        return index;
    }

    private static int getMinIndex(List<Double> doubles) {
        int index = -1;
        Double min = null;
        int i = 0;
        for (Double value : doubles) {
            if (min == null) {
                min = value;
                index = i;
            } else if (value < min) {
                min = value;
                index = i;
            }
            i++;
        }
        return index;
    }

    public static <T> Double formatForDouble(T item) {
        return NumberFormatArrayItem.INSTANSE.format(item);
    }

    private static final Comparator<LinkedList<PathItem>> COMPARATOR_PATHITEM = new Comparator<LinkedList<PathItem>>() {
        @Override
        public int compare(LinkedList<PathItem> o1, LinkedList<PathItem> o2) {
            return AbstractPath.getPath(o1).compareTo(AbstractPath.getPath(o2));
        }
    };

    public static LinkedList<FindIndexModel<JsonElement>> getSortResultByAscDate(LinkedList<FindIndexModel<JsonElement>> patient, PathNode find, String sortKey, String countPath) {
        LinkedList<LinkedList<PathItem>> emptyPathItems = new LinkedList<>();
        LinkedList<LinkedList<PathItem>> pathItems = PathNode.getPathItem(find, sortKey);
        boolean isInTheSameGroup = isInTheSameGroup(sortKey, countPath);
        LinkedList<FindIndexModel<JsonElement>> sortList = new LinkedList<>();
        LinkedList<FindIndexModel<JsonElement>> resultList = new LinkedList<>();
        for (LinkedList<PathItem> pathitem : pathItems) {
            LinkedList<FindIndexModel<JsonElement>> tmp = JsonAttrUtil.getAllValueWithAnalisePath(AbstractPath.getPath(pathitem, sortKey), patient);
            if (tmp == null || tmp.size() == 0) {
                emptyPathItems.add(pathitem);
            } else
                sortList.addAll(tmp);
        }
        sortList = sort(sortList, true, StringDateFormatArrayItem.INSTACE);
        if (sortList == null) sortList = new LinkedList<>();
        if (isInTheSameGroup) {
            for (FindIndexModel<JsonElement> sortItem : sortList) {
                LinkedList<PathItem> findItem = sortItem.getPathItem();
                LinkedList<FindIndexModel<JsonElement>> tmp = JsonAttrUtil.getAllValueWithAnalisePath(AbstractPath.getPath(findItem, countPath), patient);
                resultList.addAll(tmp);
            }
            addEmpty(patient, countPath, emptyPathItems, resultList);
            return resultList;
        }
        TreeSet<LinkedList<PathItem>> countPathItemSet = new TreeSet<>(COMPARATOR_PATHITEM);
        countPathItemSet.addAll(PathNode.getPathItem(find, countPath));
        // add sort value
        for (FindIndexModel<JsonElement> sortItem : sortList) {
            LinkedList<PathItem> findItem = sortItem.getPathItem();
            LinkedList<FindIndexModel<JsonElement>> tmp = JsonAttrUtil.getAllValueWithAnalisePath(AbstractPath.getPath(findItem, countPath), patient);
            if (tmp != null) {
                for (FindIndexModel<JsonElement> tmpJson : tmp) {
                    if (countPathItemSet.contains(tmpJson.getPathItem())) {
                        countPathItemSet.remove(tmpJson.getPathItem());
                        if (!hasContainPathItem(resultList, tmpJson)) {
                            resultList.add(tmpJson);
                        }
                    }
                }
            }
        }
        //add empty list
        addEmpty(patient, countPath, emptyPathItems, resultList);
        return resultList;
    }

    private static void addEmpty(LinkedList<FindIndexModel<JsonElement>> patient, String countPath, LinkedList<LinkedList<PathItem>> emptyPathItems, LinkedList<FindIndexModel<JsonElement>> resultList) {
        for (LinkedList<PathItem> pathitem : emptyPathItems) {
            LinkedList<FindIndexModel<JsonElement>> tmp = JsonAttrUtil.getAllValueWithAnalisePath(AbstractPath.getPath(pathitem, countPath), patient);
            if (tmp != null) resultList.addAll(tmp);
        }
    }

    public static LinkedList<FindIndexModel<JsonElement>> getSortResultByAscDate(JsonObject patient, PathNode find, String sortKey, String countPath) {
        LinkedList<JsonElement> list = new LinkedList<>();
        list.add(patient);
        return getSortResultByAscDate(JsonAttrUtil.exchangeForFindIndexModel(list), find, sortKey, countPath);
    }

    public static boolean isInTheSameGroup(String sortKey, String countPath) {
        boolean isInTheSameGroup = false;
        try {
            isInTheSameGroup = sortKey.substring(0, sortKey.lastIndexOf('.')).equals(countPath.substring(0, countPath.lastIndexOf('.')));
        } catch (Exception e) {
            isInTheSameGroup = false;
        }
        return isInTheSameGroup;
    }

    public static boolean hasContainPathItem(List<FindIndexModel<JsonElement>> list, FindIndexModel<JsonElement> item) {
        if (list == null || list.isEmpty()) return false;
        for (FindIndexModel<JsonElement> listItem : list) {
            if (COMPARATOR_PATHITEM.compare(listItem.getPathItem(), item.getPathItem()) == 0) return true;
        }
        return false;
    }

    public static LinkedList<FindIndexModel<JsonElement>> filterByTime(LinkedList<FindIndexModel<JsonElement>> list, String dateTime, boolean needEqual, boolean needless) {
        return JsonAttrUtil.compare(list, dateTime, needEqual, needless);
    }

    public static LinkedList<FindIndexModel<JsonElement>> filterByTime(LinkedList<FindIndexModel<JsonElement>> list, String dateTime, boolean needEqual, boolean needless, boolean onlyEqual) {
        return JsonAttrUtil.compare(list, dateTime, needEqual, needless, onlyEqual);
    }

    public static LinkedList<FindIndexModel<JsonElement>> getListByBaseFilter(FindIndexModel<JsonElement> datas, String from, JsonObject condition, String sortKey, ArrayOpEnum opEnum) {
        LinkedList<FindIndexModel<JsonElement>> list = new LinkedList<>();
        list.add(datas);
        return getListByBaseFilter(list, from, condition, sortKey, opEnum);
    }

    public static LinkedList<FindIndexModel<JsonElement>> getListByBaseFilter(LinkedList<FindIndexModel<JsonElement>> list, String from, JsonObject condition, String sortKey, ArrayOpEnum opEnum) {
        LinkedList<FindIndexModel<JsonElement>> froms = JsonAttrUtil.getAllValueWithAnalisePath(from, list);
        if (froms == null || froms.size() == 0) return null;
        PathNode pathNode = null;
        if (condition != null) {
            ConditionCheck conditionCheck = new ConditionCheck(condition);
            froms = conditionCheck.filter(froms);
            pathNode = conditionCheck.getPathItemsByPathNode(list, null);
            if (pathNode == null) return null;
        }
        if (StringUtil.isNotEmptyStr(sortKey)) {
            if (pathNode == null) pathNode = new PathNode();
            froms = ObtainUtils.getSortResultByAscDate(froms, pathNode, sortKey, from);
        }
        if (opEnum != null) {
            froms = new LinkedList<>(ObtainUtils.obtainList(froms, opEnum));
        }
        if (froms == null || froms.size() == 0) return null;
        return froms;
    }

    public static Map<String, LinkedList<FindIndexModel<JsonElement>>> classify(LinkedList<FindIndexModel<JsonElement>> sources, String countPath, Classifier classifier) {
        LinkedList<FindIndexModel<JsonElement>> dataList = JsonAttrUtil.getAllValueWithAnalisePath(countPath, sources);
        if (dataList == null || dataList.size() == 0 || classifier == null) return null;
        Map<String, LinkedList<FindIndexModel<JsonElement>>> map = new HashMap<>();
        for (FindIndexModel<JsonElement> dataItem : dataList) {
            String value = classifier.getClassifierKey(dataItem.getValue());
            if (StringUtil.isEmptyStr(value)) {
                value = "";
            }
            if (!map.containsKey(value)) map.put(value, new LinkedList<>());
            map.get(value).add(dataItem);
        }
        return map;
    }
}
