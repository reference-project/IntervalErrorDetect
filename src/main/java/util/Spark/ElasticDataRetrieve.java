package util.Spark;

import dao.elsaticsearch.ElasticSearch;
import org.apache.spark.api.java.JavaPairRDD;
import util.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticDataRetrieve {
    private ElasticSearch elasticSearch = new ElasticSearch();
    private SparkDataProcess dataProcess = new SparkDataProcess();
    private JavaPairRDD<String, Map<String, Object>> esRdd;
    private Map<String, JavaPairRDD<String, Map<String, Object>>> esRddMap = new HashMap<>();
    private List<String> indices = Config.getElasticsearchIndices();
    private Long count = 0L;

    public Map<String, JavaPairRDD<String, Map<String, Object>>> retrieve (Long start, Long end) {
        count = 0L;
        if (end <= start)
            return null;
        if (indices == null)
            return null;
        for (String index : indices) {
            esRdd = dataProcess.resetJavaPairRDD(index, elasticSearch.getResult(index, start, end));
            if (esRdd == null)
                continue;
            esRddMap.put(index, esRdd);
            count = count + esRdd.count();
        }
        return esRddMap;
    }

    public Map<String, JavaPairRDD<String, Map<String, Object>>> retrieve (Long start, Long end, Long recordMoreThan) {
        retrieve(start, end);
        if (count <= recordMoreThan)
            return null;
        return esRddMap;
    }
}