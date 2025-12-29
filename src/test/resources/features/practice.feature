@Practice @UI
Feature: Practice Page Interactions
  As a test automation engineer
  I want to practice various UI interactions
  So that I can validate different web elements

  Background:
    Given the practice page is opened

  @Alerts @Smoke
  Scenario Outline: Dismiss confirmation alert
    When I enter name "<name>"
    And I trigger the confirm alert
    Then the alert text should be "<expectedAlert>"
    When I dismiss the alert
    Then the alert should be closed

    Examples:
      | name       | expectedAlert                                      |
      | Yehor      | Hello Yehor, Are you sure you want to confirm?     |
      | Automation | Hello Automation, Are you sure you want to confirm?|

  @Scroll @Normal
  Scenario: Scroll page and verify table visibility
    When I scroll the window down by 500 pixels
    Then the web table section should be visible
    When I scroll the fixed table to bottom
    Then I should see table values

  @Links @Critical
  Scenario: Validate footer links are not broken
    When I check all footer links
    Then all links should return valid status codes

