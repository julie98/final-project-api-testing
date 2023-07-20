package constants;

import utils.ApplicationProperties;

public enum URL {
    USER("/user");


    private final String url;

    URL(String path) {
        this.url = ApplicationProperties.get("baseURI") + path;
    }

    public String toString() {
        return url;
    }
}
