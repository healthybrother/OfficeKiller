package org.healthyin.OfficeKiller.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author healthyin
 * @version PerformanceProgress 2022/11/26
 */
@Data
public class PerformanceProgress implements Serializable {

    private static final long serialVersionUID = -8026410478430525217L;
    /**
     * 唯一键
     */
    private String progressId;

    /**
     * 周期开始时间
     */
    private Date startTime;

    /**
     * 周期结束时间
     */
    private Date endTime;

//    /**
//     * 对应的考核指标
//     */
//    private Performance targetPerformance;
//
//    /**
//     * 对应的员工信息
//     */
//    private Staff staff;

    /**
     * 周期性进展内容
     */
    private String progressDetail;
}
