package com.undefined.laundry.utils.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.sql.Date;
import java.time.LocalDate;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TodayOrFuture.Validator.class)
public @interface TodayOrFuture {

    public String message() default "must be a today or future date";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<TodayOrFuture, Date> {

        @Override
        public boolean isValid(Date value, ConstraintValidatorContext context) {
            var now = LocalDate.now();
            var valueDate = value.toLocalDate();
            return !valueDate.isBefore(now);
        }
    }
}
