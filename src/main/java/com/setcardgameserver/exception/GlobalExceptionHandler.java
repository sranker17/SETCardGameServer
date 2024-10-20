package com.setcardgameserver.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String DESCRIPTION = "description";

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        log.error("BadCredentialsException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The username or password is incorrect");
        return errorDetail;
    }

    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatusException(AccountStatusException exception) {
        log.error("AccountStatusException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The account is locked");
        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        log.error("AccessDeniedException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "You are not authorized to access this resource");
        return errorDetail;
    }

    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException exception) {
        log.error("SignatureException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The JWT signature is invalid");
        return errorDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        log.error("ExpiredJwtException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
        errorDetail.setTitle("Token expired");
        errorDetail.setProperty(DESCRIPTION, "The JWT token has expired");
        return errorDetail;
    }

    @ExceptionHandler(InvalidGameException.class)
    public ProblemDetail handleInvalidGameException(InvalidGameException exception) {
        log.error("InvalidGameException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The game is invalid");
        return errorDetail;
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ProblemDetail handleGameNotFoundException(GameNotFoundException exception) {
        log.error("GameNotFoundException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Game not found");
        return errorDetail;
    }

    @ExceptionHandler(InvalidScoreException.class)
    public ProblemDetail handleInvalidScoreException(InvalidScoreException exception) {
        log.error("InvalidScoreException occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Invalid score");
        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleSecurityMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("An MethodArgumentNotValidException occurred {}", exception.getMessage());
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), errors.toString());
        errorDetail.setProperty(DESCRIPTION, "Validation error");
        return errorDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        log.error("An exception occurred {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Unknown internal server error");
        return errorDetail;
    }
}
