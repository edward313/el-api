package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.ReviewStar;

import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import javax.validation.ConstraintValidator;

public class ReviewStarValidation implements ConstraintValidator<ReviewStar,Integer> {
    private boolean allowNull;

    @Override
    public void initialize(ReviewStar constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer reviewStar, ConstraintValidatorContext constraintValidatorContext) {
        if(reviewStar == null && allowNull) {
            return true;
        }
        if(!Objects.equals(reviewStar, LifeUniConstant.REVIEW_ONE_STAR) &&
                !Objects.equals(reviewStar, LifeUniConstant.REVIEW_TWO_STAR) &&
                !Objects.equals(reviewStar, LifeUniConstant.REVIEW_THREE_STAR) &&
                !Objects.equals(reviewStar, LifeUniConstant.REVIEW_FOUR_STAR) &&
                !Objects.equals(reviewStar, LifeUniConstant.REVIEW_FIVE_STAR)) {
            return false;
        }
        return true;
    }
}
