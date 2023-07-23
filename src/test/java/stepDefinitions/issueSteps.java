package stepDefinitions;

import api.IssueAPI;
import api.LoginAPI;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.annotations.Test;



public class issueSteps {

    String username_team_lead = "vera_team_lead";
    String password_team_lead = "12345";

    String username_developer = "vera_developer";
    String password_developer = "12345";

    Response issueResponse;
    Response commentResponse;

    @Test
    @When("I am signed in as a team lead user")
    public void iamSignedInAsATeamLeadUser() {
        Response response = IssueAPI.userLogin(username_team_lead, password_team_lead);
        response.then().statusCode(200);
    }

    @Test(dependsOnMethods = "iamSignedInAsATeamLeadUser")
    @Then("I created issues that belong to different epics and priorities in the backlog")
    public void iCreatedIssuesThatBelongToDifferentEpicsAndPrioritiesInTheBacklog() {
        issueResponse = IssueAPI.createIssues();
        issueResponse.then().statusCode(201);
    }

    @Test(dependsOnMethods = "iCreatedIssuesThatBelongToDifferentEpicsAndPrioritiesInTheBacklog")
    @And("I can specify the blocking relationship between different issues")
    public void iCanSpecifyTheBlockingRelationshipBetweenDifferentIssues() {
        String blockOption = "Blocks"; // todo
        String issue1Name = "TJ-2"; // todo
        String issue2Name = "TJ-3"; // todo
        String comment = "Issue TJ-2 is blocking TJ-3"; // todo
        Response response = IssueAPI.setBlockingIssue(blockOption, issue1Name, issue2Name, comment);
        response.then().statusCode(201);
    }

    @Test(dependsOnMethods = "iCanSpecifyTheBlockingRelationshipBetweenDifferentIssues")
    @And("I can assign issues to different users")
    public void iCanAssignIssuesToDifferentUsers() {
        String issueID = issueResponse.jsonPath().getString("issues[1].id");
        Response response = IssueAPI.assignIssues(username_developer, issueID);
        response.then().statusCode(204);
    }

    @Test
    @When("I am signed in as a developer user")
    public void iamSignedInAsADeveloper() {
        Response response = IssueAPI.userLogin(username_developer, password_developer);
        response.then().statusCode(200);
    }


    @Test(dependsOnMethods = "iamSignedInAsADeveloper")
    @Then("I can view all the issues assigned to me")
    public void iCanViewAllTheIssuesAssignedToMe() {
        Response response = IssueAPI.viewMyIssues();
        response.then().statusCode(200);
    }

    @Test(dependsOnMethods = "iCanViewAllTheIssuesAssignedToMe")
    @And("I can add comments to an issue")
    public void iCanAddCommentsToAnIssue() {
        String issueID = issueResponse.jsonPath().getString("issues[1].id");
        String commentBody = "This issue is complicated"; // hard code
        String typeName = "role"; // hard code
        String roleName = "developer1"; //hard code
        commentResponse = IssueAPI.addComment(issueID, commentBody, typeName, roleName);
        commentResponse.then().statusCode(201);
    }


    @Test(dependsOnMethods = "iCanAddCommentsToAnIssue")
    @And("I can update the comment to an issue")
    public void iCanUpdateTheCommentToAnIssue() {
        String issueID = issueResponse.jsonPath().getString("issues[1].id");
        String commentID = commentResponse.jsonPath().getString("id");
        String commentBodyUpdate = "This issue is easy"; // hard code
        String typeName = "role"; // hard code
        String roleName = "developer1"; // hard code
        Response response = IssueAPI.editComment(issueID, commentID, commentBodyUpdate, typeName, roleName);
        response.then().statusCode(200);
    }
}
