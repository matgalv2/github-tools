package io.github.matgalv2.githubtools.error;

public class CouldNotConnectToGithubApi extends ApplicationException {

    public CouldNotConnectToGithubApi(String message) {
        super(new Error(503, message));
    }
}
