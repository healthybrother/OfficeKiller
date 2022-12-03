package org.healthyin.OfficeKiller;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.docx4j.com.google.common.collect.Maps;
import org.healthyin.OfficeKiller.models.ExtractConfig;
import org.healthyin.OfficeKiller.models.Performance;
import org.healthyin.OfficeKiller.models.PerformanceProgress;
import org.healthyin.OfficeKiller.models.Staff;
import org.healthyin.OfficeKiller.utils.ConfigHandler;
import org.healthyin.OfficeKiller.utils.DateUtils;
import org.healthyin.OfficeKiller.utils.ExcelHandler;
import org.healthyin.OfficeKiller.utils.WordHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author healthyin
 * @version TransExecutor 2022/12/3
 */
public class TransExecutor {

    /**
     * 从文件中整理到的数据
     */
    private static ExcelHandler excelHandler;
    private static WordHandler wordHandler;
    private static List<Staff> staffList = Lists.newArrayList();
    private static Map<String, List<ExtractConfig>> staffConfigs = Maps.newHashMap();

    private static Map<String, Map<String, String>> textExtractErrorMessage = Maps.newHashMap();

    private static Map<String, Map<String, String>> textFillErrorMessage = Maps.newHashMap();

    public void execute() {
        //1. 数据准备阶段
        prepareData();
        //2. 数据处理
        //2.1筛选出需要修改的信息
        Map<String, Pair<String, String>> textNeedFill = getFillData();
        //2.2填入信息

    }

    private void prepareData() {
        excelHandler = new ExcelHandler("src/main/resources/2022年每周工作填报表（2022-10-21）.xlsx");
        wordHandler = new WordHandler("src/main/resources/工程管理部上周工作情况 (2).docx");
        staffList = excelHandler.initStaffInfo();
        staffConfigs = ConfigHandler.getConfigFromLocal();
    }

    /**
     *
     * @return <内容, <位置, 填入方式>>
     */
    private Map<String, Pair<String, String>> getFillData() {
        Map<String, Pair<String, String>> result = Maps.newHashMap();
        staffList.forEach(staff -> {
            Map<String, String> errorInfo = Maps.newHashMap();
            if (!staffConfigs.containsKey(staff.getName())) {
                errorInfo.put("没有找到员工配置", "没有找到员工配置");
                textExtractErrorMessage.put(staff.getName(), errorInfo);
                return;
            }
            List<ExtractConfig> configs = staffConfigs.get(staff.getName());
            configs.forEach(config -> {
                for(Performance performance : staff.getPerformanceList()) {
                    if (performance.getTitle().equals(config.getPerformanceTitle())) {
                        int version = performance.getVersion();
                        PerformanceProgress progress = performance.getProgressList().get(version);

                        if (null == progress.getPeriod() || (null == progress.getPeriod().getLeft() && null == progress.getPeriod().getRight())) {
                            errorInfo.put(performance.getTitle(), "最新版本没填日期/没解析出日期");
                            return;
                        }
                        switch (DateUtils.isCurrentWeek(progress.getPeriod())) {
                            case 1:
                                while(DateUtils.isCurrentWeek(progress.getPeriod()) == 1) {
                                    progress = performance.getProgressList().get(--version);
                                }
                                break;
                            case -1:
                                errorInfo.put(performance.getTitle(), "没有找到当周日报");
                                return;
                            default:
                                break;
                        }
                        String text = progress.getProgressDetail();
                        if ( null == text || text.isBlank()) {
                            errorInfo.put(performance.getTitle(), "本周进展为空");
                            return;
                        }

                        //段
                        if (config.getExtractType().equals("p")) {
                            //没有过滤词直接写
                            if (null == config.getKeyWordList() || config.getKeyWordList().isEmpty()) {
                                result.put(text, new ImmutablePair<>(config.getDocxPosition(), config.getWriteType()));
                                return;
                            } else {
                                AtomicBoolean flag = new AtomicBoolean(true);
                                config.getKeyWordList().forEach(keyword -> {
                                    if (!text.contains(keyword)) {
                                        flag.set(false);
                                    }
                                });
                                if (flag.get()) {
                                    result.put(text, new ImmutablePair<>(config.getDocxPosition(), config.getWriteType()));
                                    return;
                                } else {
                                    errorInfo.put(performance.getTitle(), "没有找到所有关键词:" + config.getKeyWordList().toString() + ", 所以不填写, 可能需要核对");
                                }
                            }
                            //单行
                        } else if (config.getExtractType().equals("l")) {
                            List<String> textList = List.of(text.split("\n"));
                            if (null == config.getKeyWordList() || config.getKeyWordList().isEmpty()) {
                                result.put(textList.get(0), new ImmutablePair<>(config.getDocxPosition(), config.getWriteType()));
                                return;
                            } else {
                                for(String line : textList) {
                                    AtomicBoolean flag = new AtomicBoolean(true);
                                    config.getKeyWordList().forEach(keyword -> {
                                        if (!line.contains(keyword)) {
                                            flag.set(false);
                                        }
                                    });
                                    if (flag.get()) {
                                        result.put(line + "\n", new ImmutablePair<>(config.getDocxPosition(), config.getWriteType()));
                                        return;
                                    }
                                }
                                errorInfo.put(performance.getTitle(), "没有找到所有关键词:" + config.getKeyWordList().toString() + ", 所以不填写, 可能需要核对");
                            }
                            //多行
                        } else if (config.getExtractType().equals("ls")) {
                            StringBuffer sb = new StringBuffer();
                            List<String> textList = List.of(text.split("\n"));
                            for(String line : textList) {
                                AtomicBoolean flag = new AtomicBoolean(true);
                                config.getKeyWordList().forEach(keyword -> {
                                    if (!line.contains(keyword)) {
                                        flag.set(false);
                                    }
                                });
                                if (flag.get()) {
                                    sb.append(line + "\n");
                                }
                            }
                            if (!sb.toString().isBlank()) {
                                result.put(sb.toString(), new ImmutablePair<>(config.getDocxPosition(), config.getWriteType()));
                                return;
                            }
                            errorInfo.put(performance.getTitle(), "没有找到所有关键词:" + config.getKeyWordList().toString() + ", 所以不填写, 可能需要核对");
                        //段分行, 标号, 默认就不筛选了
                        } else if (config.getExtractType().equals("lp")) {
                            StringBuffer sb = new StringBuffer();
                            int line = 1;
                            for(char ch : text.toCharArray()) {
                                if (ch == '（') {
                                    sb.append("\n  ");
                                }
                                sb.append(ch);
                                if (ch == '\n') {
                                    sb.append(line++).append(". ");
                                }
                            }
                            result.put(sb.toString(), new ImmutablePair<>(config.getDocxPosition(), config.getWriteType()));
                            return;
                        }
                    }
                }
                errorInfo.put(config.getPerformanceTitle(), "没有找到对应的考核指标");
            });
            textExtractErrorMessage.put(staff.getName(), errorInfo);
        });
        return result;
    }

    private void fillToDocx(Map<String, Pair<String, String>> text) {

    }
}
