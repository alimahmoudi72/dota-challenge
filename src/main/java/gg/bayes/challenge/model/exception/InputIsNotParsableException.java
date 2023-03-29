package gg.bayes.challenge.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Could not parse any entry.")
public class InputIsNotParsableException extends RuntimeException{
}
