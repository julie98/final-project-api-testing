package stepDefinitions;

import api.PermissionSchemeAPI;
import api.ProjectAPI;
import api.ProjectRoleAPI;
import entity.ProjectRole;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ProjectRoleManagementSteps {
    static ThreadLocal<Response> responseThreadLocal = new ThreadLocal<>();
    Map<String, Integer> projectRoleIds = new HashMap<>();
    static ThreadLocal<Map> projectRoleIdsThreadLocal = new ThreadLocal<>();

    Map<String, Integer> permissionSchemeIds = new HashMap<>();
    static ThreadLocal<Map> permissionSchmeIdsThreadLocal = new ThreadLocal<>();
    private String projectRoleName1;
//    private int projectRoleId;
    private int projectId = 10203; // todo

    @When("I create project role with projectRoleName {string} and description {string}")
    public void iCreateProjectRoleWithProjectRoleNameAndDescription(String projectRoleName, String projectRoleDescription) {
        projectRoleName1 = projectRoleName;
        Response response = new ProjectRoleAPI().createProjectRole(ProjectRole.builder()
                .name(projectRoleName)
                .description(projectRoleDescription)
                .build());

        int projectRoleId = response
                .then()
                .statusCode(200)
                .extract().jsonPath()
                .getInt("id");

        System.out.println("---------------------");
        System.out.println("create project role");
        System.out.println("project role name: " + projectRoleName);
        System.out.println("project role id: " + projectRoleId);

        projectRoleIds.put(projectRoleName, projectRoleId);
        projectRoleIdsThreadLocal.set(projectRoleIds);

        responseThreadLocal.set(response);
    }

    @And("I can add existing user {string} in a project to a project role")
    public void iCanAddExistingUserInAProjectToAProjectRole(String username) {
        int projectRoleId = projectRoleIds.get(projectRoleName1);

        Response response = new ProjectAPI().addUserToAProjectRole(username, projectId, projectRoleId);
        response.then()
                .statusCode(200);

        System.out.println("---------------------");
        System.out.println("add existing user in a project to a project role");
        System.out.println("username: " + username);
        System.out.println("project role id: " + projectRoleId);

        responseThreadLocal.set(response);
    }

    @And("I can filter users in a project based on their roles")
    public void iCanFilterUsersInAProjectBasedOnTheirRoles() {
        Map<String, Integer> projectRoleIds = projectRoleIdsThreadLocal.get();
        int projectRoleId = projectRoleIds.get(projectRoleName1);

        Response response = new ProjectAPI().getProjectRoleInAProject(projectId, projectRoleId);
        response.then()
                .statusCode(200);

        System.out.println("---------------------");
        System.out.println("filter user in a project based on their roles response body");
        response.then().log().body();

        responseThreadLocal.set(response);
    }

    @And("I can create the permission scheme with name {string} and description {string} and projectRole {string} and permission {string}")
    public void iCanCreateThePermissionSchemeWithNameAndDescriptionAndParameterTeamLeadRoleIdAndPermission(String permissionSchemeName, String permissionSchemeDescription, String projectRole, String permissionSchemePermission) {
        Map<String, Integer> projectRoleIds = projectRoleIdsThreadLocal.get();
        System.out.println("project role id map: " + projectRoleIds.toString());
        System.out.println("project role id from map: " + projectRoleIds.get(projectRole));
        int projectRoleId = projectRoleIds.get(projectRole);

        Response response = new PermissionSchemeAPI().createPermissionScheme(permissionSchemeName, permissionSchemeDescription, projectRoleId, permissionSchemePermission);

        int permissionSchemeId = response
                .then()
                .statusCode(201)
                .extract().jsonPath()
                .getInt("id");

        System.out.println("---------------------");
        System.out.println("create permission scheme");
        System.out.println("permission scheme name: " + permissionSchemeName);
        System.out.println("permission scheme id: " + permissionSchemeId);

        permissionSchemeIds.put(permissionSchemeName, permissionSchemeId);
        permissionSchmeIdsThreadLocal.set(permissionSchemeIds);

        response.then().log().body();
        responseThreadLocal.set(response);
    }

    @And("I delete the project roles I created")
    public void iDeleteTheProjectRolesICreated() {
        Map<String, Integer> projectRoleIds = projectRoleIdsThreadLocal.get();
        for (Map.Entry<String, Integer> entry: projectRoleIds.entrySet()) {
            int projectRoleId = entry.getValue();
            Response response = new ProjectRoleAPI().deleteProjectRoleCreated(projectRoleId);
            response.then()
                    .statusCode(204);
        }
    }

    @And("I delete the permission schemes I created")
    public void iDeleteThePermissionSchemesICreated() {
        Map<String, Integer> permissionSchemeIds = permissionSchmeIdsThreadLocal.get();
        for (Integer permissionSchemeId: permissionSchemeIds.values()) {
            Response response = new PermissionSchemeAPI().deletePermissionSchemeCreated(permissionSchemeId);
            response.then()
                    .statusCode(204);
        }
    }
}
