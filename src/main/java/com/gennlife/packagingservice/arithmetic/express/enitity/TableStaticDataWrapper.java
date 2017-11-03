package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractStaticDataWrapper;
import com.gennlife.packagingservice.arithmetic.express.exceptions.ConfigError;
import com.gennlife.packagingservice.arithmetic.express.exceptions.UnMatchInstanceError;
import com.gennlife.packagingservice.arithmetic.express.interfaces.SourceDataWrapperInterface;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/27.
 */
public class TableStaticDataWrapper extends AbstractStaticDataWrapper {
    private String table;
    private String id;
    private static final Logger logger = LoggerFactory.getLogger(TableStaticDataWrapper.class);

    public TableStaticDataWrapper(String tableKey, String idKey, JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        super(config, contextNode, conditionCheck);
        this.table = JsonAttrUtil.getStringValue(tableKey, config);
        if (StringUtil.isEmptyStr(table)) {
            throw new ConfigError("MapOperandDatas need tableName " + tableKey);
        }
        this.id = JsonAttrUtil.getStringValue(idKey, config);
        if (StringUtil.isEmptyStr(table)) {
            throw new ConfigError("MapOperandDatas need id " + idKey);
        }
        SourceDataWrapperInterface data = this.conditionCheck.getDataWrapper(table);
        if (data instanceof MapSourceDataWrapper) {
            MapSourceDataWrapper datasource = (MapSourceDataWrapper) data;
            JsonElement value = datasource.getItem(id);
           /* if (value == null)
                throw new UnFindIdError("can't find " + id + " in table " + table);*/
            setValue(value);
        } else {
            throw new UnMatchInstanceError("need instance  MapSourceDataWrapper");
        }
    }

    public String getTable() {
        return table;
    }

    public String getId() {
        return id;
    }
}
