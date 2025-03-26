package com.abs.utility.core.aop.exception;


import com.abs.utility.core.aop.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FmException extends RuntimeException {

    private ErrorCode errorCode;

    private String addMessage;


    public FmException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
