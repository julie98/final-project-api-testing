package stepDefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class userManagementSteps {
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

    @Test
    @When("login as admin")
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

    @Test
    @And("Create user")
    public void createUser() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "kate"); // todo
        payload.put("password", "abracadabra");
        payload.put("emailAddress", "kate@atlassian.com");
        payload.put("displayName", "Kate of Atlassian");
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
    public void getUserInformation() {
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .queryParam("username", "kate") // todo, need to delete
                .queryParam("includeDeleted", "false")
                .when()
                .basePath("/rest/api/2/user")
                .get();
        response.then()
                .log().body().statusCode(200);
    }

    @And("Deactive user")
    public void deactiveUser() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("active", false);
        Response response = RestAssured
                .given()
                .header(authHeader)
                .cookies(loginResponse.getCookies())
                .queryParam("username", "kate") // todo
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .basePath("/rest/api/2/user")
                .put();
    }

    @Test
    @Then("Try to login")
    public void tryToLogin() {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("username", "kate"); // todo
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
    public void viewWithFilter() {
        Response response = RestAssured
                .given()
                .header(authHeader)
                .queryParam("includeActive", false)
                .queryParam("includeInactive", true)
                .queryParam("username", "kate") // todo
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
                .body("[0].name", Matchers.equalTo("kate")); // todo
    }

    @And("Create another user")//may need if no other user inside
    public void createAnotherUser() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "kate2"); // todo, need to delete
        payload.put("password", "abracadabra");
        payload.put("emailAddress", "kate@atlassian.com");
        payload.put("displayName", "Kate of Atlassian");
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

    @Test
    @And("Assign user to groups")
    public void assignUser() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "kate2"); // todo
        Response response = RestAssured
                .given()
                .header(authHeader)
                .queryParam("groupname", "jira-software-users")
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
    public void viewUserWithGroupFilter() {
        Response response = RestAssured
                .given()
                .header(authHeader)
                .queryParam("groupname", "jira-software-users")
                .when()
                .basePath("/rest/api/2/group/member")
                .get();
        response.then()
                .assertThat()
                .body("values.name", Matchers.hasItem("kate2")); // todo

    }
    @AfterClass
    public void deleteUser(){
        Response response = RestAssured
                .given()
                .header(authHeader)
                .queryParam("username", "kate") // todo
                .when()
                .basePath("/rest/api/2/user")
                .delete();

        Response response1 = RestAssured
                .given()
                .header(authHeader)
                .queryParam("username", "kate2") // todo
                .when()
                .basePath("/rest/api/2/user")
                .delete();
    }
}
