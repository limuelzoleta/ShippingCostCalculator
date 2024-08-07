# Project Name

## Overview

A brief description of your project, what it does, and its main features.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Swagger UI](#swagger-ui)
5. [Running the Application](#running-the-application)
6. [Building the Project](#building-the-project)
7. [Running Tests](#running-tests)

## Getting Started

Instructions to get a copy of the project up and running on your local machine for development and testing purposes.

## Prerequisites

List of software and tools you need to install before you can run the application.

- Java 22
- Maven 3.6+

## Installation

Step-by-step guide on how to install and set up the project.

```sh
# Clone the repository
git clone https://github.com/limuelzoleta/ShippingCostCalculator.git

# Navigate to the project directory
cd ShippingCostCalculator

# Install dependencies
mvn clean install
```

# Configuration

Details about the configuration settings of the application, found
in `application.yaml` | `application-nonprod.yaml` | `application-prod.yaml`.

```yaml
spring:
  application:
    name: ShippingCostCalculator

server:
  port: 8080

springdoc:
  api-docs:
    path: /openapi.json
  swagger-ui:
    path: /docs
  show-actuator: true
  writer-with-default-pretty-printer: true

shipping:
  cost:
    multiplier:
      large: ${large_parcel_cost_multiplier}
      medium: ${medium_parcel_cost_multiplier}
      small: ${small_parcel_cost_multiplier}
      heavy: ${heavy_parcel_cost_multiplier}
    threshold:
      maxWeightLimit: ${max_weight_limit}
      heavyParcelWeightLimit: ${heavy_weight_limit}
      smallParcelVolumeLimit: ${small_volume_limit}
      mediumParcelVolumeLimit: ${medium_volume_limit}

voucher:
  api:
    endpoint: https://mynt-exam.mocklab.io/voucher/
    key: ${voucher_api_key}
```

# Swagger UI

You can view the API documentation and test the endpoints using this [link](http://localhost:8080/swagger-ui/index.html)
or if you are running on a different port Swagger-UI can be found at

`http://{hostname}:{port}/swagger-ui/index.html`

# Running the Application

Instructions on how to run the application.

```sh
mvn spring-boot:run
```

Or, you can build the project and run the jar file:

```sh
# Build the project
mvn clean package

# Run the jar file
java -jar target/your-app-name.jar
```

# Building the Project

Explanation on how to build the project.

```sh
# Build the project
mvn clean package
```

# Running Tests

Instructions on how to run tests.

```sh
# Run unit tests
mvn test

# Run integration tests
mvn verify
```
