package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.interfaces.SourceDataWrapperInterface;

/**
 * Created by Chenjinfeng on 2017/10/23.
 */
public class PathNodeDataWrapper implements SourceDataWrapperInterface<PathNode> {
    private PathNode pathNode;

    public PathNodeDataWrapper(PathNode pathNode) {
        this.pathNode = pathNode;
    }

    public PathNodeDataWrapper() {
    }

    @Override
    public PathNode getData() {
        return pathNode;
    }

    public void setPathNode(PathNode pathNode) {
        this.pathNode = pathNode;
    }
}
