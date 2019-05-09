package com.example.demo.interceptor;

import com.example.demo.entity.CodeMsg;
import com.example.demo.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author jinbin
 * @date 2018-07-08 22:37
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        //LOGGER.info(e.getMessage()+"<============");
        return Result.error(CodeMsg.SERVER_ERROR.fillArgs( e.getMessage()));
    }
}
