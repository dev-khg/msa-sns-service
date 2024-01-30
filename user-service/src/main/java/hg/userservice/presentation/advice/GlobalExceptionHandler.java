package hg.userservice.presentation.advice;

import com.example.commonproject.exception.BadRequestException;
import com.example.commonproject.exception.InternalServerException;
import com.example.commonproject.exception.UnAuthorizedException;
import com.example.commonproject.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.commonproject.response.ApiResponse.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException e) {
        return badRequest().body(failure(e.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerException(InternalServerException e) {
        return internalServerError().body(failure(e.getMessage()));
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<ApiResponse<Void>> handleUnAuthorizedException(UnAuthorizedException e) {
        return new ResponseEntity<>(failure(e.getMessage()), UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime error occurred!", e);
        return internalServerError().body(failure("Sorry! Server has problem."));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleCheckException(Exception e) {
        log.error("Check error occurred!", e);
        return internalServerError().body(failure("Sorry! Server has problem."));
    }
}
