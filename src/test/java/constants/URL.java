package constants;

import utils.ApplicationProperties;

public enum URL {

    AUTH("/auth"),
    PROJECT_ROLE("/api/2/role"),
    PROJECT("/api/2/project"),
    PERMISSION_SCHEME("/api/2/permissionscheme");


    private final String url;

    URL(String path) {
        this.url = ApplicationProperties.get("baseURI") + path;
    }

    public String toString() {
        return url;
    }
}
