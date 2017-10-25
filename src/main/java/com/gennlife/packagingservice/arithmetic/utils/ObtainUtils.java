package com.gennlife.packagingservice.arithmetic.utils;

import com.gennlife.packagingservice.arithmetic.express.enitity.FindIndexModel;
import com.gennlife.packagingservice.arithmetic.express.enitity.NumberResultEntity;
import com.gennlife.packagingservice.arithmetic.express.enums.ArrayOpEnum;
import com.gennlife.packagingservice.arithmetic.express.enums.NumberOpEnum;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chenjinfeng on 2017/10/20.
 */
public class ObtainUtils {
    public static final Logger logger = LoggerFactory.getLogger(ObtainUtils.class);

    public static List<FindIndexModel<JsonElement>> obtainTarget(LinkedList<FindIndexModel<JsonElement>> list, ArrayOpEnum type, Integer... index) {
        if (list == null || list.size() == 0) return list;
        if (type == ArrayOpEnum.ALL)
            return list;
        LinkedList<FindIndexModel<JsonElement>> result = new LinkedList<>();
        if (type == ArrayOpEnum.LAST) {
            result.add(list.getLast());
            return result;
        }
        if (type == ArrayOpEnum.FIRST) {
            result.add(list.getFirst());
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
            int need = index[0] - 1;
            if (need >= list.size()) return null;
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
                if (type == ArrayOpEnum.AFTERLIST) {
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
        if (item instanceof Double) return (Double) item;
        else if (item instanceof JsonElement) {
            try {
                return Double.valueOf(((JsonElement) item).getAsString());
            } catch (Exception e) {
                {
                    logger.error("", e);
                    return null;
                }
            }
        } else if (item instanceof String) {
            return Double.valueOf((String) item);
        }
        logger.error(" unknown type change to double " + item.getClass());
        return null;
    }

}
