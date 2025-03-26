package com.abs.utility.core.aop.exception.type;


import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@ToString
public enum ErrorCode {

    /**
     * ********************************************************************************************************
     * Exception Start
     */

    /**
     * System Exception
     */
      HttpRequestMethodNotSupportedException(HttpStatus.NOT_FOUND, "404")
    , RuntimeException(HttpStatus.BAD_REQUEST, "400")
    , IllegalArgumentException(HttpStatus.BAD_REQUEST, "400")
    , AccessDeniedException(HttpStatus.UNAUTHORIZED, "401")
    , NoHandlerFoundException(HttpStatus.NOT_FOUND, "404")
    , NestedServletException(HttpStatus.NOT_FOUND, "404")
    , ResourceNotFoundException(HttpStatus.NOT_FOUND, "404")
    , ParseException(HttpStatus.BAD_REQUEST, "400")
    , ArrayIndexOutOfBoundsException(HttpStatus.BAD_REQUEST, "400")
    , NoSuchMessageException(HttpStatus.BAD_REQUEST, "400")
    , MailException(HttpStatus.BAD_REQUEST, "400")
    , NullPointerException(HttpStatus.BAD_REQUEST, "400")
    //, MethodArgumentNotValidException(HttpStatus.BAD_REQUEST, 400)
    , MethodArgumentTypeMismatchException(HttpStatus.NOT_FOUND, "404")
    , RequestRejectedException(HttpStatus.NOT_FOUND, "404")

    /**
     * Sql Exception
     */
    , BadSqlGrammarException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , InvalidResultSetAccessException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DuplicateKeyException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DataIntegrityViolationException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DataAccessResourceFailureException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , CannotAcquireLockException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DeadlockLoserDataAccessException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , CannotSerializeTransactionException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , SQLSyntaxErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "500") //Not error catch!?
    , DataAccessException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , SQLNonTransientException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , SQLException(HttpStatus.INTERNAL_SERVER_ERROR, "500")

    /**
     * 외 모든 Exception
     */
    , Exception(HttpStatus.INTERNAL_SERVER_ERROR, "500")


    , HttpMessageNotReadableException(HttpStatus.BAD_REQUEST, "400")


    /**
     * Exception END
     * ********************************************************************************************************
     */







    /**
     * ********************************************************************************************************
     * Custom Exception Start
     * - 하기 경로의 errorCode와 매칭하여 사용
     * - resources/message/Resource Bundle 'messages'/messages_*.properties
     */

    /**
     * 파일 업로드 exception = fm
     */
    , ERROR_FM1011(HttpStatus.INTERNAL_SERVER_ERROR,"FM1011")
    , ERROR_FM1012(HttpStatus.INTERNAL_SERVER_ERROR,"FM1012")
    , ERROR_FM1013(HttpStatus.INTERNAL_SERVER_ERROR,"FM1013")
    , ERROR_FM1001(HttpStatus.BAD_REQUEST,"FM1001")
    , ERROR_FM1003(HttpStatus.BAD_REQUEST,"FM1003")
    , ERROR_FM1005(HttpStatus.BAD_REQUEST,"FM1005")
    , ERROR_FM1006(HttpStatus.BAD_REQUEST,"FM1006")
    ;




    /**
     * Custom Exception End
     * ********************************************************************************************************
     */


    private final HttpStatus status;
    private String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public static ErrorCode findException(String exceptionName) {
        return Arrays.stream(ErrorCode.values())
                .filter(s -> s.name().equals(exceptionName))
                .findAny()
                .orElse(ErrorCode.Exception);
    }



}
