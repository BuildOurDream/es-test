package test.es.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: nobody
 * @Date: 2020-09-12 19:41
 * @Desc:
 */
@Getter
@AllArgsConstructor
public enum PlatSource {

    TV("电视", 0.6),
    WE_CHAT("微信", 0.2),
    DOU_YIN("抖音",0.2);

    private String desc;

    /**
     * 计算总分时得分所占比例
     */
    private Double proportion;
}
