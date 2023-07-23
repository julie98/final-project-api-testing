package api;

import com.google.gson.JsonObject;
import constants.IssuePayloadConstants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class IssueAPI {
    private static final String BASE_URL = "http://localhost:8080/rest";
    private static final String username_Team_Lead = "vera_team_lead";
    private static final String password_Team_Lead = "12345";
    private static final String username_Developer = "vera_developer";
    private static final String password_Developer = "12345";

    private static Response loginResponse;
    private static Header authHeader_Team_Lead = new Header("Authorization", "Basic " + encodeCredentials(username_Team_Lead, password_Team_Lead));
    private static Header authHeader_Developer = new Header("Authorization", "Basic " + encodeCredentials(username_Developer, password_Developer));

    private static String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public static Response userLogin(String username, String password) {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);
        return loginResponse = RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/auth/1/session");
    }

    public static Response createIssues() {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Team_Lead)
                .contentType(ContentType.JSON)
                .body(IssuePayloadConstants.ISSUE_PAYLOAD)
                .post("/api/2/issue/bulk");
    }

    public static Response setBlockingIssue(String blockOption, String issue1Name, String issue2Name, String comment) {
        // Create a new JsonObject
        JsonObject jsonObject = new JsonObject();

        // Set the "type" object
        JsonObject typeObject = new JsonObject();
        typeObject.addProperty("name", blockOption);
        jsonObject.add("type", typeObject);

        // Set the "inwardIssue" object
        JsonObject inwardIssueObject = new JsonObject();
        inwardIssueObject.addProperty("key", issue1Name);
        jsonObject.add("inwardIssue", inwardIssueObject);

        // Set the "outwardIssue" object
        JsonObject outwardIssueObject = new JsonObject();
        outwardIssueObject.addProperty("key", issue2Name);
        jsonObject.add("outwardIssue", outwardIssueObject);

        // Set the "comment" object
        JsonObject commentObject = new JsonObject();
        commentObject.addProperty("body", comment);
        jsonObject.add("comment", commentObject);

        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Team_Lead)
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post("/api/2/issueLink");
    }

    public static Response assignIssues(String username, String issueID) {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", username);
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Team_Lead)
                .contentType(ContentType.JSON)
                .pathParam("issueID", issueID)
                .body(payload)
                .put("/api/2/issue/{issueID}/assignee");
    }

    public static Response viewMyIssues() {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Developer)
                .contentType(ContentType.JSON)
                .queryParam("jql", "assignee=currentuser()")
                .get("/api/2/search");
    }

    public static Response addComment(String issueID, String commentBody, String typeName, String roleName) {
        // Create a new JsonObject
        JsonObject jsonObject = new JsonObject();

        // Set the "body" property
        jsonObject.addProperty("body", commentBody);

        // Create a new JsonObject for "visibility"
        JsonObject visibilityObject = new JsonObject();

        // Set the "type" property
        visibilityObject.addProperty("type", typeName);

        // Set the "value" property
        visibilityObject.addProperty("value", roleName);

        // Add the "visibility" object to the main jsonObject
        jsonObject.add("visibility", visibilityObject);
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Developer)
                .contentType(ContentType.JSON)
                .pathParam("issueID", issueID)
                .queryParam("expand")
                .body(jsonObject.toString())
                .post("/api/2/issue/{issueID}/comment");
    }

    public static Response editComment(String issueID, String commentID, String commentBodyUpdate, String typeName, String roleName) {
        // Create a new JsonObject
        JsonObject jsonObject = new JsonObject();

        // Set the "body" property
        jsonObject.addProperty("body", commentBodyUpdate);

        // Create a new JsonObject for "visibility"
        JsonObject visibilityObject = new JsonObject();

        // Set the "type" property
        visibilityObject.addProperty("type", typeName);

        // Set the "value" property
        visibilityObject.addProperty("value", roleName);

        // Add the "visibility" object to the main jsonObject
        jsonObject.add("visibility", visibilityObject);
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Developer)
                .contentType(ContentType.JSON)
                .pathParam("issueID", issueID)
                .pathParam("commentID", commentID)
                .queryParam("expand")
                .body(jsonObject.toString())
                .put("/api/2/issue/{issueID}/comment/{commentID}");
    }


}
