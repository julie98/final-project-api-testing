package api;

import entity.Issues;
import entity.Sprint;
import entity.SprintState;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class SprintAPI {
    static String adminUsername = "hzhu64";
    static String adminPassword = "Zhy123321!";
    static Header authHeader = new Header("Authorization", "Basic " + encodeCredentials(adminUsername, adminPassword));

//    static Response response = SprintAPI.login();
    static Response response = new LoginAPI(adminUsername, adminPassword).loginUser(adminUsername, adminPassword);
    private static String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost:8080/rest/agile/1.0")
            .setContentType(ContentType.JSON)
            .build();


    public static Response createSprint(Sprint sprint){
        return given()
                .spec(requestSpec)
                .cookies(response.getCookies())
                .header(authHeader)
                .body(sprint)
                .post("/sprint");
    }

    public static Response addIssues(Issues issues, int sprintId){
        return given()
                .spec(requestSpec)
                .cookies(response.getCookies())
                .header(authHeader)
                .body(issues)
                .post("/sprint/" + sprintId + "/issue");
    }

    public static Response changeState(SprintState sprintState, int sprintId){
        return given()
                .spec(requestSpec)
                .cookies(response.getCookies())
                .header(authHeader)
                .body(sprintState)
                .put("/sprint/" + sprintId);
    }

    public static Response downloadChart(int boardId){
        return given()
                .cookies(response.getCookies())
                .header(authHeader)
                .queryParam("rapidViewId", boardId)
                .when()
                .get("http://localhost:8080/rest/greenhopper/1.0/rapid/charts/velocity");
    }

    public static Response viewIssuesInSprint(int sprintId){
        return given()
                .cookies(response.getCookies())
                .header(authHeader)
                .get("http://localhost:8080/rest/agile/1.0/sprint/" + sprintId + "/issue");
    }
}
