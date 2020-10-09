package test.es.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import test.es.entity.RespVo;


@RestControllerAdvice
@Slf4j
public class UnionExceptionHandler {


    @ExceptionHandler(value = {ClientException.class})
    @ResponseBody
    public RespVo clientException(ClientException ex) {
        log.error(ex.getMsg());
        return RespVo.err(ex.getCode(), ex.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public RespVo handleException(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        return RespVo.err();
    }
}
