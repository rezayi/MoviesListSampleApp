package sample.mrezaei.movies.configurations;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sample.mrezaei.movies.exceptions.InputParamException;
import sample.mrezaei.movies.exceptions.MovieNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles probable exceptions
 */
@Hidden
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(InputParamException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(InputParamException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(MovieNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
