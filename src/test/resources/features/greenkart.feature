@GreenKart @E2E
Feature: GreenKart Shopping
  As a customer
  I want to add vegetables to cart
  So that I can purchase fresh produce

  Background:
    Given the GreenKart website is opened

  @Cart @PromoCode @Critical
  Scenario Outline: Add vegetables to cart and apply promo code
    When I add the following vegetables to cart:
      | vegetables   |
      | <vegetables> |
    Then the GreenKart cart count should be "<count>"
    When I open the cart overlay
    And I proceed to checkout from the overlay
    Then the cart should contain:
      | expectedNames   |
      | <expectedNames> |
    When I apply promo code "<promoCode>"
    Then the promo code should be applied successfully

    Examples:
      | vegetables                    | count | expectedNames                                     | promoCode           |
      | Brocolli,Cauliflower,Cucumber | 3     | Brocolli - 1 Kg,Cauliflower - 1 Kg,Cucumber - 1 Kg| rahulshettyacademy  |
      | Beans,Carrot                  | 2     | Beans - 1 Kg,Carrot - 1 Kg                        | rahulshettyacademy  |

  @Cart @Sorting @Normal
  Scenario Outline: Verify cart items are sorted alphabetically
    When I add the following vegetables to cart:
      | vegetables   |
      | <vegetables> |
    Then the GreenKart cart count should be "<count>"
    When I open the cart overlay
    And I proceed to checkout from the overlay
    Then the products in cart should be sorted alphabetically

    Examples:
      | vegetables                    | count |
      | Brocolli,Cauliflower,Cucumber | 3     |
      | Beans,Carrot                  | 2     |

  @TopDeals @Sorting @Minor
  Scenario Outline: Verify Top Deals sorting and prices
    When I open the Top Deals page
    And I sort by name column
    Then the vegetable names should be sorted
    And the price for "<vegetable>" should be displayed
    When I close the Top Deals page
    Then I should return to home page

    Examples:
      | vegetable |
      | Rice      |
      | Beans     |

