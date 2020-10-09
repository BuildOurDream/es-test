package test.es.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import test.es.enums.ResultEnum;

/**
 * @Author: nobody
 * @Date: 2020-10-05 13:43
 * @Desc:
 */
@Data
public class RespVo {

    private Integer code;

    private String msg;

    private Object data;

    public RespVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespVo(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static RespVo ok() {
        return new RespVo(1, "ok", null);
    }

    public static RespVo ok(Object data) {
        return new RespVo(1,"ok",data);
    }

    public static RespVo err() {
        return new RespVo(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getMsg());
    }

    public static RespVo err(Integer code, String msg) {
        return new RespVo(code, msg);
    }
}
