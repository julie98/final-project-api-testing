Feature: Project Management

  Scenario Outline:
    When I login with <user> and <password>
    And I create a new project with <key>, <name>, and <description>
    Then I should see the detail of the project
    When I create a new issue with <summary> and <issueDescription>
    Then I should see the detail of the issue
#    This feature bugged, Epic works in postman but not in rest assured.
    # https://jira.atlassian.com/browse/JSWSERVER-19861
    When I create new user with <username>, <userPassword>,<email> and <displayName>
    And I create project role with <roleName> and <RoleDescription>
    And I add <username> to the project role.

    Examples:
    | user | password | key | name | description | summary | issueDescription | roleName | RoleDescription | username | userPassword | email | displayName |
    | "admin"| "12345"| "TST" | "Cucumbertest1" | "This is a test project" | "Test Issue" | "This is a test issue" | "Idunnowhatrole" | "This is a test role" | "testsuer" | "12345" | "testuser@localhost.com" | "TestUser1" |