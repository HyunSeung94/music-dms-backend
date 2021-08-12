package com.mesim.sc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Annotation {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface NoAspect {}

}
