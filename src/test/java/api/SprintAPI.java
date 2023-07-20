package api;

import constants.URL;
import entity.Issues;
import entity.Sprint;
import entity.SprintState;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import org.apache.http.protocol.ResponseDate;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class SprintAPI {
    static String username = "yulinhe";
    static String password = "Edison980522";
    static Header authHeader = new Header("Authorization", "Basic " + encodeCredentials(username, password));

    static Response response = SprintAPI.login();
    private static String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost:8080/rest/agile/1.0")
            .setContentType(ContentType.JSON)
            .build();

    //todo: temporary use, should integrate with other people
    public static Response login(){
        HashMap<String, String> payload = new HashMap<>();
        payload.put("username", "yulinhe");
        payload.put("password", "Edison980522");
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("http://localhost:8080/rest/auth/1/session");
    }

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
