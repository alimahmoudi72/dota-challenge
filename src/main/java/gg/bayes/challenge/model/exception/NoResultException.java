package gg.bayes.challenge.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No result matched the given query.")
public class NoResultException extends RuntimeException {
}
