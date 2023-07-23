package api;

import constants.URL;
import entity.ProjectRole;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static api.LoginAPI.encodeCredentials;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static io.restassured.http.Cookies.cookies;

public class ProjectRoleAPI {
    private static final String adminUsername = "haoyu";
    private static final String adminPassword = "12345";

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.PROJECT_ROLE.toString())
            .addHeader("Authorization", "Basic " + encodeCredentials(adminUsername, adminPassword))
            .setContentType(ContentType.JSON)
            .build();

    Response loginResponse = new LoginAPI(adminUsername, adminPassword).loginUser(adminUsername, adminPassword);

    public Response createProjectRole(ProjectRole projectRole) {
        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .body(projectRole)
                .when()
                .post();
    }

    public Response getProjectRoles() {
        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .when()
                .get();
    }

    public Response deleteProjectRoleCreated(int projectRoleId) {
        int swapId = 10002; // to do
        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .pathParam("projectRoleId", projectRoleId)
                .pathParam("swapId", swapId) // should be pathParam, not queryParam
                .when()
                .delete("/{projectRoleId}?swap={swapId}"); // swap to the default admin user
    }
}
