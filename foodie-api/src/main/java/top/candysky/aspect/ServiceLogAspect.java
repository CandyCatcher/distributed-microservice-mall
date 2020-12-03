package top.candysky.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    private final static Logger log = LoggerFactory.getLogger("ServiceLogAspect");

    /**
     * AOP通知：
     * 1.前置通知
     * 2.后置通知：在方法正常调用之后执行
     * 3.环绕通知
     * 4.异常通知
     * 5.最终通知：在方法调用之后执行
     */
    @Around("execution(* top.candysky.service.impl..*.*(..))")
    public Object reportTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // {}表示占位符 {}.{}表示哪个类的哪个方法
        log.info("========== 开始执行 {}.{} ==========",
                joinPoint.getTarget().getClass(), joinPoint.getSignature().getName());

        long beginTime = System.currentTimeMillis();

        // 执行目标service
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long takeTime = endTime - beginTime;

        if (takeTime > 3000) {
            log.error("========== 执行结束，耗时：{} 毫秒 ==========", takeTime);
        } else if (takeTime > 2000) {
            log.warn("========== 执行结束，耗时：{} 毫秒 ==========", takeTime);
        } else {
            log.info("========== 执行结束，耗时：{} 毫秒 ==========", takeTime);
        }

        return result;
    }
}
