package gg.bayes.challenge.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "The operation failed.")
public class InternalServerErrorException extends RuntimeException {
}
