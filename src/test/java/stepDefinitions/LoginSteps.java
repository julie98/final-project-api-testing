package stepDefinitions;

import api.LoginAPI;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class LoginSteps {

    Response loginResponse;
    @When("I log in with authentication adminUsername {string}, adminPassword {string}, and username {string}, password {string}")
    public void iLogInWithAdminUsernameAdminPasswordAndUsernamePassword(String adminUsername, String adminPassword, String username, String password) {
        loginResponse = new LoginAPI(adminUsername, adminPassword)
                .loginUser(username, password);
    }

    @Then("the status code should be {int}")
    public void theStatusCodeShouldBe(int statusCode) {
        loginResponse.then()
                .statusCode(200);
    }
}
