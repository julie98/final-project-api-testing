Feature: project role management
  Scenario Outline:
    When I create project role with projectRoleName "team lead" and description "lead the team"
    And I create project role with projectRoleName "developer" and description "develop applications"
    And I create project role with projectRoleName "QA" and description "test applications"
    And I can add existing user <username> in a project to a project role
    And I can filter users in a project based on their roles
    And I can create the permission scheme with name "team lead can manage sprints" and description "only a user with team lead role can manage sprints" and projectRole "team lead" and permission "MANAGE_SPRINTS_PERMISSION"
    And I can create the permission scheme with name "team lead can assign issues" and description "team lead can assign issues to other user in the current project" and projectRole "team lead" and permission "ASSIGN_ISSUES"
    And I can create the permission scheme with name "developer can resolve an issue" and description "Only a user with developer role can resolve an issue" and projectRole "developer" and permission "RESOLVE_ISSUES"
    And I can create the permission scheme with name "QA can close an issue" and description "Only a user with QA role can close an issue" and projectRole "QA" and permission "CLOSE_ISSUES"
    And I delete the project roles I created
    And I delete the permission schemes I created
    Examples:
      | username |
      | "julie"  |