package PTPMUD.HoTroSinhVien.Exception;

import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseObject> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(
                new ResponseObject("failed", exception.getMessage(), "")
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseObject> handleRuntime(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed", exception.getMessage(), "")
        );
    }
}
