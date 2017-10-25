package com.gennlife.packagingservice.arithmetic.express.abstracts;


import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;

/**
 * Created by Chenjinfeng on 2017/10/12.
 */
public interface ExpressInterface {
    String NEED_PATH_KEY = "needPath";
    String DETAIL_KEY = "key";
    String OPERATOR_KEY="operator";
    String DETAILS_ARRAY_KEY="detail";

    public boolean hasError();

    public ExpressInterface parse();


    public boolean isFindflag();


    public PathNode getFindPathNode();
}
