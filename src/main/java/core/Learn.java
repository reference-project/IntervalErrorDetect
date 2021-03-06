package core;

import core.learn.FieldHotKeyFinder;
import core.learn.FieldHotkeyFindLoader;
import dao.elsaticsearch.ElasticSearch;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaPairRDD;
import util.Config;
import util.file.ResultBackup;
import util.spark.ElasticDataRetrieve;
import util.Time;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Learn implements Serializable {
    /**
     *  Learn 类是学习模式的控制器类
     *  具体工作流程：
     *  1. 记录起始时间戳，
     *  2. 调用学习方法
     *  3. 获取dataRetrieve数据获取类，
     *  4. 向dataRetrieve类提供elasticSearch数据源操作类，
     *  5. 获取数据
     *  6. 检测数据量，如果数据量为空或者小于500条数据，可能由于数据过少产生不可靠的学习结果，返回
     *  7. 调用学习组件，统计操作队列发生频率
     *  8. file.save(result)方法将学习结果本地化保存在规则文件中
     */

    private Long queryStartTime = 0L;
    private Long queryFinishTime = 0L;
    private Long jobStartTime = 0L;
    private Long jobFinishTime = 0L;
    private ElasticSearch es ;

    public Learn(ElasticSearch es) {
        this.es = es;
    }

    private Long getQueryStartTime () {
        return queryStartTime;
    }

    private Long getQueryFinishTime () {
        return Time.now();
    }

    private void setQueryStartTime (Long queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public void autorun () throws InterruptedException{
        while(!Thread.currentThread().isInterrupted()) {
            jobStartTime = Time.now();
            execute();
            jobFinishTime = Time.now();
            // 单位为毫秒，1s = 1000ms
            Thread.sleep(Config.getSleepTime() * 1000);
        }
//        Thread.sleep();

    }

    public void execute (){
        queryStartTime = getQueryStartTime();
        queryFinishTime = getQueryFinishTime();
        Map<String, JavaPairRDD<String, Map<String, Object>>> esRddMap =
                new ElasticDataRetrieve().retrieveAll(es, queryStartTime,queryFinishTime,500L);
        if (null == esRddMap)
            return;
        if (Thread.currentThread().isInterrupted())
            return;
        Map<String, List<List<String>>> result = learn(esRddMap);
        fileSaving(result);

//        FieldHotKeyFinder fieldHotkeyFinder = new FieldHotKeyFinder(es);
//        fieldHotkeyFinder.learn(queryStartTime, queryFinishTime);
//        setQueryStartTime(queryFinishTime);
    }

    private Map<String, List<List<String>>> learn (Map<String, JavaPairRDD<String, Map<String, Object>>> esRddMap) {
        if (Thread.currentThread().isInterrupted())
            return null;
        FieldHotkeyFindLoader learn = new FieldHotkeyFindLoader();
        return learn.execute(esRddMap);
    }

    private void fileSaving (Map<String, List<List<String>>> result) {
        if (Thread.currentThread().isInterrupted())
            return;
        ResultBackup file = new ResultBackup();
        file.save(result);
    }

}
