package stepDef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class groupManageStep {
    String baseUrl="http://localhost:8080";
    String username = "Yusheng Zheng";
    String password = "123456789zys+";
    Response loginResponse;
    Header authHeader = new Header("Authorization", "Basic " + encodeCredentials(username, password));
    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = baseUrl;
    }
    private static String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
    @Test
    @When("login as admin for group")
    public void iGetTheDetailedInformationOfTheUser() {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);
        loginResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .basePath("/rest/auth/1/session")
                .post();

        loginResponse.then()
                .log().body().statusCode(200);
    }
    @Test
    @And("create group")
    public void createGroup(){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "group2");
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .basePath("/rest/api/2/group")
                .post();

        response.then()
                .log().body().statusCode(201);
    }
    @Test
    @And("add user to group")
    public void addUserToGroup(){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "Sage");
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .queryParam("groupname","group2")
                .body(payload)
                .when()
                .basePath("/rest/api/2/group/user?")
                .post();

        response.then()
                .log().body().statusCode(201);
    }
    @Test
    @And("get user with group filter")
    public void getUserWithFitlter(){
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .queryParam("groupname","group2")
                .when()
                .basePath("/rest/api/2/group/member")
                .get();

        response.then()
                .log().body().statusCode(200);
    }
}
