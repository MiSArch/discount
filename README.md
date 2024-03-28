# Discount Service

The discount service provides the bounded context `Discount`. It is responsible for managing discounts, coupons, and their usages by users.

## Documentation

Detailed information about the discount service can be found in the [documentation](https://misarch.github.io/docs/docs/dev-manuals/services/discount).


## Getting started

A development version of the discount service can be started using docker compose:

```bash
docker-compose -f docker-compose.dev.yml up --build
```
A GraphiQL interface is available at http://localhost:8080/graphiql to interact with the service.

> [!NOTE]
> Running the service locally through the IDE is neither recommended nor supported.