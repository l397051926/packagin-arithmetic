package com.gennlife.packagingservice.arithmetic.express.abstracts;

import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.express.interfaces.SupportNotOperatorInterface;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
public abstract class AbstractDirectOperandCheck implements SupportNotOperatorInterface {
    private boolean findFlag = false;
    private PathNode findPathNode = null;
    private static final Logger logger = LoggerFactory.getLogger(AbstractDirectOperandCheck.class);
    private boolean hasError = false;
    /**
     * 取反
     */
    private boolean isNot = false;

    public void setNot(boolean not) {
        isNot = not;
    }

    protected void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public void setFindFlag(boolean findFlag) {
        this.findFlag = findFlag;
    }

    public void setFindPathNode(PathNode findPathNode) {
        this.findPathNode = findPathNode;
    }

    public AbstractDirectOperandCheck() {
    }


    public final boolean isMatch(JsonElement source) {
        if (source != null && source.isJsonObject()) {
            if (!allowJsonObject()) {
                logger.error("not support for jsonObject");
                return false;
            }
        }
        try {
            boolean findFlag = check(source);
            if (hasError) {
                return false;
            }
            //if (isNot) return !findFlag;
            return findFlag;
        } catch (Exception e) {
            //logger.error("", e);
            setHasError(true);
            return false;
        }
    }

    protected abstract boolean check(JsonElement source);

    /**
     * 操作符是否允许空匹配
     */
    public final boolean isEmptyListOK() {
        boolean findFlag = allEmptyList();
        if (isNot) return !findFlag;
        return findFlag;
    }

    public abstract boolean allEmptyList();

    public boolean isFindflag() {
        return findFlag;
    }


    public PathNode getFindPathNode() {
        return findPathNode;
    }


    public boolean allowJsonObject() {
        return false;
    }

    @Override
    public boolean isNot() {
        return isNot;
    }
}
