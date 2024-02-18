package io.github.matgalv2.githubtools.common;

public enum ErrorMessage {
    COULD_NOT_GET_BRANCHES("Could not get branches for repository %s due to error: %s"),
    COULD_NOT_CONNECT_TO("Could not connect to %s");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String value(){
        return this.message;
    }
}
