package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractStaticDataWrapper;
import com.gennlife.packagingservice.arithmetic.express.exceptions.UnMatchInstanceError;
import com.gennlife.packagingservice.arithmetic.express.interfaces.SourceDataWrapperInterface;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
public class MapOperandDatas extends AbstractStaticDataWrapper {
    private static final String CONTEXT_MAP_NAME_KEY = "table";
    private static final String REF_ID_KEY = "id";
    private String table;
    private String id;

    public MapOperandDatas(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        super(config, contextNode, conditionCheck);
        this.table = JsonAttrUtil.getStringValue(CONTEXT_MAP_NAME_KEY, config);
        if (StringUtil.isEmptyStr(table)) {
            throw new RuntimeException("MapOperandDatas need tableName");
        }
        this.id = JsonAttrUtil.getStringValue(REF_ID_KEY, config);
        if (StringUtil.isEmptyStr(table)) {
            throw new RuntimeException("MapOperandDatas need id");
        }
        SourceDataWrapperInterface data = this.conditionCheck.getDataWrapper(table);
        if (data instanceof MapSourceDataWrapper) {
            MapSourceDataWrapper datasource = (MapSourceDataWrapper) data;
            JsonElement value = datasource.getItem(id);
            if (value == null)
                throw new RuntimeException("can't find " + id + " in table " + table);
            setValue(value);
        } else {
            throw new UnMatchInstanceError("need instance  MapSourceDataWrapper");
        }
    }
}
