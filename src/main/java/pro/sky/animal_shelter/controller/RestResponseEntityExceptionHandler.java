package pro.sky.animal_shelter.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pro.sky.animal_shelter.exception.*;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = { NullPointerException.class })
    protected ResponseEntity<Object> handleNullPointer(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Null pointer exception occurred";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({DogNotFoundException.class, CatNotFoundException.class, ReportNotFoundException.class, UserNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        String bodyOfResponse;

        if (ex instanceof DogNotFoundException) {
            bodyOfResponse = "Такая собака не найдена";
        } else if (ex instanceof CatNotFoundException) {
            bodyOfResponse = "Такая кошка не найдена";
        } else if (ex instanceof ReportNotFoundException) {
            bodyOfResponse = "Такой отчет не найден";
        } else if (ex instanceof UserNotFoundException) {
            bodyOfResponse = "Такой пользователь не найден";
        } else {
            bodyOfResponse = "Не найдено";
        }

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UploadPhotoException.class)
    protected ResponseEntity<Object> handleUploadPhoto(UploadPhotoException ex, WebRequest request) {
        String bodyOfResponse = "Фото не загружается";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NO_CONTENT, request);
    }
}
