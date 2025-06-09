/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easylearning.api.exception;

import lombok.Getter;

/**
 *
 * @author cht
 */
@Getter
public class NotFoundException extends RuntimeException{
    private final String code;
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message){
        super(message);
        this.code = null;

    }
    public NotFoundException(String message, String code) {
        super(message);
        this.code = code;

    }
}