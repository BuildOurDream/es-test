package test.es.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: nobody
 * @Date: 2020-09-10 11:46
 * @Desc: 素材状态
 */
@Getter
@AllArgsConstructor
public enum OnlineState {

    ON_LINE("上线"),
    OFF_LINE("下线");

    private String desc;
}
