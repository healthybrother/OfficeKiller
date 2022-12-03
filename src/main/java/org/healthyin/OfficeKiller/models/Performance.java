package org.healthyin.OfficeKiller.models;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author healthyin
 * @version Performance 2022/11/26
 */
@Data
public class Performance implements Serializable {
    private static final long serialVersionUID = 2133896527638656880L;

    /**
     * 唯一键
     */
    private String performanceId;

    /**
     * 考核指标
     */
    private String title;

    /**
     * 实现目标
     */
    private String target;

    /**
     * 绩效完成周期进展<版本， 进展>
     */
    private Map<Integer, PerformanceProgress> progressList = new HashMap<>();

    /**
     * 绩效进展最高版本(当前目标有多少个进展)
     */
    private int version;
}
