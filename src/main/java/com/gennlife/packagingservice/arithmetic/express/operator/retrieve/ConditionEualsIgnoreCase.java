package com.gennlife.packagingservice.arithmetic.express.operator.retrieve;

import com.gennlife.packagingservice.arithmetic.express.abstracts.AbstractListOperator;

/**
 * Created by Chenjinfeng on 2017/6/22.
 */
public class ConditionEualsIgnoreCase extends AbstractListOperator {


    @Override
    protected boolean checkItem(String source, String item) {
        return source.equalsIgnoreCase(item);
    }
}
