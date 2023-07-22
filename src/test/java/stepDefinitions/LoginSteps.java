package stepDefinitions;

import api.LoginAPI;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class LoginSteps {
    static ThreadLocal<Response> responseThreadLocal = new ThreadLocal<>();
    @When("I log in with authentication adminUsername {string}, adminPassword {string}, and username {string}, password {string}")
    public void iLogInWithAdminUsernameAdminPasswordAndUsernamePassword(String adminUsername, String adminPassword, String username, String password) {
        Response response = new LoginAPI(adminUsername, adminPassword)
                .loginUser(username, password);
        responseThreadLocal.set(response);
    }

    @Then("the status code should be {int}")
    public void theStatusCodeShouldBe(int statusCode) {
        responseThreadLocal.get().then()
                .statusCode(200);
    }
}
