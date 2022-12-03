package org.healthyin.OfficeKiller;

import org.healthyin.OfficeKiller.models.ExtractConfig;
import org.healthyin.OfficeKiller.utils.ConfigHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
//        System.out.println(System.getProperty("user.dir"));
//        ExcelHandler excelHandler = new ExcelHandler("src/main/resources/2022年每周工作填报表（2022-10-21）.xlsx");
//        excelHandler.initStaffInfo();
//        WordHandler wordHandler = new WordHandler("src/main/resources/工程管理部上周工作情况 (2).docx");
//        System.out.println("yes");



        Map<String, List<ExtractConfig>> configMap = ConfigHandler.getConfigFromLocal();
        ExtractConfig extractConfig = configMap.get("朱汉良").get(0);
        System.out.println(extractConfig.getKeyWordList());
        System.out.println("jjj");

    }
}