package org.healthyin.OfficeKiller.utils;

import lombok.Getter;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

/**
 * @author healthyin
 * @version WordHandler 2022/11/26
 */
public class WordHandler {

    @Getter
    private static WordprocessingMLPackage wordprocessingMLPackage;

    public WordHandler(String path) {
        try {
            FileInputStream is = new FileInputStream(path);
            wordprocessingMLPackage = WordprocessingMLPackage.load(is);
            System.out.println("yes");
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void save(String path) {
        StringBuffer sb = new StringBuffer();
        sb.append("工程管理部上周工作情况（");
        sb.append(DateUtils.getCurrentWeekSaturday().get(Calendar.MONTH));
        sb.append("月");
        sb.append(DateUtils.getCurrentWeekSaturday().get(Calendar.DAY_OF_MONTH));
        sb.append("日）.docx");
        File file = new File(path + sb);
        try {
            wordprocessingMLPackage.save(file);
        } catch (Docx4JException e) {
            System.out.println("word保存失败" + e.getMessage());
        }
    }


}
