package com.gennlife.packagingservice.arithmetic.express;

import com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.express.factorys.ConditionOperatorFactory;
import com.gennlife.packagingservice.arithmetic.express.interfaces.InstructionOperatorInterface;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.arithmetic.utils.StringUtil;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/12.
 */
public class ArithmeticExpress implements ExpressInterface {
    private static final Logger logger = LoggerFactory.getLogger(ArithmeticExpress.class);
    private ConditionCheck conditionCheck;
    private boolean error;
    private boolean findflag = false;
    private String detailoperator;
    private PathNode nodeContext;
    private PathNode findPathNode;
    private JsonObject config;
    private String needPath;


    public ArithmeticExpress(ConditionCheck conditionCheck, PathNode findindexNode, String detailoperator, JsonObject config) {
        this.nodeContext = findindexNode;
        this.needPath = JsonAttrUtil.getStringValue(NEED_PATH_KEY, config);
        this.detailoperator = detailoperator;
        this.config = config;
        this.conditionCheck = conditionCheck;
    }

    public boolean hasError() {
        return error;
    }

    public boolean isFindflag() {
        return findflag;
    }

    public PathNode getFindPathNode() {
        return findPathNode;
    }

    public ArithmeticExpress parse() {
        String detailkey = JsonAttrUtil.getStringValue(DETAIL_KEY, config);
        if (!StringUtil.isEmptyStr(detailkey))
            conditionCheck.setLastPath(detailkey);
        InstructionOperatorInterface conditionOperator = ConditionOperatorFactory.getInstructionOperator(detailoperator,config,conditionCheck );
        if (conditionOperator == null) {
            logger.error("unknown operator " + detailoperator);
        }
        if (conditionOperator != null) {
            conditionOperator.count(config, this.nodeContext, this.conditionCheck);
            if (conditionOperator.hasError()) {
                error = true;
                return this;
            }
            findflag = conditionOperator.isFindflag();
            findPathNode = conditionOperator.getFindPathNode();
            findPathNode = PathNode.getNeedPathNode(findPathNode, needPath);
            error = false;
            return this;
        }
        return this;

    }

}
