import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.FindIndexModel;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.utils.FileUtil;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;


public class ConditionTest {
    private static final Logger logger = LoggerFactory.getLogger(ConditionTest.class);
    @Test
    public void testConditionCheck() {
        String data = FileUtil.readFile("c.json");
        JsonObject jsonData = JsonAttrUtil.toJsonObject(data);

        String conditionstr = FileUtil.readFile("condition.json");
        JsonObject conditionJson = JsonAttrUtil.toJsonObject(conditionstr);
        JsonArray visits = JsonAttrUtil.getJsonArrayValue("visits", jsonData);
        if (visits != null) {
            JsonArray v = new JsonArray();
            v.add(visits.get(0));
            JsonAttrUtil.makeEmpty(visits);
            for (int i = 0; i < 100; i++)
                visits.addAll(v);
        }
        ConditionCheck conditionCheck = new ConditionCheck(conditionJson);
        conditionCheck.initCondition();
        LinkedList<FindIndexModel<JsonElement>> lists = new LinkedList<>();
        FindIndexModel findIndexModel = new FindIndexModel();
        findIndexModel.setValue(jsonData);
        findIndexModel.setKey(null);
        findIndexModel.setP(null);
        findIndexModel.setLeaf(true);
        lists.add(findIndexModel);
        long s = System.currentTimeMillis();
        PathNode result = conditionCheck.getPathItemsByPathNode(lists, null);
        logger.info("time " + (System.currentTimeMillis() - s) + " ms");
        if (visits != null) logger.info("visits.size" + visits.size());
        if (result != null) {
            PathNode tmp = result.getNeedPathNode("visits.inspection_reports.sub_inspection.SUB_INSPECTION_EN");
            PathNode tmp2 = result.getNeedPathNode("visits[1].inspection_reports.55");
        }
        result = result;

    }


}
