Feature: Group Management
  Scenario:
    When login as admin for group
    And create group
    And add user to group
    Then get user with group filter