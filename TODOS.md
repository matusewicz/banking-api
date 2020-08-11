# Open requirements and todos

## Requirements

1. [x] Spring Boot application with REST API

2. [x] Create bank account
    - Accept client information
    - Return account number

3. [x] Deposit money to bank account
    - add money to bank account
    - Optional: limit transactions by min and max amounts

4. [x] Transfer money to other bank account
    - withdraw money from one account and deposit it to another

5. [ ] Show current account balance

6. [ ] Configurable transaction rules
    - by default prevent negative balances
    - allow overdraft for specific accounts

7. [ ] List transaction history

8. [ ] BONUS: Support multi currency
    - deposit money in different currencies to a single account

9. [ ] BONUS: Account locking
    - allow locking and unlocking of accounts as fraud protection

## Steps

- ~~Create walking skeleton~~
- ~~Define open api spec~~
- ~~Implement create and get bank account~~
- ~~Integrate problem json~~
- ~~Integrate java validation~~
- ~~Implement hateos linked resources~~
- ~~Implement add money to account~~
- ~~Implement move money between accounts~~
- Implement account balance
- Implement transaction history
- Implement transaction rules
- Implement currency exchange calculation on deposit of foreign currencies
- Implement account locking/unlocking