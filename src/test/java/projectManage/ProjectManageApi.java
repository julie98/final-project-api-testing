package projectManage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectManageApi {

    private int ProjectID;
    private int RoleID;
    private String username;
    private Response response;
    private int issueID;
    private final String baseURI = "http://localhost:8080";

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost:8080")
            .setBasePath("/rest/api/2/project")
            .setContentType(ContentType.JSON)
            .build();

    String auth = "Basic " + Base64.getEncoder().encodeToString("admin:12345".getBytes());


    @When("I login with {string} and {string}")
    public void login(String username, String password) {
        Map<String, String> userinfo = new HashMap<>();
        userinfo.put("username", username);
        userinfo.put("password", password);

        this.response = given()
                .contentType(ContentType.JSON)
                .body(userinfo)
                .when()
                .post("http://localhost:8080/rest/auth/1/session")
                .then()
                .log().body()
                .statusCode(200)
                .extract().response();
    }

    @And("I create a new project with {string}, {string}, and {string}")
    public void CreateProject(String key, String name, String description) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("key", key);
        requestBody.put("name", name);
        requestBody.put("projectTypeKey", "business");
        requestBody.put("projectTemplateKey", "com.atlassian.jira-core-project-templates:jira-core-task-management");
        requestBody.put("description", description);
        requestBody.put("lead", "admin");
        requestBody.put("url", "http://atlassian.com");
        requestBody.put("assigneeType", "PROJECT_LEAD");
        requestBody.put("avatarId", 10200);

        Response response = given()
                .spec(requestSpec)
                .header("Authorization", auth)
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post()
                .then()
                .log()
                .ifError()
                .statusCode(201)
                .extract().response();

        this.ProjectID = response.path("id");
    }

    @Then("I should see the detail of the project")
    public void getProject() {
        Response response = given()
                .header("Authorization", auth)
                .when()
                .get(baseURI + "/rest/api/2/project/" + this.ProjectID)
                .then()
                .extract().response();
        String projectID = response.path("id");
        assertThat(projectID, equalTo(Integer.toString(this.ProjectID)));
        System.out.println(response.asString());
    }
    @When("I create new user with {string}, {string},{string} and {string}")
    @Test
    public void createUser( String username, String userPassword, String emailAddress, String displayName) {
        JSONObject userBody = new JSONObject();
        userBody.put("name", username);
        userBody.put("password", userPassword);
        userBody.put("emailAddress", emailAddress);
        userBody.put("displayName", displayName);
        List<String> applicationKeys = new ArrayList<>();
        applicationKeys.add("jira-core");
        userBody.put("applicationKeys", applicationKeys);

        Response userRes = given()
                .header("Authorization", auth)
                .cookies(this.response.getCookies())
                .contentType(ContentType.JSON)
                .body(userBody.toString())
                .when()
                .post(baseURI + "/rest/api/2/user")
                .then()
                        .extract().response();

        this.username = userRes.path("name");
        System.out.println(this.username);
    }

    @And("I create project role with {string} and {string}")
    public void createProjectRole(String roleName, String roleDescription) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", roleName);
        requestBody.put("description", roleDescription);

        Response response = given()
                .header("Authorization", auth)
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post(baseURI + "/rest/api/2/role")
                .then()
                .log().body()
                .statusCode(200)
                .extract().response();

        this.RoleID = response.path("id");
    }

    @And("I add {string} to the project role.")
    public void addProjectRole(String username){
        ArrayList<String> usernames = new ArrayList<>();
        usernames.add(username);
        JSONObject roleBody = new JSONObject();
        roleBody.put("user", username);
        given()
                .header("Authorization", auth)
                .contentType(ContentType.JSON)
                .body(roleBody.toString())
                .when()
                .post(baseURI + "/rest/api/2/project/" + this.ProjectID + "/role/" + this.RoleID)
                .then()
                .log()
                .ifError();
    }

    @When("I create a new issue with {string} and {string}")
    public void createEpicIssue(String summary, String issueDescription){
        JSONObject issueType = new JSONObject();
        issueType.put("name", "Task");

        JSONObject project = new JSONObject();
        project.put("id", ProjectID);

        JSONObject fields = new JSONObject();
        fields.put("project", project);
        fields.put("summary", summary);
        fields.put("description", issueDescription);
        fields.put("issuetype", issueType);

        JSONObject mainObject = new JSONObject();
        mainObject.put("fields", fields);

        String issueID = given()
                .header("Authorization", auth)
                .contentType(ContentType.JSON)
                .body(mainObject.toString())
                .when()
                .post(baseURI + "/rest/api/2/issue")
                .then()
                .log()
                .ifError()
                .extract().response().path("id");
        this.issueID = Integer.parseInt(issueID);

    }

    @Then("I should see the detail of the issue")
    public void getIssue(){
        given()
                .header("Authorization", auth)
                .when()
                .get(baseURI + "/rest/api/2/issue/" + this.issueID)
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    public void getWorkflow(){
        given()
                .header("Authorization", auth)
                .when()
                .get(baseURI + "/rest/api/2/workflow")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    public void updateProject(){
        JSONObject requestBody = new JSONObject();
        requestBody.put("key", "TST");
        requestBody.put("name", "Test");
        requestBody.put("projectTypeKey", "business");
        requestBody.put("projectTemplateKey", "com.atlassian.jira-core-project-templates:jira-core-task-management");
        requestBody.put("description", "This is a test project");
        requestBody.put("lead", "admin");
        requestBody.put("url", "http://atlassian.com");
        requestBody.put("assigneeType", "PROJECT_LEAD");
        requestBody.put("avatarId", 10200);

        given()
                .header("Authorization", auth)
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .put(baseURI + "/rest/api/2/project/" + this.ProjectID)
                .then()
                .log().body()
                .statusCode(200);
    }


}
