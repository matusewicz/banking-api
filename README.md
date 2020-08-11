# Banking API
Simple example service providing a REST API to transfer money between two accounts and deposit money.

## Build & Run
```
./gradlew clean build && java -jar build/libs/banking-api.jar
```
This REST API provides HATEOAS style linked resources.

You can change the base url of those links by setting `SERVICE_BASE_URL` environment variable.
```
export SERVICE_BASE_URL=https://example.org && ./gradlew clean build && java -jar build/libs/banking-api.jar
```
## REST API
The API is document using [OAS](https://swagger.io/specification/).

You can find the full spec [here](docs/banking-api.spec.yml).

To explore it easier just copy paste it to a visual tool like [here](https://editor.swagger.io/)

## How to use it (example requests)

### Create Account:
```bash
curl -v -H 'Content-Type: application/json' -d '{"email":"alice@example.org", "baseCurrency":"EUR"}' http://localhost:8080/accounts
```
#### Response
```
< HTTP/1.1 201 Created
< Location: http://localhost:8080/accounts/4a6ff9b0-3f88-4ca1-ba75-543fc6fb4b72
< Content-Length: 0
```

##### Validation Error
```bash
curl -v -H 'Content-Type: application/json' -d '{"email":"alice example.org", "baseCurrency":"EURO"}' http://localhost:8080/accounts
```
##### Response
```
< HTTP/1.1 400 Bad Request
< Content-Type: application/problem+json
```
```json
{
  "type": "https://zalando.github.io/problem/constraint-violation",
  "status": 400,
  "violations": [
    {
      "field": "baseCurrency",
      "message": "must be a ISO 4217 currency code"
    },
    {
      "field": "email",
      "message": "must be a well-formed email address"
    }
  ],
  "title": "Constraint Violation"
}
```

### List Accounts: 
```bash
curl -v http://localhost:8080/accounts
```
#### Response
```
< HTTP/1.1 200 OK
< Content-Type: application/json
```
```json
{
  "customerAccounts": [
    {
      "accountNumber": "c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2",
      "email": "bob@example.org",
      "baseCurrency": "EUR",
      "_links": {
        "_self": {
          "href": "http://localhost:8080/accounts/c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2"
        },
        "balance": {
          "href": "http://localhost:8080/accounts/c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2/balance"
        },
        "transactions": {
          "href": "http://localhost:8080/accounts/c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2/transactions"
        }
      }
    },
    {
      "accountNumber": "63c550e7-86f8-4fd7-8244-4f4b4f5238cb",
      "email": "alice@example.org",
      "baseCurrency": "EUR",
      "_links": {
        "_self": {
          "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb"
        },
        "balance": {
          "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/balance"
        },
        "transactions": {
          "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions"
        }
      }
    }
  ],
  "_links": {
    "_self": {
      "href": "http://localhost:8080/accounts"
    }
  }
}
```

### View Account: 
```bash
curl -v http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb
```
#### Response
```
< HTTP/1.1 200 OK
< Content-Type: application/json
```
```json
{
  "accountNumber": "63c550e7-86f8-4fd7-8244-4f4b4f5238cb",
  "email": "alice@example.org",
  "baseCurrency": "EUR",
  "_links": {
    "_self": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb"
    },
    "accounts": {
      "href": "http://localhost:8080/accounts"
    },
    "balance": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/balance"
    },
    "transactions": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions"
    }
  }
}
```

### Deposit Money: 
For the sake of this example the application provides two preconfigured cash points [CP-1, CP-2] that can be used to deposit money.
```bash
curl -v -H 'Content-Type: application/json' -d '{"cashPointId":"CP-1", "depositAmount":{"amount":100.25, "currency":"EUR"}}' http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/deposit
```
#### Response
```
< HTTP/1.1 201
< Location: http://localhost:8080/transfers/1060a958-0ebd-46c3-8315-8b05aeb625a0
```

### View Balance
```bash
curl -v http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/balance
```
#### Response
```
< HTTP/1.1 200 OK
< Content-Type: application/json
```
```json
{
  "accountNumber": "63c550e7-86f8-4fd7-8244-4f4b4f5238cb",
  "balance": {
    "amount": 100.25,
    "currency": "EUR"
  },
  "_links": {
    "_self": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/balance"
    },
    "account": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb"
    }
  }
}
```

### Create Money Transfer
```bash
curl -v -H 'Content-Type: application/json' -d '{"debtorAccountId": "63c550e7-86f8-4fd7-8244-4f4b4f5238cb", "creditorAccountId": "c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2", "instructedAmount": {"amount":10.52, "currency":"EUR"}, "reference":"Happy Birthday" }' http://localhost:8080/transfers
```
#### Response
```
< HTTP/1.1 201 Created
< Content-Type: application/json
< Location: http://localhost:8080/transfers/f8cec13a-a82e-4baa-a13e-402d7e1084f8
```

##### Validation Error
```bash
curl -v -H 'Content-Type: application/json' -d '{"debtorAccountId": "1", "creditorAccountId": "2", "instructedAmount": {"amount":-10.25, "currency":"EUR"}, "reference":"Happy Birthday" }' http://localhost:8080/transfers
```

##### Problem Responses 
```
< HTTP/1.1 400 Bad Request
< Content-Type: application/problem+json
```
```json
{
  "type": "https://zalando.github.io/problem/constraint-violation",
  "status": 400,
  "violations": [
    {
      "field": "instructedAmount",
      "message": "must be greater than 0"
    }
  ],
  "title": "Constraint Violation"
}
```

Disallowing overdrafts:
```json
{
  "title": "Money Transfer failed",
  "status": 400,
  "detail": "Debtor account <c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2> has insufficient credit to transfer <EUR 10.52> to account <63c550e7-86f8-4fd7-8244-4f4b4f5238cb>."
}
```

### List Transactions
```bash
curl -v http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions
```
#### Response
```
< HTTP/1.1 200 OK
< Content-Type: application/json
```
```json
{
  "transactions": [
    {
      "id": "13e7e039-a76d-4b7f-9b49-ba7f426a8c5c",
      "transactionAmount": {
        "amount": 100.25,
        "currency": "EUR"
      },
      "_links": {
        "_self": {
          "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions/13e7e039-a76d-4b7f-9b49-ba7f426a8c5c"
        }
      }
    },
    {
      "id": "594b5903-b299-4a82-8f58-20a497f2a45e",
      "transactionAmount": {
        "amount": -10.52,
        "currency": "EUR"
      },
      "_links": {
        "_self": {
          "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions/594b5903-b299-4a82-8f58-20a497f2a45e"
        }
      }
    }
  ],
  "_links": {
    "_self": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions"
    },
    "account": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb"
    }
  }
}
```

### View Transaction
```bash
curl -v http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions/594b5903-b299-4a82-8f58-20a497f2a45e
```
#### Response
```
< HTTP/1.1 200 OK
< Content-Type: application/json
```
```json
{
  "id": "594b5903-b299-4a82-8f58-20a497f2a45e",
  "transactionAmount": {
    "amount": -10.52,
    "currency": "EUR"
  },
  "_links": {
    "_self": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions/594b5903-b299-4a82-8f58-20a497f2a45e"
    },
    "transactions": {
      "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions"
    }
  }
}
```


### List all money transfer between accounts
```bash
curl -v  http://localhost:8080/transfers
```
#### Response
```
< HTTP/1.1 200 OK
< Content-Type: application/json
```
```json
{
  "transfers": [
    {
      "id": "f8cec13a-a82e-4baa-a13e-402d7e1084f8",
      "debitTransaction": {
        "id": "594b5903-b299-4a82-8f58-20a497f2a45e",
        "transactionAmount": {
          "amount": -10.52,
          "currency": "EUR"
        },
        "_links": {
          "_self": {
            "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions/594b5903-b299-4a82-8f58-20a497f2a45e"
          },
          "transactions": {
            "href": "http://localhost:8080/accounts/63c550e7-86f8-4fd7-8244-4f4b4f5238cb/transactions"
          }
        }
      },
      "creditTransaction": {
        "id": "01f10ba3-a9e1-443e-b340-94c722c3f57f",
        "transactionAmount": {
          "amount": 10.52,
          "currency": "EUR"
        },
        "_links": {
          "_self": {
            "href": "http://localhost:8080/accounts/c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2/transactions/01f10ba3-a9e1-443e-b340-94c722c3f57f"
          },
          "transactions": {
            "href": "http://localhost:8080/accounts/c0fad95d-8ec4-4b1d-ba5b-c7fe737b0ff2/transactions"
          }
        }
      },
      "_links": {
        "_self": {
          "href": "http://localhost:8080/transfers/f8cec13a-a82e-4baa-a13e-402d7e1084f8"
        }
      }
    }
  ],
  "_links": {
    "_self": {
      "href": "http://localhost:8080/transfers"
    }
  }
}
```

## Tech-Stack
- build: [gradle](https://docs.gradle.org/current)
- language: [kotlin](https://kotlinlang.org/docs/reference)
- web framework: [spring boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- json (de)serialization: [jackson](https://github.com/FasterXML/jackson)

## Test-Stack
- [assertj](https://assertj.github.io/doc)
- [mockito-kotlin](https://github.com/nhaarman/mockito-kotlin)

## Out Of Scope
- auth
- api pagination
- api filtering 
- database persistence

## Limitations

## Open ToDos
- multi currency deposit and money transfers
- add idempotency key to the api 
- implement money transfer status
- validate currency of debtor/creditor accounts are same
- account policy rules (for example configurable credit/debit limits)
- request/response logging