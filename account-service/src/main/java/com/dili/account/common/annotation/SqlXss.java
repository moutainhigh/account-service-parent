package com.dili.account.common.annotation;

import com.dili.account.validator.SqlXssValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Auther: miaoguoxin
 * @Date: 2020/6/23 13:31
 * @Description: 校验sql字段
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = SqlXssValidator.class)
public @interface SqlXss {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
