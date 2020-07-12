package com.kerry.helper.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **********书山有路勤为径**********
 *
 * @author k1rry
 * @date 2020/7/11
 * **********学海无涯苦作舟**********
 */
@Service
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelReadField {

    String value() default "";

}
