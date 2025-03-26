package com.abs.utility.core.aop.exception;


import com.abs.utility.core.base.dto.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionAdvice {

    private final MessageSource messageSource;

    private final ExceptionAdvice exceptionAdvice;


    /**
     * Fantoo 서비스에서 정의한 에러코드 Advice
     */
    @ExceptionHandler(FmException.class)
    public ResponseEntity fantooHandler(final FmException e, WebRequest request) {
        ApiResult<Object> apiResult = new ApiResult<>();
        apiResult.setErrorCode(String.valueOf(e.getErrorCode().getCode()));

        String message = messageSource.getMessage(apiResult.getCode(), null, Locale.KOREAN);
        if(!ObjectUtils.isEmpty(e.getAddMessage())) {
            if(message.indexOf("{}") > -1) {
                message = message.replace("{}", e.getAddMessage());
            } else {
                message += " (" + e.getAddMessage() + ")";
            }
        }
        apiResult.setMsg(message);
        HashMap<String, Object> hashMap = exceptionAdvice.errorHashMap(e, request, null);
        apiResult.setDataObj(hashMap);

        log.info("[Error] Exception   Code: {} , Msg: {}", e.getErrorCode().getCode(), apiResult.getMsg());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(apiResult);
    }



}
