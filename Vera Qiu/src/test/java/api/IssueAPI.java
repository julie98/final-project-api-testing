package api;

import constants.IssueCommentConstants;
import constants.IssueCommentUpdateConstants;
import constants.IssueLinkConstants;
import constants.IssuePayloadConstants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class IssueAPI {
    private static final String BASE_URL = "http://localhost:8080/rest";
    private static final String username_Team_Lead = "karlie";
    private static final String password_Team_Lead = "abulaka";
    private static final String username_Developer = "jake";
    private static final String password_Developer = "abulaka";

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
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .body(IssuePayloadConstants.ISSUE_PAYLOAD)
                .post("/api/2/issue/bulk");
    }

    public static Response setBlockingIssue() {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Team_Lead)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .body(IssueLinkConstants.ISSUE_LINK_PAYLOAD)
                .post("/api/2/issueLink");
    }

    public static Response assignIssues(String username, String issueID) {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", username);
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Team_Lead)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .pathParam("issueID", issueID)
                .body(payload)
                .post("/api/2/issue/{issueID}/assignee");
    }

    public static Response viewMyIssues() {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Developer)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .queryParam("jql", "assignee=currentuser()")
                .get("/api/2/search");
    }

    public static Response addComment(String issueID) {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Developer)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .pathParam("issueID", issueID)
                .queryParam("expand")
                .body(IssueCommentConstants.COMMENT_PAYLOAD)
                .post("/api/2/issue/{issueID}/comment");
    }

    public static Response editComment(String issueID, String commentID) {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .header(authHeader_Developer)
                .cookies(loginResponse.getCookies())
                .contentType(ContentType.JSON)
                .pathParam("issueID", issueID)
                .pathParam("commentID", commentID)
                .queryParam("expand")
                .body(IssueCommentUpdateConstants.COMMENT_PAYLOAD)
                .post("/api/2/issue/{issueID}/comment/{commentID}");
    }


}
