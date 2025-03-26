package com.zxp.nowcodercommunity.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;


@Aspect
@Component
public class CreateTimeAspect {

    private static final Logger log = LoggerFactory.getLogger(CreateTimeAspect.class);

    @Around("@annotation(com.zxp.nowcodercommunity.annotation.AutoCreateTime)")
    public Object createTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("开始createTime字段填充");
        // 通过反射来获取运行时对象
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            // 获取第一个参数
            Object entity = args[0];
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("createTime".equals(field.getName())) { // 只处理 createTime 字段
                    field.setAccessible(true);
                    if (field.get(entity) == null) { // 只有 createTime 为空时才赋值
                        field.set(entity, LocalDateTime.now());
                    }
                    break;
                }
            }
        }
        return joinPoint.proceed();
    }
}
