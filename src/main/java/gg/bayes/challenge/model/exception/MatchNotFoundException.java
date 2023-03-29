package gg.bayes.challenge.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Match with given id not found.")
public class MatchNotFoundException extends RuntimeException {
}
