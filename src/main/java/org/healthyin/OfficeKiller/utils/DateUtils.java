package org.healthyin.OfficeKiller.utils;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author healthyin
 * @version DateUtils 2022/12/3
 */
public class DateUtils {

    public static Pair<Date, Date> getPeriodTime(String str){
        MutablePair<Date, Date> result = new MutablePair<>();
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
                result.setLeft(startTime);
                result.setRight(endTime);
            } else if (dateNum.size() == 4) {
                String startTimeString = today.get(Calendar.YEAR)
                        + (dateNum.get(0).length() == 1 ? ("0" + dateNum.get(0)) : dateNum.get(0))
                        + (dateNum.get(1).length() == 1 ? ("0" + dateNum.get(1)) : dateNum.get(1));
                String endTimeString = today.get(Calendar.YEAR)
                        + (dateNum.get(2).length() == 1 ? ("0" + dateNum.get(2)) : dateNum.get(2))
                        + (dateNum.get(3).length() == 1 ? ("0" + dateNum.get(3)) : dateNum.get(3));
                startTime = simpleDateFormat.parse(startTimeString);
                endTime = simpleDateFormat.parse(endTimeString);
                result.setLeft(startTime);
                result.setRight(endTime);
            }
            //处理跨年的情况
            if (null != result.getLeft() && null != result.getRight()) {
                if(result.getRight().before(result.getLeft())) {
                    String startTimeString = today.get(Calendar.YEAR)-1
                            + (dateNum.get(0).length() == 1 ? ("0" + dateNum.get(0)) : dateNum.get(0))
                            + (dateNum.get(1).length() == 1 ? ("0" + dateNum.get(1)) : dateNum.get(1));
                    result.setLeft(simpleDateFormat.parse(startTimeString));
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

    public static String getCurrentWeekPeriod(){
        StringBuffer sb = new StringBuffer();
        sb.append("（");
        Calendar cal=Calendar.getInstance();
        return getPeriodString(sb, cal);
    }

    public static String getNextWeekPeriod() {
        StringBuffer sb = new StringBuffer();
        sb.append("（");
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 1);
        return getPeriodString(sb, cal);
    }

    private static String getPeriodString(StringBuffer sb, Calendar cal) {
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        sb.append(cal.get(Calendar.MONTH));
        sb.append("月");
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        sb.append("日");
        sb.append("-");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        sb.append(cal.get(Calendar.MONTH));
        sb.append("月");
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        sb.append("日");
        sb.append("）");
        return sb.toString();
    }

    public static Calendar getCurrentWeekSunday(){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal;
    }

    public static Calendar getCurrentWeekSaturday(){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return cal;
    }

    public static int isCurrentWeek(Pair<Date, Date> period) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (period.getRight().before(cal.getTime())) {
            return -1;
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        if (period.getLeft().after(cal.getTime())) {
            return 1;
        }
        return 0;
    }

}
