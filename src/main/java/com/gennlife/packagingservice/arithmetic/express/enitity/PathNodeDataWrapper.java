package com.gennlife.packagingservice.arithmetic.express.enitity;

import com.gennlife.packagingservice.arithmetic.express.interfaces.SourceDataWrapperInterface;
import com.google.gson.JsonElement;

import java.util.LinkedList;

/**
 * Created by Chenjinfeng on 2017/10/23.
 */
public class PathNodeDataWrapper implements SourceDataWrapperInterface<PathNode> {
    private PathNode pathNode;
    private LinkedList<FindIndexModel<JsonElement>> origindata;

    public PathNodeDataWrapper(PathNode pathNode) {
        this.pathNode = pathNode;
    }

    public PathNodeDataWrapper() {
    }

    public PathNodeDataWrapper(LinkedList<FindIndexModel<JsonElement>> origindata) {
        this.origindata = origindata;
    }

    @Override
    public PathNode getData() {
        if (pathNode == null) {
            if (origindata != null && origindata.size() > 0) {
                pathNode = PathNode.getPathNodeFromJson(origindata);
                origindata = null;
            }
        }
        return pathNode;
    }

    public void setPathNode(PathNode pathNode) {
        this.pathNode = pathNode;
    }
}
