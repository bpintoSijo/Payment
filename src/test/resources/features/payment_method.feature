Feature: Payment Method Management

  Scenario: Create a credit card payment method
    Given I want to create a "CARD" payment method with account "card-001"
    When I submit the payment method
    Then the response status should be 200
    And the payment method type should be "CARD"
    And the payment method account should be "card-001"

  Scenario: Create a PayPal payment method
    Given I want to create a "PAYPAL" payment method with account "paypal-001"
    When I submit the payment method
    Then the response status should be 200
    And the payment method type should be "PAYPAL"
    And the payment method account should be "paypal-001"

  Scenario: Create a crypto payment method
    Given I want to create a "CRYPTO" payment method with account "crypto-001"
    When I submit the payment method
    Then the response status should be 200
    And the payment method type should be "CRYPTO"
    And the payment method account should be "crypto-001"

  Scenario: List all available payment methods
    When I request all available payment methods
    Then the response status should be 200
    And the response should be a JSON array

  Scenario: Create a payment method with an invalid type returns 400
    Given I want to create a "INVALID" payment method with account "unknown-001"
    When I submit the payment method
    Then the response status should be 400
