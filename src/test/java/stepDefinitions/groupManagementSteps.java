package stepDefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;

import java.util.HashMap;


public class groupManagementSteps {
    String baseUrl = "http://localhost:8080";
    String adminUsername = "hzhu64";
    String adminPassword = "Zhy123321!";
    Response loginResponse;
    Header authHeader = new Header("Authorization", "Basic " + encodeCredentials(adminUsername, adminPassword));

    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = baseUrl;
    }

    private static String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    @When("login as admin for group")
    public void iGetTheDetailedInformationOfTheUser() {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("username", adminUsername);
        payload.put("password", adminPassword);
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


    @And("create group")
    public void createGroup() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "group2"); // todo
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


    @And("add user to group")
    public void addUserToGroup() {
        HashMap<String, Object> payload = new HashMap<>();
        //custom name here
        payload.put("name", "julie"); // todo
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .queryParam("groupname", "group2") // todo
                .body(payload)
                .when()
                .basePath("/rest/api/2/group/user?")
                .post();

        response.then()
                .log().body().statusCode(201);
    }


    @And("get user with group filter")
    public void getUserWithFitlter() {
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .queryParam("groupname", "group2") // todo
                .when()
                .basePath("/rest/api/2/group/member")
                .get();

        response.then()
                .log().body().statusCode(200);
    }

    @AfterClass
    public void delete(){
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .queryParam("groupname", "group2") // todo
                .when()
                .basePath("/rest/api/2/group")
                .delete();
    }
}
