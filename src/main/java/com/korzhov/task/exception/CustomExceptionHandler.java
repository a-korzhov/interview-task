package com.korzhov.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.korzhov.task.exception.ErrorResponseEntityBuilder.buildResponseEntity;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    // 204 – if the transaction is older than 60 seconds
    @ExceptionHandler(value = {TransactionExpiredException.class})
    protected ResponseEntity<Object> handleNoContent(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.NO_CONTENT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // 422 – if the transaction date is in the future
    @ExceptionHandler(value = {TransactionInFutureException.class})
    protected ResponseEntity<Object> handleUnprocessableEntity(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    // Additional exception for empty statistic result
    @ExceptionHandler(value = {StatisticIsEmptyException.class})
    protected ResponseEntity<Object> handleNoContentStatistic(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.NO_CONTENT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }
}
