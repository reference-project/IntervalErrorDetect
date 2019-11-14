package core;

import core.learn.FieldHotkeyFindLoader;
import org.apache.spark.api.java.JavaPairRDD;
import util.File.ResultBackup;
import util.Spark.ElasticDataRetrieve;
import util.Time;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Learn implements Serializable {
    private Long queryStartTime = 0L;
    private Long queryFinishTime = 0L;
    private Long jobStartTime = 0L;
    private Long jobFinishTime = 0L;

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
//        Thread.sleep();
        while (!Thread.currentThread().isInterrupted()) {
            jobStartTime = Time.now();
            execute();
            jobFinishTime = Time.now();
            Thread.sleep(5 * 60 * 1000);
        }
    }

    public void execute (){
        queryStartTime = getQueryStartTime();
        queryFinishTime = getQueryFinishTime();

        ElasticDataRetrieve dataRetrieve = new ElasticDataRetrieve();
        Map<String, JavaPairRDD<String, Map<String, Object>>> esRddMap =
                dataRetrieve.retrieve(queryStartTime,queryFinishTime,500L);

        if (null == esRddMap)
            return;

        setQueryStartTime(queryFinishTime);
        FieldHotkeyFindLoader learn = new FieldHotkeyFindLoader();
        Map<String, List<List<String>>> result = learn.execute(esRddMap);
        ResultBackup file = new ResultBackup();
        file.save(result);
    }

}
