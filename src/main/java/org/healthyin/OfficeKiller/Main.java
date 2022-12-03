package org.healthyin.OfficeKiller;

import org.healthyin.OfficeKiller.utils.ExcelHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        ExcelHandler excelHandler = new ExcelHandler("src/main/resources/2022年每周工作填报表（2022-10-21）.xlsx");
        excelHandler.initStaffInfo();
    }
}