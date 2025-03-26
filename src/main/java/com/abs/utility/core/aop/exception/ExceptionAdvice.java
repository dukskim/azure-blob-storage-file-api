package com.abs.utility.core.aop.exception;


import com.abs.utility.core.aop.exception.type.ErrorCode;
import com.abs.utility.core.base.dto.ApiResult;
import com.abs.utility.core.base.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionAdvice {

    private final MessageSource messageSource;



    /**
     * valid 관련 Advice
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(final MethodArgumentNotValidException e, WebRequest request) {
        String validMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return this.validExceptionHandler(e, request, validMessage);
    }


    /**
     * valid 관련 Advice
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationExceptionandler(final ConstraintViolationException e, WebRequest request) {
        String validMessage = e.getMessage();
        if(validMessage.indexOf(":") > -1) {
            validMessage = validMessage.substring(validMessage.indexOf(":") + 1);
            validMessage = validMessage.trim();
        }
        return this.validExceptionHandler(e, request, validMessage);
    }


    /**
     * Valild 체크 (Get Method Dto)
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> bindExceptionHandler(final BindException e, WebRequest request) {
        String validMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return this.validExceptionHandler(e, request, validMessage);
    }


    /**
     * 서비스시 발생하는 에러 Advice
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(final Exception e, WebRequest request) {
        ErrorCode exceptionType = ErrorCode.findException(e.getClass().getSimpleName());

        String path = request.getDescription(false);
        String exceptionKey = EncryptionUtil.md5(path + LocalDateTime.now());

        HashMap<String, Object> hashMap = errorHashMap(e, request, exceptionKey);
        ApiResult<Object> apiResult = new ApiResult<>();
        apiResult.setErrorCode(String.valueOf(exceptionType.getCode()));
        apiResult.setMsg(messageSource.getMessage(apiResult.getCode(), null, Locale.KOREAN));
        apiResult.setDataObj(hashMap);

        log.info("[Error] Exception   Code: {} , Msg: {}", exceptionType.getCode(), apiResult.getMsg());

        StringBuilder sb = new StringBuilder();
        for(StackTraceElement el : e.getStackTrace()){
            sb.append(request.getDescription(false)).append("-[").append(exceptionKey).append("] ").append(el.toString()).append("\n");
        }
        log.error(sb.toString());
        //e.printStackTrace();

        return ResponseEntity.status(exceptionType.getStatus()).body(apiResult);
    }


    /**
     * valid 공통 info
     */
    private ResponseEntity validExceptionHandler(final Exception e, WebRequest request, String validMessage) {
        ApiResult<Object> apiResult = new ApiResult<>();
        String[] validMessages = validMessage.split(":");
        String errorCode;
        String subMessage = "";
        String localeMessage;

        HashMap<String, Object> hashMap = errorHashMap(e, request);
        ErrorCode exceptionType;
        if(validMessages.length > 1) {
            exceptionType = ErrorCode.findException(validMessages[0].trim());
            errorCode = String.valueOf(exceptionType.getCode());
            subMessage = validMessages[1].trim();
        } else {
            exceptionType = ErrorCode.findException(validMessage.trim());
            errorCode = String.valueOf(exceptionType.getCode());
        }
        apiResult.setErrorCode(errorCode);

        if(exceptionType == ErrorCode.Exception) {
            apiResult.setMsg(messageSource.getMessage(apiResult.getCode(), null, Locale.KOREAN));
            hashMap.put("message","Server Error, 개발자에게 문의하세요.");
            apiResult.setDataObj(hashMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResult);
        } else {
            apiResult.setMsg("Bad Request");
        }

        localeMessage = messageSource.getMessage(apiResult.getCode(), null, request.getLocale());
        if(localeMessage.indexOf("{") > -1) {
            localeMessage = localeMessage.replace("{}", subMessage);
        } else {
            subMessage = subMessage != "" ? "(" + subMessage + ")" : "";
            localeMessage += subMessage;
        }

        hashMap.put("message",localeMessage);
        apiResult.setDataObj(hashMap);
        log.info("[Error] validException   Code: {} , Msg: {}", errorCode, apiResult.getMsg());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResult);
    }



    /**
     * 공통 info
     */
    public HashMap<String, Object> errorHashMap(Exception e, WebRequest request) {
        HashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        hashMap.put("path", request.getDescription(false));
        hashMap.put("error", e.getClass().getSimpleName());
        hashMap.put("time", LocalDateTime.now());
        hashMap.put("message",e.getMessage());

        return hashMap;
    }
    public HashMap<String, Object> errorHashMap(Exception e, WebRequest request, String exceptionKey) {
        HashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        String path = request.getDescription(false);

        hashMap.put("path", path);
        hashMap.put("error", e.getClass().getSimpleName());
        hashMap.put("time", LocalDateTime.now());
        hashMap.put("message",e.getMessage());
        if(exceptionKey != null) {
            hashMap.put("exceptionKey", exceptionKey);
        }
        return hashMap;
    }

}
