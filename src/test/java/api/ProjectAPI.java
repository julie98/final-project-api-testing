package api;

import constants.URL;
import entity.Project;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import static api.LoginAPI.encodeCredentials;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;

public class ProjectAPI {
    private static final String adminUsername = "hzhu64";
    private static final String adminPassword = "Zhy123321!";
    private final static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.PROJECT.toString())
            .addHeader("Authorization", "Basic " + encodeCredentials(adminUsername, adminPassword))
            .setContentType(ContentType.JSON)
            .build();
    Response loginResponse = new LoginAPI(adminUsername, adminPassword).loginUser(adminUsername, adminPassword);

    public Response createProject(Project project) {
        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .body(project)
                .when()
                .post();
    }

    public Response addUserToAProjectRole(String username, int projectId, int roleId) {
        JSONObject requestBody = new JSONObject();
        JSONArray usersArray = new JSONArray();
        usersArray.put(username);
        requestBody.put("user", usersArray);

        System.out.println("username " + username);
        System.out.println("roleId " + roleId);

        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .body(requestBody.toString())
                .pathParam("projectId", projectId)
                .pathParam("roleId", roleId)
                .when()
                .post("/{projectId}/role/{roleId}");
    }

    public Response getProjectRoleInAProject(int projectId, int roleId) {
        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .pathParam("projectId", projectId)
                .pathParam("roleId", roleId)
                .when()
                .get("/{projectId}/role/{roleId}");
    }
}
