Feature: Transaction Management

  Scenario: Create a transaction for an existing payment method
    Given a "CARD" payment method with account "card-txn-001" exists
    When I create a transaction with amount 100.00 for that payment method
    Then the response status should be 200
    And the transaction amount should be 100.00

  Scenario: Create a transaction for a non-existent payment method returns 404
    When I create a transaction with amount 50.00 for payment method id 99999
    Then the response status should be 404
