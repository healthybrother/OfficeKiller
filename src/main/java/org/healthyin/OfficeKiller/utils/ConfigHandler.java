package org.healthyin.OfficeKiller.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.compress.utils.Lists;
import org.docx4j.com.google.common.collect.Maps;
import org.healthyin.OfficeKiller.models.ExtractConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author healthyin
 * @version ConfigHandler 2022/12/3
 */
public class ConfigHandler {

    public static void saveConfig(Map<String, List<ExtractConfig>> configs) throws IOException {
        String configJsonString = JSON.toJSONString(configs);
        FileWriter fw = new FileWriter("src/main/resources/config.json");
        fw.write(configJsonString);
        fw.flush();
        fw.close();
    }

    public static Map<String, List<ExtractConfig>> getConfigFromLocal(){
        try {
            String configJsonString = Files.readAllLines(Paths.get("src/main/resources/config.json")).get(0);
            Map<String, List<ExtractConfig>> result =  JSONObject.parseObject(configJsonString, new TypeReference<Map<String, List<ExtractConfig>>>(){});
            return result;
        } catch (IOException e) {
            return Maps.newHashMap();
        }
    }

    public static void modifyConfig(Map<String, List<ExtractConfig>> modifyConfigs) throws IOException {
        Map<String, List<ExtractConfig>> configMap = getConfigFromLocal();

        modifyConfigs.forEach((staffName, modifyConfig) -> {
            if (configMap.containsKey(staffName)) {
                List<ExtractConfig> currentConfigs = configMap.get(staffName);
                List<ExtractConfig> newConfigs = Lists.newArrayList();
                modifyConfig.forEach(config -> {
                    boolean flag = false;
                    for (ExtractConfig curConfig : currentConfigs) {
                        if (curConfig.getPerformanceTitle().equals(config.getPerformanceTitle())) {
                            if (!config.getExtractType().isBlank()) {
                                curConfig.setExtractType(config.getExtractType());
                            }
                            if (!config.getDocxPosition().isBlank()) {
                                curConfig.setDocxPosition(config.getDocxPosition());
                            }
                            if (!config.getWriteType().isBlank()) {
                                curConfig.setWriteType(config.getWriteType());
                            }
                            if (null != config.getKeyWordList() && !config.getKeyWordList().isEmpty()) {
                                curConfig.getKeyWordList().addAll(config.getKeyWordList());
                            }
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        newConfigs.add(config);
                    }
                });
                currentConfigs.addAll(newConfigs);
            } else {
                configMap.put(staffName, modifyConfig);
            }
        });
        saveConfig(configMap);
    }
}
