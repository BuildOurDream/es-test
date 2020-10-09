package test.es.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: nobody
 * @Date: 2020-09-09 18:06
 * @Desc:素材类型
 */
@Getter
@AllArgsConstructor
public enum MaterialType {

    FOOD("美食"),
    FILM("电影"),
    OTHER("其他");

    private String desc;

}
