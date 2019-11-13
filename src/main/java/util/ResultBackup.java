package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ResultBackup {
    Logger logger = LoggerFactory.getLogger(util.ResultBackup.class);
    private final String dirPath = "D:\\Project\\2020\\dig-lib\\";
    synchronized public void save(Map<String, List<List<String>>> hotKeyMap) {
        if (hotKeyMap == null)
            return;
        List<String> elasticsearchField = Config.getElasticsearchFields();
        for (String str: elasticsearchField) {
            saveFile(str, hotKeyMap.get(str));
        }
    }

    private List<List<String>> removeRepeat (List<List<String>> hotKeyLists) {
        if (hotKeyLists == null)
            return null;
        return new ArrayList<>(new HashSet<>(hotKeyLists));
    }

    synchronized private void saveFile (String fileName, List<List<String>> hotKeyLists) {
        hotKeyLists = removeRepeat(hotKeyLists);
        if (fileName == null)
            return;
        if (hotKeyLists == null)
            return;

        String filePath = dirPath + fileName + ".iedb";
        File file = new File(filePath);

        try {
            if(!file.exists())
                file.createNewFile();

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));

            for (List<String> list: hotKeyLists) {
                for (String str : list) {
                    fileWriter.append(str);
                    fileWriter.append(',');
                }
                fileWriter.append("\r\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            logger.error(Long.toString(Time.now()));
            logger.error(e.toString());
        } finally {

        }

    }
}