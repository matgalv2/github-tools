package io.github.matgalv2.githubtools.error;

import lombok.Getter;

@Getter
public abstract class ApplicationError extends RuntimeException {
    private final Error error;

    protected ApplicationError(Error error) {
        super(error.getMessage());
        this.error = error;
    }
}
