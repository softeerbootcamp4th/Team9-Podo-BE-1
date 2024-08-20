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
        log.warn("ARRIVAL-001> 요청 URI: " + request.getRequestURI() + ", 에러 메세지: " + e.getMessage());
        return new CommonResponse<>(ErrorCode.PHONENUM_EXISTS_ERROR);
    }

    @ExceptionHandler(AsyncRequestExecuteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<?> asyncRequestExecuteException(AsyncRequestExecuteException e, HttpServletRequest request) {
        log.warn("ARRIVAL-002> 요청 URI: " + request.getRequestURI() + ", 에러 메세지: " + e.getMessage());
        return new CommonResponse<>(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DailyQuizNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<?> dailyQuizNotFoundException(DailyQuizNotExistsException e, HttpServletRequest request) {
        log.warn("ARRIVAL-003> 요청 URI: " + request.getRequestURI() + ", 에러 메세지: " + e.getMessage());
        return new CommonResponse<>(ErrorCode.QUIZ_NOT_FOUND);
    }

    @ExceptionHandler(EventClosedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponse<?> eventClosedException(EventClosedException e, HttpServletRequest request) {
        log.warn("ARRIVAL-004> 요청 URI: " + request.getRequestURI() + ", 에러 메세지: " + e.getMessage());
        return new CommonResponse<>(ErrorCode.EVENT_CLOSED);
    }
}
