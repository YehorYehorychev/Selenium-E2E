@FlightBooking @E2E
Feature: Flight Booking System
  As a traveler
  I want to book flights
  So that I can plan my travel

  Background:
    Given the flight booking website is opened

  @Currency @Normal
  Scenario Outline: Select currency and verify selection
    When I select currency "<currency>"
    Then the selected currency should be "<currency>"

    Examples:
      | currency |
      | USD      |
      | INR      |

  @Passengers @Critical
  Scenario Outline: Configure passenger composition
    When I open the passenger selector
    And I add "<adults>" adults
    And I add "<children>" children
    And I confirm passenger selection
    Then the passenger info should show "<adults>" adults and "<children>" children

    Examples:
      | adults | children |
      | 1      | 2        |
      | 2      | 1        |

  @Route @Normal
  Scenario Outline: Select origin and destination cities
    When I select origin "<origin>"
    And I select destination "<destination>"
    Then the origin should be "<origin>"
    And the destination should be "<destination>"

    Examples:
      | origin | destination |
      | GOI    | BLR         |
      | DEL    | HYD         |

  @Country @DynamicDropdown @Minor
  Scenario Outline: Search and select country from dynamic dropdown
    When I search for country "<countryQuery>"
    And I select country suggestion "<countrySuggestion>"
    Then the selected country should be "<countrySuggestion>"

    Examples:
      | countryQuery | countrySuggestion            |
      | Br           | Virgin Islands (British)     |
      | Ind          | India                        |

