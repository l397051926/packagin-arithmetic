package com.gennlife.packagingservice.arithmetic.pretreatment.utils;

import com.gennlife.packagingservice.arithmetic.utils.DateUtil;
import com.gennlife.packagingservice.arithmetic.pretreatment.enums.OperandOperationEnums;
import com.google.gson.JsonElement;

import static com.gennlife.packagingservice.arithmetic.express.enitity.NumberResultEntity.formatDouble;


/**
 * Created by Chenjinfeng on 2017/10/21.
 */
public class PreTreatmentUtils {
    public static String operationValue(JsonElement value, OperandOperationEnums type, JsonElement ... arg) {
        if (OperandOperationEnums.NUMBER_ADD == type) {
            try {
                double param = Double.valueOf(arg[0].getAsString());
                return String.valueOf(formatDouble(value.getAsDouble() + param));
            } catch (Exception e) {
                return null;
            }
        }
        if (OperandOperationEnums.NUMBER_SUB == type) {
            try {
                double param = Double.valueOf(arg[0].getAsString());
                return String.valueOf(formatDouble(value.getAsDouble() - param));
            } catch (Exception e) {
                return null;
            }
        }
        if (OperandOperationEnums.NUMBER_RAISE_PERCENT == type) {
            try {
                double param = Double.valueOf(arg[0].getAsString())/100;
                return String.valueOf(formatDouble(value.getAsDouble() * (1 + param)));
            } catch (Exception e) {
                return null;
            }
        }
        if (OperandOperationEnums.NUMBER_FAIL_PERCENT == type) {
            try {
                double param = Double.valueOf(arg[0].getAsString())/100;
                return String.valueOf(formatDouble(value.getAsDouble() * (1 - param)));
            } catch (Exception e) {
                return null;
            }
        }

        if (OperandOperationEnums.TIME_ADD_DAY == type) {
            try {
                int param = Integer.valueOf(arg[0].getAsString());
                String result = DateUtil.getDateByOperateDay(value.getAsString(), param, true);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        if (OperandOperationEnums.TIME_SUB_DAY == type) {
            try {
                int param = Integer.valueOf(arg[0].getAsString());
                String result = DateUtil.getDateByOperateDay(value.getAsString(), param, false);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
