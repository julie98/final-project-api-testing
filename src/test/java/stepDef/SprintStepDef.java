package stepDef;

import api.SprintAPI;
import entity.Issues;
import entity.Sprint;
import entity.SprintResponse;
import entity.SprintState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class SprintStepDef {
    private static final ThreadLocal<SprintResponse> sprintResponse = new ThreadLocal<>();
    private static final ThreadLocal<String> stateAfterOperation = new ThreadLocal<>();

    @When("I create a new sprint")
    public void iCreateANewSprint() {
        sprintResponse.set(SprintAPI.createSprint(Sprint.builder().originBoardId(2)
                        .name("sprint 77")
                        .goal("sprint 3 goal")
                        .startDate("2023-07-21T15:22:00.000+10:00")
                        .endDate("2023-08-19T01:22:00.000+10:00").build())
                .then().statusCode(201).extract().jsonPath().getObject("", SprintResponse.class));
        System.out.println(sprintResponse.get().getId());
    }

    @Then("the sprint should be in the {string} state")
    public void theSprintShouldBeInTheState(String state) {
        Assert.assertEquals(sprintResponse.get().getState(), state);
    }

    @And("there are issues in the backlog")
    public void thereAreIssuesInTheBacklog() {
        SprintAPI.viewIssuesInSprint(sprintResponse.get().getId())
                .then()
                .statusCode(200)
                .log().body();
    }


    @When("I move issues from the backlog to the current sprint")
    public void iMoveIssuesFromTheBacklogToTheCurrentSprint() {
        List<String> issueList = new ArrayList<>();
        issueList.add("TP-1");
        issueList.add("TP-2");

        SprintAPI.addIssues(Issues.builder().issues(issueList.toArray(new String[0])).build(), sprintResponse.get().getId())
                .then()
                .statusCode(204)
                .log().body();

    }

    @Then("the current sprint should have the moved issues")
    public void theCurrentSprintShouldHaveTheMovedIssues() {
    }

    @When("I start the sprint")
    public void iStartTheSprint() {
        Response response = SprintAPI.changeState(SprintState.builder()
                .name("sprint 77")
                .goal("sprint 77 goal")
                .startDate("2023-07-21T15:22:00.000+10:00")
                .endDate("2023-08-19T01:22:00.000+10:00")
                        .completeDate("2023-08-21T11:11:28.008+10:00")
                .state("active").build(), sprintResponse.get().getId());

        response.then().log().body();
        stateAfterOperation.set(response.then().extract().jsonPath().getString("state"));
    }

    @Then("the sprint status should be {string}")
    public void theSprintStatusShouldBe(String state) {
        Assert.assertEquals(stateAfterOperation.get(), state);
    }

    @Given("there is an active sprint")
    public void thereIsAnActiveSprint() {
        Assert.assertEquals(stateAfterOperation.get(), "active");
    }

    @When("I end the current active sprint")
    public void iEndTheCurrentActiveSprint() {
        Response response = SprintAPI.changeState(SprintState.builder()
                        .name("sprint 77")
                        .goal("sprint 77 goal")
                        .startDate("2023-07-21T15:22:00.000+10:00")
                        .endDate("2023-08-19T01:22:00.000+10:00")
                        .completeDate("2023-08-21T11:11:28.008+10:00")
                        .state("closed").build(), sprintResponse.get().getId());
        response.then().log().body();
        stateAfterOperation.set(response.then().extract().jsonPath().getString("state"));
    }

    @Then("the sprint status should be set to {string}")
    public void theSprintStatusShouldBeSetTo(String state) {
        Assert.assertEquals(stateAfterOperation.get(), state);
    }

    @When("I download the velocity chart")
    public void iDownloadTheVelocityChart() {
        //todo: handle the board id
        SprintAPI.downloadChart(2)
                .then()
                .log().body().statusCode(200);
    }

    @Then("I should receive the velocity chart as an image file")
    public void iShouldReceiveTheVelocityChartAsAnImageFile() {
    }
}
