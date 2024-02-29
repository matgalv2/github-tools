package io.github.matgalv2.githubtools.error;

public class CouldNotConnectToGithubApi extends ApplicationError{

    public CouldNotConnectToGithubApi(String message) {
        super(new Error(500, message));
    }
}
