package api;

import constants.URL;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class UserAPI {
    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.USER.toString())
            .setContentType(ContentType.JSON)
            .build();
}