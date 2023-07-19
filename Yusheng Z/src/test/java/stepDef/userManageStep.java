package stepDef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.*;

public class userManageStep {
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
    @When("login as admin")
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
    @And("Create user")
    public void createUser(){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "charlieThird");
        payload.put("password", "abracadabra");
        payload.put("emailAddress", "charlie@atlassian.com");
        payload.put("displayName", "Charlie of Atlassian");
        List<String> applicationKeys = new ArrayList<>();
        applicationKeys.add("jira-core");
        payload.put("applicationKeys", applicationKeys);
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .basePath("/rest/api/2/user")
                .post();

        response.then()
                .log().body().statusCode(201);
    }
    @Then("Get user information")
    public void getUserInformation(){
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .queryParam("username","charlie")
                .queryParam("includeDeleted","false")
                .when()
                .basePath("/rest/api/2/user")
                .get();
        response.then()
                .log().body().statusCode(200);
    }
    @And("Deactive user")
    public void deactiveUser(){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("active", false);
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .queryParam("username","charlie")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .basePath("/rest/api/2/user")
                .put();
    }
    @Test
    @Then("Try to login")
    public void tryToLogin(){
        HashMap<String, String> payload = new HashMap<>();
        payload.put("username", "charlie");
        payload.put("password", "abracadabra");
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .basePath("/rest/auth/1/session")
                .post();

        response.then()
                .log().body().statusCode(401);
        response.then()
                .assertThat()
                .body("errorMessages", Matchers.contains("Login failed"));
    }
    @Test
    @And("View User with filter")
    public void viewWithFilter(){
        Response response = RestAssured
                .given()
                .header(authHeader)
                .queryParam("includeActive",false)
                .queryParam("includeInactive",true)
                .queryParam("username","charlie")
                .when()
                .basePath("rest/api/2/user/search")
                .get();

        response.then()
                .log().body().statusCode(200);
        response.then()
                .assertThat()
                .body("[0].active", Matchers.equalTo(false));
        response.then()
                .assertThat()
                .body("[0].name", Matchers.equalTo("charlie"));
    }
    @And("Create another user")//may need if no other user inside
    public void createAnotherUser(){

    }
    @Test
    @And("Assign user to groups")
    public void assignUser(){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "charlieThird");
        Response response = RestAssured
                .given()
                .header(authHeader)
                .queryParam("groupname","jira-software-users")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .basePath("/rest/api/2/group/user")
                .post();
        response.then()
                .log().body().statusCode(201);
    }
    @Test
    @And("view user in result after group filter")
    public void viewUserWithGroupFilter(){
        Response response = RestAssured
                .given()
                .header(authHeader)
                .queryParam("groupname","jira-software-users")
                .when()
                .basePath("/rest/api/2/group/member")
                .get();
        response.then()
                .assertThat()
                .body("values.name", Matchers.hasItem("charlieThird"));

    }
}
