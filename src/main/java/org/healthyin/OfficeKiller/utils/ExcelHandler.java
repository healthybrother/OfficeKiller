package org.healthyin.OfficeKiller.utils;

import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.healthyin.OfficeKiller.models.Performance;
import org.healthyin.OfficeKiller.models.PerformanceProgress;
import org.healthyin.OfficeKiller.models.Staff;

import java.io.FileInputStream;
import java.nio.file.FileSystemException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author healthyin
 * @version ExcelHandler 2022/11/26
 */
public class ExcelHandler {

    private static Workbook fillTable;

    public ExcelHandler(String path) {
        try {
            FileInputStream is = new FileInputStream(path);
            if (path.toLowerCase().endsWith("xlsx")) {
                fillTable = new XSSFWorkbook(is);
            } else if (path.toLowerCase().endsWith("xls")) {
                fillTable = new HSSFWorkbook(is);
            } else {
                throw new FileSystemException("文件类型不对");
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Staff> initStaffInfo() {
        List<Staff> result = Lists.newArrayList();

        Iterator<Sheet> iterator = fillTable.sheetIterator();

        while (iterator.hasNext()) {
            Sheet singleStaffSheet = iterator.next();
            Staff staff = new Staff();
            staff.setStaffId(UUID.randomUUID().toString());
            staff.setName(singleStaffSheet.getSheetName());
            //从第三行开始才有数据
            for (int i = 2; i < singleStaffSheet.getLastRowNum(); i++) {
                Row row = singleStaffSheet.getRow(i);
                Performance performance = getPerformanceFromRow(singleStaffSheet, row);
                staff.getPerformanceList().add(performance);
            }
            int version = staff.getPerformanceList().get(0).getVersion();
            staff.getPerformanceList().forEach(performance -> {
//                if (performance.getVersion() != version) {
//                    throw new RuntimeException(staff.getName() + "周报内容不全");
//                }
            });
            result.add(staff);
        }

        return result;
    }

    private Performance getPerformanceFromRow(Sheet sheet, Row row) {
        Performance performance = new Performance();
        performance.setPerformanceId(UUID.randomUUID().toString());
        int startCol = 0;
        while(getCellValue(row.getCell(startCol)).isBlank() || getCellValue(row.getCell(startCol)).equals("ROW()-2")) {
            startCol++;
        }
        performance.setTitle(getCellValue(row.getCell(startCol++)));
        performance.setTarget(getCellValue(row.getCell(startCol++)));
        //第4列开始为周报内容
        int version = 0;
        for(;startCol < row.getLastCellNum();startCol++) {
            Cell cell = row.getCell(startCol);
            //没有内容直接略过，因为某些原因中间的块也会是null
            if (cell == null || getCellValue(cell).isBlank()) {
                continue;
            }
            PerformanceProgress progress = new PerformanceProgress();
            progress.setProgressId(UUID.randomUUID().toString());
            progress.setProgressDetail(getCellValue(cell));

            String timeStr = getCellValue(sheet.getRow(1).getCell(cell.getColumnIndex()));

            List<Date> periodTime = getPeriodTime(timeStr);

            if (!periodTime.isEmpty()) {
                progress.setStartTime(periodTime.get(0));
                progress.setEndTime(periodTime.get(1));
            }

            performance.getProgressList().put(startCol-2, progress);
            version = startCol-2;
        }
        performance.setVersion(version);
        return performance;
    }


    private String getCellValue(Cell cell) {
        if (null == cell) {
            return "";
        }
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case ERROR:
                return String.valueOf(cell.getErrorCellValue());
            default:
                return "";
        }
    }

    private List<Date> getPeriodTime(String str){
        List<Date> result = Lists.newArrayList();
        if (null == str || str.isBlank()) {
            return result;
        }
        List<String> splitString = Arrays.asList(str.split("[（）月\\-日]"));
        List<String> dateNum = Lists.newArrayList();
        splitString.forEach(tmpStr -> {
            if (isNumeric(tmpStr)) {
                dateNum.add(tmpStr);
            }
        });

        Calendar today = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date startTime;
        Date endTime;
        try{
            if (dateNum.size() == 3) {
                String startTimeString = today.get(Calendar.YEAR)
                        + (dateNum.get(0).length() == 1 ? ("0" + dateNum.get(0)) : dateNum.get(0))
                        + (dateNum.get(1).length() == 1 ? ("0" + dateNum.get(1)) : dateNum.get(1));
                String endTimeString = today.get(Calendar.YEAR)
                        + (dateNum.get(0).length() == 1 ? ("0" + dateNum.get(0)) : dateNum.get(0))
                        + (dateNum.get(2).length() == 1 ? ("0" + dateNum.get(2)) : dateNum.get(2));
                startTime = simpleDateFormat.parse(startTimeString);
                endTime = simpleDateFormat.parse(endTimeString);
                result.add(startTime);
                result.add(endTime);
            } else if (dateNum.size() == 4) {
                String startTimeString = today.get(Calendar.YEAR)
                        + (dateNum.get(0).length() == 1 ? ("0" + dateNum.get(0)) : dateNum.get(0))
                        + (dateNum.get(1).length() == 1 ? ("0" + dateNum.get(1)) : dateNum.get(1));
                String endTimeString = today.get(Calendar.YEAR)
                        + (dateNum.get(2).length() == 1 ? ("0" + dateNum.get(2)) : dateNum.get(2))
                        + (dateNum.get(3).length() == 1 ? ("0" + dateNum.get(3)) : dateNum.get(3));
                startTime = simpleDateFormat.parse(startTimeString);
                endTime = simpleDateFormat.parse(endTimeString);
                result.add(startTime);
                result.add(endTime);
            }
            //处理跨年的情况
            if (!result.isEmpty()) {
                if(result.get(1).before(result.get(0))) {
                    String startTimeString = today.get(Calendar.YEAR)-1
                            + (dateNum.get(0).length() == 1 ? ("0" + dateNum.get(0)) : dateNum.get(0))
                            + (dateNum.get(1).length() == 1 ? ("0" + dateNum.get(1)) : dateNum.get(1));
                    result.set(0, simpleDateFormat.parse(startTimeString));
                }
            }
        } catch (ParseException e) {
            System.out.println("转时间出错了");;
        }
        return result;
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
