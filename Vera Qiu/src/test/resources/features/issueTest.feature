Feature: Issue Management
  Scenario: Team Lead user performs issue management actions
    Given I am signed in as a team lead user
    Then I created issues that belong to different epics and priorities in the backlog
    And I can specify the blocking relationship between different issues
    And I can assign issues to different users


  Scenario: Developer user perform issue management actions
    Given I am signed in as a developer user
    Then I can view all the issues assigned to me
    And I can add comments to an issue
    And I can update the comment to an issue