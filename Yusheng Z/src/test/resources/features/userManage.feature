Feature: User Management
  Scenario:
    When login as admin
    And Create user
    Then Get user information
    And Deactive user
    Then Try to login
    And View User with filter
    And Create another user
    And Assign user to groups
    Then view user in result after group filter
