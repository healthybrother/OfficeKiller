package org.healthyin.OfficeKiller.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author healthyin
 * @version ExtractConfig 2022/12/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtractConfig implements Serializable {

    private static final long serialVersionUID = -751869025482016237L;
    /**
     * 对应的考核指标
     */
    private String performanceTitle;

    /**
     * 关键词， 为空则无需筛选
     */
    private List<String> keyWordList;

    /**
     * 在word中填写的位置, 为空则需要单列
     */
    private String docxPosition;

    /**
     * 抽取的类型, 筛选成功后, 取一段还是取一行
     * l:一行
     * p:段
     * lp:整段, 每行加编号
     * ls:多行(每行都匹配仅保留匹配上的行)
     */
    private String extractType;

    /**
     * 写入word的方式
     * ah:追加写, 标题之后
     * ra:替换标题下内容后追加写
     * r:替换标题下的内容
     * wh:新增大标题写入, 新增在头部
     */
    private String writeType;

}
