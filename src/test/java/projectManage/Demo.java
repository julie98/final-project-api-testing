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

public class Demo {

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


        @BeforeTest
        public void login() {
            Map<String, String> userinfo = new HashMap<>();
            userinfo.put("username", "admin");
            userinfo.put("password", "12345");

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

        @Test
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
                    .log().body()
                    .statusCode(201)
                    .extract().response();

            this.ProjectID = response.path("id");
        }

        @Test
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

        @Test
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

        @Test
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
                    .statusCode(200);
        }

        @Test  //https://jira.atlassian.com/browse/JSWSERVER-19861 bugged no fix
        //change to story works find. Epic is bugged
        public void createEpicIssue(){
            JSONObject issueType = new JSONObject();
            issueType.put("name", "Story"); //If Epic not gonna work

            JSONObject project = new JSONObject();
            project.put("id", 10001);

            JSONObject fields = new JSONObject();
            fields.put("project", project);
            fields.put("summary", "IMA EPIC");
            fields.put("description", "IMA EPIC!!!!!!!!!!!!!!");
            fields.put("issuetype", issueType);

            JSONObject mainObject = new JSONObject();
            mainObject.put("fields", fields);

            System.out.println(mainObject.toString());

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

            System.out.println(issueID);

        }

        @Test
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

