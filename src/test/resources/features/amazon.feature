@Amazon @E2E
Feature: Amazon Search and Shopping
  As a customer
  I want to search for products on Amazon
  So that I can find items and add them to my cart

  Background:
    Given the Amazon website is opened

  @Search @Smoke
  Scenario Outline: Search for products and verify results
    When I search for "<keyword>"
    Then I should see search results
    And the search box should contain "<keyword>"

    Examples:
      | keyword      |
      | laptop       |
      | headphones   |
      | gaming mouse |

  @Actions @UI
  Scenario: Hover and interact with navigation menu
    When I search for "laptop"
    And I hover over "Accounts & Lists"
    And I open the Watchlist
    Then I should see the Sign-In header

  @Cart @Critical
  Scenario Outline: Open cart in new window
    When I open the cart in a new window
    Then the cart should be "<visibility>"

    Examples:
      | visibility |
      | visible    |

