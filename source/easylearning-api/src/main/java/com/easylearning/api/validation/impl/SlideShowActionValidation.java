package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.SlideShowAction;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SlideShowActionValidation  implements ConstraintValidator<SlideShowAction,Integer> {

    private boolean allowNull;
    @Override
    public void initialize(SlideShowAction constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null && allowNull){
            return true;
        }
        if (!ObjectUtils.equals(LifeUniConstant.SLIDESHOW_NO_ACTION, integer) &&
                !ObjectUtils.equals(LifeUniConstant.SLIDESHOW_OPEN_URL, integer)){
            return false;
        }
        return true;
    }
}
