package io.github.matgalv2.githubtools.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
    private int status;
    private String message;
}
