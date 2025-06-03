package com.core.payment_gateway_service.dto;
import lombok.AccessLevel;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ObjectUtil {
    /**
     * Check annotation from field object
     *
     * @param field          field that have annotations
     * @param reqAnnotations list annotation
     * @return true if field from object matched with list annotation
     */
    public static boolean hasAnnotation(Field field, List<Class<?>> reqAnnotations) {
        return coreHasAnnotation(field, annotation -> reqAnnotations.contains(annotation.annotationType()));
    }

    public static String getValueOrNull(Object[] obj, int index) {
        if (obj != null && index >= 0 && index < obj.length) {
            Object value = obj[index];
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    /**
     * Check annotation from field object
     *
     * @param field         field that have annotations
     * @param reqAnnotation key annotation
     * @return true if field from object matched with annotation
     */
    public static boolean hasAnnotation(Field field, Class<?> reqAnnotation) {
        return coreHasAnnotation(field, annotation -> reqAnnotation.equals(annotation.annotationType()));
    }

    /**
     * Core logic check has annotation from field
     *
     * @param field  context field
     * @param filter filter logic
     * @return true if field matched with filter annotation using filter logic
     */
    private static boolean coreHasAnnotation(Field field, Predicate<? super Annotation> filter) {
        var matchedFieldAnnotation = Arrays.stream(field.getAnnotations())
                .filter(filter)
                .findFirst().orElse(null);

        return null != matchedFieldAnnotation;
    }

    /**
     * Get value from field using getter method
     *
     * @param obj   object of field
     * @param field field
     * @return value of object
     */
    public static Object getValueFromObject(Object obj, Field field) {
        Object value = null;
        try {
            value = obj.getClass()
                    .getMethod(constructMethodNameFromFieldName(field.getName(), "get"))
                    .invoke(obj);
        } catch (Exception ignored) {
            log.error("ignoring error getValueFromObject caused by: {}", ignored.getCause());
        }
        return value;
    }

    /**
     * Set value from object
     *
     * @param obj   context object
     * @param field destination field
     * @param value value
     */
    public static void setValueFromObject(Object obj, Field field, Object value) {
        try {
            setValueThrowError(obj, field, value);
        } catch (Exception ignored) {
            log.error("ignoring error setValueFromObject caused by: {}", ignored.getCause());
        }
    }

    /**
     * set value to field using setter method. if error will throw exception
     *
     * @param obj
     * @param field
     * @param value
     * @param <T>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static <T> void setValueThrowError(Object obj, Field field, T value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        obj.getClass()
                .getMethod(constructMethodNameFromFieldName(field.getName(), "set"), field.getType())
                .invoke(obj, value);
    }

    public static boolean setRawValueFromObjectWithStatus(Object obj, Field field, Object value) {
        try {
            if (value instanceof String sValue) {
                if (StringUtils.hasText(sValue)) {
                    if (field.getType().equals(BigDecimal.class)) {
                        setValueThrowError(obj, field, new BigDecimal(sValue));
                    } else if (field.getType().equals(BigInteger.class)) {
                        setValueThrowError(obj, field, new BigInteger(sValue));
                    } else if (field.getType().equals(Long.class)) {
                        setValueThrowError(obj, field, Long.parseLong(sValue));
                    } else if (field.getType().equals(Integer.class)) {
                        setValueThrowError(obj, field, Integer.parseInt(sValue));
                    } else if (field.getType().equals(Double.class)) {
                        setValueThrowError(obj, field, Double.parseDouble(sValue));
                    } else if (field.getType().equals(Float.class)) {
                        setValueThrowError(obj, field, Float.parseFloat(sValue));
                    } else if (field.getType().equals(Boolean.class)) {
                        setValueThrowError(obj, field, Boolean.parseBoolean(sValue));
                    } else {
                        setValueThrowError(obj, field, value);
                    }
                }
            } else {
                setValueThrowError(obj, field, value);
            }
            return true;
        } catch (Exception e) {
            log.error("error when set setValueFromObjectWithStatus caused by : {}", e.getCause());
            return false;
        }
    }

    /**
     * Get Field object from field by field name
     *
     * @param obj       context object
     * @param fieldName key field name
     * @return return Field object if exists. otherwise will return null
     */
    private static Field getMatchFieldFromObject(Object obj, String fieldName) {
        var fields = obj.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> fieldName.equalsIgnoreCase(field.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Set field value from object if field is exists in context object by field name
     *
     * @param obj                  context object
     * @param destinationFieldName destination field name
     * @param value                value
     */
    private static void setValueFromMatchFieldObject(Object obj, String destinationFieldName, Object value) {
        Field field = getMatchFieldFromObject(obj, destinationFieldName);
        if (null != field) {
            setValueFromObject(obj, field, value);
        }
    }


    public static Object getValueByFieldName(Object obj, String fieldName) {
        var fields = obj.getClass().getDeclaredFields();
        Object value = null;
        try {
            value = Arrays.stream(fields)
                    .filter(field -> field.getName().equals(fieldName))
                    .map(field -> getValueFromObject(obj, field))
                    .findFirst()
                    .orElse(null);
        } catch (Exception ignored) {
            log.error("ignoring error getValueByFieldName caused by: {}", ignored.getCause());
        }
        return value;
    }

    /**
     * Parse raw request from request to actual request dto
     *
     * @param actual actual object for search purpose
     * @param raw    raw object from request
     * @return false if matched field value from raw object cannot set the value to actual object
     */
    public static boolean parseRawRequest(Object actual, Object raw) {
        var rawFields = raw.getClass().getDeclaredFields();
        var actFields = actual.getClass().getDeclaredFields();
        for (var rField : rawFields) {
            Field actField = Arrays.stream(actFields)
                    .filter(aField -> rField.getName().equals(aField.getName()))
                    .findFirst().orElse(null);
            if (null == actField)
                continue;

            var rValue = getValueFromObject(raw, rField);
            if (!setRawValueFromObjectWithStatus(actual, actField, rValue))
                return false;
        }
        return true;
    }

    private static String constructMethodNameFromFieldName(String methodName, String firstWord) {
        return firstWord + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
    }

    public static <T> T getValue(Object[] data, int index) {
        return (T) data[index];
    }
}