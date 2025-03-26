package com.abs.utility.api.error.service;

import com.abs.utility.core.aop.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ErrorService {

    private final MessageSource messageSource;

    /**
     * 에러코드 조회
     */
    public Map<String, List<HashMap<String, Object>>> getErrorCode(String code) {

        // 에러코드 전체 List 배열로 가져온다.
        ErrorCode[] errorCodes = ErrorCode.values();

        // 오름차순 정렬을 위한 comparator
        Comparator<String> comparator = String::compareTo;
        Map<String, List<HashMap<String, Object>>> hashMap = new TreeMap<>(comparator);


        // 에러코드 enum 값을 꺼내가면서 비교
        for (ErrorCode errorCode : errorCodes) {
            // ERROR_로 시작하는 값만 해당
            if (errorCode.toString().split("\\.")[1].startsWith("ERROR")) {

                List<HashMap<String, Object>> errorCodeList = new ArrayList<>();
                HashMap<String, Object> errorCodeMap = new LinkedHashMap<>();

                // httpStatus 값을 기준으로 묶어준다.
                String key = errorCode.getStatus().value() + " (" + errorCode.getStatus().getReasonPhrase() + ")";

                // errorCode 의 code 값
                String codeNumber = errorCode.getCode();

                // code 값에 해당하는 message
                String message = messageSource.getMessage(errorCode.getCode(), null, Locale.KOREA);

                // 조회하려는 code 값이 있으면 해당 code 값만 조회
                if (code != null) {
                    if (codeNumber.equals(code)) {
                        errorCodeMap.put(codeNumber, message);
                        errorCodeList.add(errorCodeMap);
                        hashMap.put(key, errorCodeList);
                        break;
                    }
                } else {
                    if (hashMap.containsKey(key)) {
                        errorCodeMap.put(codeNumber, message);
                        hashMap.get(key).add(errorCodeMap);

                    } else {
                        errorCodeMap.put(codeNumber, message);
                        errorCodeList.add(errorCodeMap);
                        hashMap.put(key, errorCodeList);

                    }
                }
            }
        }
        return hashMap;
    }
}
