package com.kerry.helper.util;

import com.kerry.helper.annotation.ExcelReadField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * **********书山有路勤为径**********
 *
 * @author k1rry
 * @date 2020/7/11
 * **********学海无涯苦作舟**********
 */
public class ClassUtils {

    private ClassUtils() { }

    public static Map<String, String> fieldAnnotation(Class clazz, Class annotationClass) {
        final Map<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length != 0) {
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(annotationClass);
                if (annotation == null) {
                    continue;
                }
                if (annotation.annotationType() == ExcelReadField.class) {
                    ExcelReadField readFieldAnn = (ExcelReadField) annotation;
                    map.put(readFieldAnn.value(), field.getName());
                }
            }
        }
        return map;
    }

}
