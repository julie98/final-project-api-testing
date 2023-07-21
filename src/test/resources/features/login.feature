Feature: user login
  Scenario Outline:
    When I log in with authentication adminUsername "hzhu64", adminPassword "Zhy123321!", and username <username>, password <password>
    Then the status code should be 200
    Examples:
      | username | password     |
      | "hzhu64" | "Zhy123321!" |
