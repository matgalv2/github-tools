package io.github.matgalv2.githubtools.error;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private final Error error;
    public ServiceException(Error error) {
        super(error.getMessage());
        this.error = error;
    }
}
