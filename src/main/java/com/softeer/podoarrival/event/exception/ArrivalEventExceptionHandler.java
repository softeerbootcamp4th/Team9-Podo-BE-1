package com.softeer.podoarrival.event.exception;


import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.common.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ArrivalEventExceptionHandler {

    @ExceptionHandler(ExistingUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<?> existingUserException(ExistingUserException e, HttpServletRequest request) {
        log.warn("ARRIVALAPPLICATION-001> 요청 URI: " + request.getRequestURI() + ", 에러 메세지: " + e.getMessage());
        return new CommonResponse<>(ErrorCode.PHONENUM_EXSIST_ERROR);
    }

}
