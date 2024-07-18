package io.github.matgalv2.githubtools.error;

import lombok.Getter;

@Getter
public class ServiceException extends ApplicationException{
    public ServiceException(Error error) {
        super(error);
    }
}
