package api;

import constants.URL;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;

import static api.LoginAPI.encodeCredentials;
import static io.restassured.RestAssured.given;

public class PermissionSchemeAPI {
    private static final String adminUsername = "haoyu";
    private static final String adminPassword = "12345";

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.PERMISSION_SCHEME.toString())
            .addHeader("Authorization", "Basic " + encodeCredentials(adminUsername, adminPassword))
            .setContentType(ContentType.JSON)
            .build();

    Response loginResponse = new LoginAPI(adminUsername, adminPassword).loginUser(adminUsername, adminPassword);

    public Response createPermissionScheme(String permissionSchemeName, String permissionSchemeDescription, int projectRoleId, String permissionSchemePermission) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", permissionSchemeName);
        requestBody.put("description", permissionSchemeDescription);

        JSONArray permissionsArray = new JSONArray();
        JSONObject permissions = new JSONObject();
        JSONObject holder = new JSONObject();
        holder.put("type", "projectRole");
        holder.put("parameter", projectRoleId);
        permissions.put("holder", holder);
        permissions.put("permission", permissionSchemePermission);
        permissionsArray.put(permissions);

        requestBody.put("permissions", permissionsArray);

        System.out.println("create permission scheme request body: " + requestBody);

        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .body(requestBody.toString())
                .when()
                .post();
    }

    public Response deletePermissionSchemeCreated(int permissionSchemeId) {
        return given(requestSpec)
                .cookies(loginResponse.getCookies())
                .pathParam("permissionSchemeId", permissionSchemeId)
                .when()
                .delete("/{permissionSchemeId}");
    }
}
