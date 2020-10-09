package test.es.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import test.es.enums.MaterialType;
import test.es.enums.OnlineState;

import java.time.LocalDateTime;


/**
 * @Author: nobody
 * @Date: 2020-09-09 17:05
 * @Desc: 参与投票的素材
 */
@Getter
@Setter
@ToString
public class VoteMaterial {

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 素材编号
     */
    private String code;

    /**
     * 素材视频编号
     */
    private String videoCode;

    /**
     * 素材名
     */
    private String name;

    /**
     * 图片路径
     */
    private String pic;

    @TableField(exist = false)
    private String picPath;

    /**
     * 素材类型
     */
    private MaterialType type;

    /**
     * 素材状态
     */
    private OnlineState state;

    /**
     * 标识美食类素材是否是旧菜新做
     */
    private Boolean recycle;

    /**
     * 素材描述
     */
    private String description;

}
