# Created by yulin at 7/19/23
Feature: I want to test the sprint management feature of Jira

  Scenario: After I log in as a team leader, I can perform sprint management

    When I create a new sprint
    Then the sprint should be in the "future" state

    And there are issues in the backlog
    When I move issues from the backlog to the current sprint
    Then the current sprint should have the moved issues

    When I start the sprint
    Then the sprint status should be "active"

    Given there is an active sprint
    When I end the current active sprint
    Then the sprint status should be set to "closed"

    When I download the velocity chart
    Then I should receive the velocity chart as an image file
