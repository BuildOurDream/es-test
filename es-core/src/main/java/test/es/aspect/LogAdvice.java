package test.es.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import test.es.entity.RespVo;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;

@Component
@Aspect
@Slf4j
public class LogAdvice {

    private ThreadLocal<Instant> logStartTime = new ThreadLocal<>();
    private ThreadLocal<String> uriRecorder = new ThreadLocal<>();

    @Pointcut("@within(org.springframework.web.bind.annotation.RequestMapping)")
    public void logPointCut(){}

    @Around("logPointCut()")
    public Object aroundRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        logStartTime.set(Instant.now());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        uriRecorder.set(uri);
        log.info("request：{} 【start】. \n params: {}", uri, joinPoint.getArgs());
        Object proceed = joinPoint.proceed();

        return proceed;
    }

    @AfterReturning(value = "logPointCut()", returning = "respVo")
    public void logAfterReturning(RespVo respVo) {
        log.info("request：{} 【success】. spend time: {} ms \n returning：{} ", uriRecorder.get(),
                Duration.between(logStartTime.get(),Instant.now()).toMillis(), respVo);
        uriRecorder.remove();
        logStartTime.remove();
    }

    @AfterThrowing(value = "logPointCut()", throwing = "exception")
    public void logAfterThrowing(Exception exception) {
        log.error("request：{} 【failed】. spend time: {} ms \n exception：{} ", uriRecorder.get(),
                Duration.between(logStartTime.get(),Instant.now()).toMillis(), exception.getMessage());
        uriRecorder.remove();
        logStartTime.remove();
    }
}
