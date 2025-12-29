@Shopping @E2E
Feature: Shopping E2E Flow
  As a customer
  I want to browse and purchase products
  So that I can complete an order

  Background:
    Given the shopping application is opened

  @Login @UI @Smoke
  Scenario Outline: Login with valid credentials via UI
    When I login with email "<email>" and password "<password>"
    Then I should see the dashboard page
    And the "<button>" button should be visible

    Examples:
      | email                 | password      | button    |
      | yehor_test@test.com   | Admin123456!  | Sign Out  |

  @Login @API @Smoke
  Scenario Outline: Login via API and verify dashboard
    Given I am logged in via API with email "<email>" and password "<password>"
    When I navigate to the dashboard
    Then I should see the dashboard page
    And the "<button>" button should be visible

    Examples:
      | email                 | password      | button |
      | yehor_test@test.com   | Admin123456!  | HOME   |

  @AddToCart @Smoke @Critical
  Scenario Outline: Add product to cart
    Given I am logged in via API with email "<email>" and password "<password>"
    And I navigate to the dashboard
    When I search for product "<product>"
    And I add the product to cart
    Then the cart count should be "<count>"

    Examples:
      | email                 | password      | product      | count |
      | yehor_test@test.com   | Admin123456!  | ZARA COAT 3  | 1     |

  @Checkout @E2E @Critical
  Scenario Outline: Complete checkout process
    Given I am logged in via API with email "<email>" and password "<password>"
    And I navigate to the dashboard
    When I search for product "<product>"
    And I add the product to cart
    And I open the cart
    Then the product "<product>" should be in the cart
    And the product price should be "<price>"
    When I proceed to checkout
    And I fill checkout form with shipping country "<country>"
    And I place the order
    Then I should see the order confirmation
    And the order should contain product "<product>"
    And the order total should be "<price>"

    Examples:
      | email                 | password      | product      | price     | country       |
      | yehor_test@test.com   | Admin123456!  | ZARA COAT 3  | $ 11500   | United States |

