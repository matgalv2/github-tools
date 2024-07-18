package io.github.matgalv2.githubtools.error;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {
    private final Error error;

    protected ApplicationException(Error error) {
        super(error.getMessage());
        this.error = error;
    }
}
