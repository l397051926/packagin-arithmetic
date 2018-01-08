package com.gennlife.packagingservice.arithmetic.express.factorys;


import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.exceptions.ConfigError;
import com.gennlife.packagingservice.arithmetic.express.interfaces.ConditionOperatorInterface;
import com.gennlife.packagingservice.arithmetic.express.interfaces.InstructionOperatorInterface;
import com.gennlife.packagingservice.arithmetic.express.interfaces.SupportNotOperatorInterface;
import com.gennlife.packagingservice.arithmetic.express.operator.retrieve.*;
import com.gennlife.packagingservice.arithmetic.pretreatment.enums.InstructionOperatorEnum;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Chenjinfeng on 2017/6/22.
 */
public class ConditionOperatorFactory {
    public static final String CUSTOM_TYPE_KEY = "customType";
    private static final Logger logger = LoggerFactory.getLogger(ConditionOperatorFactory.class);

    public static ConditionOperatorInterface getConditionOperator(String key) {
        // if (StringUtil.isEmptyStr(key)) return new ConditionNotNull();
        // if (key.equalsIgnoreCase("equal")) return new ConditionEualsIgnoreCase();
        // if (key.equalsIgnoreCase("contain")) return new ConditionContainIgnoreCase();
        //if (key.equalsIgnoreCase("regex")) return new ConditionRegex();
        //if (key.equalsIgnoreCase("any")) return new ConditionAnyOperator();
        //if (key.equalsIgnoreCase("jsonhasvalue")) return new JsonHasValueOperator();
        //if (key.equalsIgnoreCase("simpleNumber")) return new SimpleNumberOperator();

        //if (key.equalsIgnoreCase("dateHmsCompare")) return new DateHmsCompareOperator();
        //if (key.equalsIgnoreCase("isnull")) return new NullCompareOperator();
        return null;
    }

    public static InstructionOperatorEnum check(String key) {
        if (StringUtil.isEmptyStr(key)) return InstructionOperatorEnum.ISNULL;
        if (key.startsWith("!")) key = key.substring(1);
        InstructionOperatorEnum result = null;
        key = key.toUpperCase();
        try {
            result = InstructionOperatorEnum.valueOf(key);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * demo for custom:
     * <code>
     * JsonObject config = new JsonObject();
     * config.addProperty(ConditionOperatorFactory.CUSTOM_TYPE_KEY, "custom");
     * ConditionCheck conditionCheck = new ConditionCheck(null);
     * Map<String, String> map = new HashMap<>();
     * conditionCheck.setCustomClassMap(map);
     * map.put("custom", "com.gennlife.packagingservice.arithmetic.express.operator.retrieve.NullCompareOperator{@link NullCompareOperator}");
     * InstructionOperatorInterface result = ConditionOperatorFactory.getInstructionOperator(InstructionOperatorEnum.CUSTOM.name(), config, conditionCheck);
     * </code>
     *
     * @throws ConfigError
     */
    public static InstructionOperatorInterface getInstructionOperator(String key, JsonObject config, ConditionCheck context) {
        InstructionOperatorEnum matchEnum = check(key);
        InstructionOperatorInterface result = null;
        boolean isNot = false;
        if (key.startsWith("!")) {
            isNot = true;
            key = key.substring(1);
        }
        if (matchEnum == InstructionOperatorEnum.CUSTOM) {
            String type = JsonAttrUtil.getStringValue(CUSTOM_TYPE_KEY, config);
            if (StringUtil.isEmptyStr(type))
                throw new ConfigError(CUSTOM_TYPE_KEY + " is null");
            Map<String, String> customClassMap = context.getCustomClassMap();
            if (customClassMap == null)
                throw new ConfigError("custom class map is null");
            if (customClassMap.containsKey(type)) {
                try {
                    result = (InstructionOperatorInterface) Class.forName(customClassMap.get(type)).newInstance();
                } catch (Exception e) {
                    throw new ConfigError("error : " + e.getMessage());
                }
            } else {
                throw new ConfigError("custom class map is not contain " + type);
            }
        } else if (matchEnum == InstructionOperatorEnum.EQUAL) result = new ConditionEualsIgnoreCase();
        else if (matchEnum == InstructionOperatorEnum.CONTAIN) result = new ConditionContainIgnoreCase();
        else if (matchEnum == InstructionOperatorEnum.REGEX) result = new ConditionRegex();
        else if (matchEnum == InstructionOperatorEnum.ISNULL) result = new NullCompareOperator();
            //包含全部
        else if (matchEnum == InstructionOperatorEnum.CONTAINALL) {
            ConditionContainIgnoreCase op = new ConditionContainIgnoreCase();
            op.setMatchAll(true);
            result = op;
        } else if (matchEnum == InstructionOperatorEnum.DATEHMSCOMPARE) {
            result = new DateHmsCompareOperator();
        } else if (matchEnum == InstructionOperatorEnum.SIMPLENUMBER) {
            result = new SimpleNumberOperator();
        } else if (matchEnum == InstructionOperatorEnum.SIMPLEDATE) {
            result = new SimpleDateOperator();
        }
        if (result != null && isNot) {
            if (result instanceof SupportNotOperatorInterface)
                ((SupportNotOperatorInterface) result).setNot(isNot);
            else {
                throw new ConfigError("not support  'not' operator for " + key);
            }
        }
        return result;
    }

}
