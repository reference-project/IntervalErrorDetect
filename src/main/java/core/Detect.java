package core;

import core.detect.FieldHotKeyDetector;
import org.apache.spark.api.java.JavaPairRDD;
import util.spark.ElasticDataRetrieve;
import util.Time;

import java.io.*;
import java.util.Map;

public class Detect implements Serializable {
    private Long detectStartTime = 0L;
    private Long detectFinishTime = 0L;
    private Long jobStartTime = 0L;
    private Long jobFinishTime = 0L;


    public void autorun () throws InterruptedException {
            jobStartTime = Time.now();
            execute();
            jobFinishTime = Time.now();
    }

    public void execute () throws InterruptedException {
        detectStartTime = getDetectStartTime();
        detectFinishTime = Time.now();

        FieldHotKeyDetector f = new FieldHotKeyDetector();

        ElasticDataRetrieve dataRetrieve = new ElasticDataRetrieve();
        Map<String, JavaPairRDD<String, Map<String, Object>>> esRddMap =
                dataRetrieve.retrieveAll(detectStartTime,detectFinishTime,500L);

        if (null == esRddMap) {
//            System.out.println("esrdd null");
            return;
        }
        f.detect(esRddMap, detectStartTime);
        detectFinishTime = Time.now();
        setDetectStartTime(detectFinishTime);
    }

    private Long getDetectStartTime () {
        return detectStartTime;
    }

    private void setDetectStartTime (Long detectFinishTime) {
        this.detectStartTime = detectFinishTime;
    }

}
