Feature: user login
  Scenario Outline:
    When I log in with authentication adminUsername "haoyu", adminPassword "12345", and username <username>, password <password>
    Then the status code should be 200
    Examples:
      | username | password |
      | "haoyu"  | "12345"  |
