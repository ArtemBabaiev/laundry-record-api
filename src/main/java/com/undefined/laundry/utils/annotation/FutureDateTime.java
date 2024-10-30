package com.undefined.laundry.utils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalTime;

import com.undefined.laundry.model.request.HasDateTime;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureDateTime.Validator.class)
public @interface FutureDateTime {

	public String message() default "date time should be future";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};

	class Validator implements ConstraintValidator<FutureDateTime, HasDateTime> {

		@Override
		public boolean isValid(HasDateTime value, ConstraintValidatorContext context) {
			LocalDate nowDate = LocalDate.now();
			if (value.getDate().isBefore(nowDate)) {
				return false;
			}
			if (value.getDate().equals(nowDate)) {
				LocalTime nowTime = LocalTime.now();
				return value.getTime().getHour() > nowTime.getHour();
			}
			return true;
		}
	}
}
