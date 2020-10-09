package test.es.exceptions;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import test.es.enums.ResultEnum;

@Data
public class ClientException extends RuntimeException {

    private Integer code;
    private String msg;

    public ClientException(){
        super();
    }

    public ClientException(ResultEnum resultEnum){
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public ClientException(ResultEnum resultEnum, String... params){
        super(StrUtil.format(resultEnum.getMsg(),params));
        this.code = resultEnum.getCode();
        this.msg = StrUtil.format(resultEnum.getMsg(),params);
    }

    public ClientException(String msg) {
        super(msg);
        this.code = ResultEnum.ERROR.getCode();
        this.msg = msg;
    }

    public ClientException(Integer code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public static void error(){
        throw new ClientException(ResultEnum.ERROR);
    }

    public static void error(ResultEnum resultEnum){
        throw new ClientException(resultEnum);
    }

    public static void error(String msg){
        throw new ClientException(ResultEnum.ERROR.getCode(),msg);
    }

    public static void error(String msg,Object... params){
        throw new ClientException(ResultEnum.ERROR.getCode(), StrUtil.format(msg,params));
    }

    public static void checkForThrow(Boolean condition, String msg, Object... params) {
        if (condition) {
            throw new ClientException(ResultEnum.ERROR.getCode(), StrUtil.format(msg, params));
        }
    }

    public static void checkForThrow(Boolean condition, ResultEnum resultEnum) {
        if (condition) {
            throw new ClientException(resultEnum);
        }
    }

    public static void checkForThrow(Boolean condition, ResultEnum resultCode, String... params) {
        if (condition) {
            throw new ClientException(resultCode,params);
        }
    }

}
