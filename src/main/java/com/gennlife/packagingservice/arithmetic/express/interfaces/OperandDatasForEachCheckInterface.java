package com.gennlife.packagingservice.arithmetic.express.interfaces;

import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractDirectOperandCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;

/**
 * Created by Chenjinfeng on 2017/10/19.
 */
//带数据源的
public interface OperandDatasForEachCheckInterface {
    public PathNode getFindPathNode();


    public  void parse(AbstractDirectOperandCheck op);

}
