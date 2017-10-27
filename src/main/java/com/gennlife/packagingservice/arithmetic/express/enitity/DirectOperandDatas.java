package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractDirectOperandCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractOperandDatasWrapper;
import com.gennlife.packagingservice.arithmetic.express.exceptions.PathNodeError;
import com.gennlife.packagingservice.arithmetic.express.interfaces.OperandDatasForEachCheckInterface;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

import static com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface.DETAIL_KEY;

/**
 * Created by Chenjinfeng on 2017/10/18.
 */
public class DirectOperandDatas extends AbstractOperandDatasWrapper implements OperandDatasForEachCheckInterface {
    private final LinkedList<LinkedList<PathItem>> findindex;
    private LinkedList<FindIndexModel<JsonElement>> elements;
    public static final String DESCIBEKEY = "unaryKey";
    private PathNode findPathNode;
    private static final Logger logger = LoggerFactory.getLogger(DirectOperandDatas.class);

    public DirectOperandDatas(JsonObject config, PathNode contextNode, ConditionCheck conditionCheck) {
        super(config, contextNode, conditionCheck);
        String detailkey = JsonAttrUtil.getStringValue(DETAIL_KEY, config);
        if (StringUtil.isEmptyStr(detailkey)) {
            detailkey = JsonAttrUtil.getStringValue(DESCIBEKEY, config);
        }
        this.findindex = PathNode.getPathItem(contextNode, detailkey);
        if (findindex == null) {
            throw new PathNodeError(" findindex must not null");
        }
        LinkedList<LinkedList<PathItem>> lastFindindex = findindex;
        LinkedList<FindIndexModel<JsonElement>> datas = conditionCheck.getOriginData();
        for (LinkedList<PathItem> findindexItem : lastFindindex) {
            detailkey = FindIndexModel.buildPathByPathItem(detailkey, findindexItem);
            elements = FindIndexModel.getAllValue(detailkey, datas, elements);
        }

    }

    @Override
    public PathNode getFindPathNode() {
        return findPathNode;
    }

    @Override
    public void parse(AbstractDirectOperandCheck op) {
        if (op == null) return;
        if (elements == null || elements.size() == 0) {
            if (op.isEmptyListOK()) {
                this.findPathNode = getContextNode();
                return;
            } else {
                this.findPathNode = null;
            }
        }
        LinkedList<LinkedList<PathItem>> result = new LinkedList<>();
        if (elements != null) {
            for (FindIndexModel<JsonElement> item : elements) {
                boolean find = op.isMatch(item.getValue());
                if (find) result.add(FindIndexModel.getFindPath(item));
            }
        }
        this.findPathNode = PathNode.getPathNodeFromPath(result);
    }


}
