package com.example.demo.interceptor;

import com.example.demo.entity.CodeMsg;
import com.example.demo.entity.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author jinbin
 * @date 2018-07-08 22:37
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        return Result.error(CodeMsg.SERVER_ERROR.fillArgs(false,e.getMessage()));
    }
}
