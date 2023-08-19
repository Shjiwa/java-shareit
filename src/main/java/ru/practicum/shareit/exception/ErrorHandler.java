package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ExceptionResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(final BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleConflictException(final ConflictException e) {
        log.error(e.getMessage(), e);
        return new ExceptionResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleUnsupportedStatus(final MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, "Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleInternalServerErrorException(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleForbiddenException(final ForbiddenException e) {
        log.error(e.getMessage(), e);
        return new ExceptionResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }
}