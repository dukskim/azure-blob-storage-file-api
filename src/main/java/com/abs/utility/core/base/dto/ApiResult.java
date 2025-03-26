package com.abs.utility.core.base.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResult<T> {


    final public static String RESULT_CODE_OK = "200";
    final public static String OK = "OK";

    private String code;
    private String msg;
    private T dataObj;


    /**
     * Error Code 명확한 인지를 위해 사용.
     * @param code
     */
    public void setErrorCode(String code) {
        this.code = code;
    }

    public ApiResult(String code) {
        this.code = code;
    }

    public ApiResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
