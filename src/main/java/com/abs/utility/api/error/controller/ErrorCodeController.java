package com.abs.utility.api.error.controller;

import com.abs.utility.api.error.service.ErrorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Api(tags = "Error Code API")
@Slf4j
public class ErrorCodeController {

    private final ErrorService errorService;


    @GetMapping("common/error/code")
    @ApiOperation(value = "에러코드 조회", notes = "error code 조회. 코드번호 입력시 해당 에러코드 조회, 코드번호 없을시 전체 에러코드 조회")
    public ResponseEntity<Map<String, List<HashMap<String, Object>>>> getErrorCode(
            @ApiParam(value = "에러코드 번호", example = "") @RequestParam(value = "code", required = false) String code) {

        Map<String, List<HashMap<String, Object>>> hashMap = errorService.getErrorCode(code);


        return ResponseEntity.ok().body(hashMap);
    }
}
