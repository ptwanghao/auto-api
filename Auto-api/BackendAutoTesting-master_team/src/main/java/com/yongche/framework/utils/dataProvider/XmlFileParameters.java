package com.yongche.framework.utils.dataProvider;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation is used to a test method(stamped with @Test) and let the data provider know where to load xml files
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlFileParameters {
    String path();
    boolean recursive() default false;
}
