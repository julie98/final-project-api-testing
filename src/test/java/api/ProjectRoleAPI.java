package api;

import constants.URL;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.Header;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;

public class ProjectRole {
    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.PROJECT_ROLE.toString())
            .setContentType(ContentType.JSON)
            .build();

//    private static final String username_Team_Lead = "julie";
//    private static final String password_Team_Lead = "12345";
//
//    private static Header authHeader_Team_Lead = new Header("Authorization", "Basic " +
//            encodeCredentials(username_Team_Lead, password_Team_Lead));

    private static String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public Response createProjectRole(String name, String description) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("description", description);


        return given(requestSpec)
                .body(requestBody)
                .when()
                .post();
    }

    public Response getProjectRole
}
