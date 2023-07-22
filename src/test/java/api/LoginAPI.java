package api;

import constants.URL;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class LoginAPI {
    private final RequestSpecification requestSpec;

    public LoginAPI(String adminUsername, String adminPassword) {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(URL.AUTH.toString())
                .addHeader("Authorization", "Basic " + encodeCredentials(adminUsername, adminPassword))
                .setContentType(ContentType.JSON)
                .build();
    }

    public static String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public Response loginUser(String username, String password){
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
        return given(requestSpec)
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/1/session");
    }

    public Response getCurrentUser() {
        return given(requestSpec)
                .contentType(ContentType.JSON)
                .when()
                .get("/1/session");
    }
}
