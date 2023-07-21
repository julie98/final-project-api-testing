package StepDef;

import api.IssueAPI;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.annotations.Test;



public class issueTestStepDef {

    String username_team_lead = "karlie";
    String password_team_lead = "abulaka";

    String username_developer = "jake";
    String password_developer = "abulaka";

    Response issueResponse;
    Response commentResponse;

@Test
@When("I am signed in as a team lead user")
public void iamSignedInAsATeamLeadUser() {
    Response response = IssueAPI.userLogin(username_team_lead, password_team_lead);
    response.then().statusCode(200);
}

@Test
@Then("I created issues that belong to different epics and priorities in the backlog")
public void iCreatedIssuesThatBelongToDifferentEpicsAndPrioritiesInTheBacklog() {
    issueResponse = IssueAPI.createIssues();
    issueResponse.then().statusCode(201);
}

@Test
@And("I can specify the blocking relationship between different issues")
public void iCanSpecifyTheBlockingRelationshipBetweenDifferentIssues() {
    Response response = IssueAPI.setBlockingIssue();
    response.then().statusCode(201);
}

@Test
@And("I can assign issues to different users")
public void iCanAssignIssuesToDifferentUsers() {
    String issueID = issueResponse.jsonPath().getString("issues[0].id");
    Response response = IssueAPI.assignIssues(username_developer, issueID);
    response.then().statusCode(204);
}

    @Test
    @When("I am signed in as a developer user")
    public void iamSignedInAsADeveloper() {
        Response response = IssueAPI.userLogin(username_developer, password_developer);
        response.then().statusCode(200);
    }


    @Test
    @Then("I can view all the issues assigned to me")
    public void iCanViewAllTheIssuesAssignedToMe() {
        Response response = IssueAPI.viewMyIssues();
        response.then().statusCode(200);
    }

    @Test
    @And("I can add comments to an issue")
    public void iCanAddCommentsToAnIssue() {
        String issueID = issueResponse.jsonPath().getString("issues[1].id");
        commentResponse = IssueAPI.addComment(issueID);
        commentResponse.then().statusCode(201);
    }

    @Test
    @And("I can update the comment to an issue")
    public void iCanUpdateTheCommentToAnIssue() {
        String issueID = issueResponse.jsonPath().getString("issues[1].id");
        String commentID = commentResponse.jsonPath().getString("id");
        Response response = IssueAPI.editComment(issueID, commentID);
        response.then().statusCode(200);
    }
}
