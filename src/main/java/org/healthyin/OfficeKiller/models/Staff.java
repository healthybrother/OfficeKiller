package org.healthyin.OfficeKiller.models;

import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author healthyin
 * @version StaffInfo 2022/11/26
 */
@Data
public class Staff implements Serializable {

    private static final long serialVersionUID = -5664086426757108502L;
    /**
     * 唯一键
     */
    private String staffId;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 员工考核指标列表
     */
    private List<Performance> performanceList = Lists.newArrayList();
}
