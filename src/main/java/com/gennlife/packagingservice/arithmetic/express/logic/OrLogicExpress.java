package com.gennlife.packagingservice.arithmetic.express.logic;

import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractLogicExpress;
import com.gennlife.packagingservice.arithmetic.express.abstracts.ExpressInterface;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathItem;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.google.gson.JsonArray;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Chenjinfeng on 2017/10/16.
 */
public class OrLogicExpress extends AbstractLogicExpress {
    private List<PathNode> tmpNodeList = new LinkedList<>();
    private Set<String> allGroupName = new TreeSet<>();
    private boolean isAll = false;
    private boolean begin = false;

    public OrLogicExpress(ConditionCheck conditionCheck, JsonArray detail, String operator, PathNode globalPathNode, String path) {
        super(conditionCheck, detail, operator, globalPathNode, path);
    }

    @Override
    protected void parseAfter() {
        if (isAll || !begin) {
            this.tmpNode = globalPathNode;
            this.findflag = true;
            return;
        }
        if (tmpNodeList.size() == 0) {
            setNotFind();
            return;
        } else {
            if (tmpNodeList.size() == 1) {
                tmpNode = tmpNodeList.remove(0);
            } else if (allGroupName.size() == 1) {
                tmpNode = tmpNodeList.remove(0);
                for (PathNode node : tmpNodeList) {
                    tmpNode.add(node);
                }
            } else {
                PathNode originDataPath = this.getConditionCheck().getOriginDataPath();
                LinkedList<LinkedList<PathItem>> indexs = new LinkedList<>();
                for (String group : allGroupName) {
                    indexs.addAll(PathNode.getPathItem(originDataPath, group));
                }
                PathNode envNode = PathNode.getPathNodeFromPath(indexs);
                tmpNode = tmpNodeList.remove(0);
                tmpNode.mergeForLeaveAndAddNew(envNode, true);
                for (PathNode node : tmpNodeList) {
                    node.mergeForLeaveAndAddNew(envNode, true);
                    tmpNode.add(node);
                }
            }
        }
        this.findflag = true;

    }

    @Override
    public boolean isContinue(ExpressInterface express) {
        begin = true;
        if (express.isFindflag()) {
            PathNode node = express.getFindPathNode();
            if (node != null) {
                node = node.getNeedPathNode(getNeedPath());
                Set<String> groupNames = node.getGroupName();
                if (groupNames != null && groupNames.size() > 0) {
                    allGroupName.addAll(groupNames);
                } else {
                    isAll = true;
                    return false;
                }
                tmpNodeList.add(node);
            }
        }
        return true;
    }

    @Override
    public PathNode getPathNodeForChild() {
        return globalPathNode.deepCopy();
    }


}
