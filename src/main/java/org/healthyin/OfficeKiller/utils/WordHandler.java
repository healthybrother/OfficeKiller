package org.healthyin.OfficeKiller.utils;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.FileInputStream;

/**
 * @author healthyin
 * @version WordHandler 2022/11/26
 */
public class WordHandler {

    private static WordprocessingMLPackage wordprocessingMLPackage;

    public WordHandler(String path) {
        try {
            FileInputStream is = new FileInputStream(path);
            wordprocessingMLPackage = WordprocessingMLPackage.load(is);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
