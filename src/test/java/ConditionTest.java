import com.gennlife.packagingservice.arithmetic.express.ConditionCheck;
import com.gennlife.packagingservice.arithmetic.express.enitity.FindIndexModel;
import com.gennlife.packagingservice.arithmetic.express.enitity.MapSourceDataWrapper;
import com.gennlife.packagingservice.arithmetic.express.enitity.PathNode;
import com.gennlife.packagingservice.arithmetic.utils.FileUtil;
import com.gennlife.packagingservice.arithmetic.utils.JsonAttrUtil;
import com.gennlife.packagingservice.rws.ConfigExcept;
import com.gennlife.packagingservice.rws.RwsConfigTransUtils;
import com.gennlife.packagingservice.rws.RwsCountUtils;
import com.gennlife.packagingservice.rws.entity.CountValueEntity;
import com.gennlife.packagingservice.testUtils.HttpRequestUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Set;

import static com.gennlife.packagingservice.rws.RwsConfigTransUtils.getRefIdList;
import static com.gennlife.packagingservice.rws.RwsConfigTransUtils.transRwsConditionConfig;
import static com.gennlife.packagingservice.rws.RwsCountUtils.ACTIVE_RESULT_KEY;


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

    @Test
    public void testRwsTrans() throws ConfigExcept {
        String config = FileUtil.readFile("rwscon.json");
        JsonObject configJson = JsonAttrUtil.toJsonObject(config);
        JsonArray condition = JsonAttrUtil.getJsonArrayValue("conditions", configJson);
        JsonObject conditionJson = transRwsConditionConfig(condition);

        Set<String> result = getRefIdList(conditionJson);
        configJson.add("condition", condition);
    }

    @Test
    public void testForOnePatient() throws ConfigExcept {
        HttpRequestUtils.initHttpPoolConfig(100, 10);
        JsonObject patient = HttpRequestUtils.getOnePatientAllDataFromSearch(getSearchUrl(), "pat_04fa8836fea4f16441e24fd0bf5b2115", getSearchIndexName());
        String config = FileUtil.readFile("rwsconditon.json");
        JsonElement configJsonElem = JsonAttrUtil.toJsonElement(config);
        JsonObject configJson = null;
        if (configJsonElem instanceof JsonObject)
            configJson = configJsonElem.getAsJsonObject();
        else {
            configJson = configJsonElem.getAsJsonArray().get(0).getAsJsonObject();
        }
        RwsConfigTransUtils.transActiveConfig(configJson);
        String countPath = JsonAttrUtil.getStringValue(ACTIVE_RESULT_KEY, configJson);

        CountValueEntity result = RwsCountUtils.count(patient, configJson, countPath, new MapSourceDataWrapper(),false);

        result = result;
    }


    private String getSearchIndexName() {
        return "yantai_hospital_clinical_patients";
    }

    private String getSearchUrl() {
        return "http://10.0.2.162:8989/search-server/search";
    }
    @Test
    public void testNull()
    {
        String condition=FileUtil.readFile("a.json");
        String value=FileUtil.readFile("b.json");
        JsonObject valueJson=JsonAttrUtil.toJsonObject(value);
        JsonObject conditionJson=JsonAttrUtil.toJsonObject(condition);
        ConditionCheck conditionCheck=new ConditionCheck(conditionJson);
        PathNode result = conditionCheck.getPathItemsByPathNode(valueJson);
        result=result;


    }
}
