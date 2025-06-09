package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.PhoneNumber;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidation implements ConstraintValidator<PhoneNumber, String> {
    private boolean allowNull;

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(phoneNumber) && allowNull) {
            return true;
        }
        if (StringUtils.isBlank(phoneNumber) || !phoneNumber.matches(LifeUniConstant.PHONE_NUMBER_REGEX)) {
            return false;
        }
        return true;
    }
}
