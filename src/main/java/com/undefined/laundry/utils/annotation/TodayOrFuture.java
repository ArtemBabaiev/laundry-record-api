package com.undefined.laundry.utils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TodayOrFuture.Validator.class)
public @interface TodayOrFuture {

	public String message() default "must be a today or future date";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};

	class Validator implements ConstraintValidator<TodayOrFuture, LocalDate> {

		@Override
		public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
			var now = LocalDate.now();
			return !value.isBefore(now);
		}
	}
}
