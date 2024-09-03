# Country Code Lookup Application

This Java application fetches and processes a list of country calling codes from a [Wikipedia page](https://en.wikipedia.org/wiki/List_of_country_calling_codes#Alphabetical_order) and stores them in a PostgreSQL database. The application also provides an API to look up the country corresponding to a given phone number.

## Table of Contents

- [Requirements](#requirements)
- [Setup](#setup)
    - [Database Setup](#database-setup)
    - [Application Setup](#application-setup)
- [Running tests](#running-tests)
- [Running the Application](#running-the-application)
- [After working](#after-working)
- [API Endpoints](#api-endpoints)
- [Usage](#usage)

## Requirements

Backend:
- Java (17+)
- Spring Boot (2+)
- Maven
- PostgreSQL (13+)

Frontend:
- HTML
- CSS
- JavaScript

## Setup

### Database Setup

1. **Install and setup PostgreSQL. Follow the official documentation**:
    ```bash
    https://www.postgresql.org/download/
    ```

2. **Run the SQL script to create the database and tables**:

   If you have `psql` installed, you can run:
    ```bash
    psql -U postgres -f scripts/create_database.sql
    ```
    The script will ask for user's password, use: 1234

### Application Setup

1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/country-code-lookup.git
    cd country-code-lookup
    ```

2. **Configure the application**:
    - If necessary update `application.yml` file in the `src/main/resources` directory with your database credentials.

3. **Build the project**:
    ```bash
    mvn clean install
    ```

## Running tests

To run all tests, use the following command:

```bash
mvn test
```

## Running the Application

To run the application, use the following command:

```bash
mvn spring-boot:run
```

## After working

Run the SQL script to clean the resources:
```bash
psql -U postgres -f scripts/delete_database.sql
```

## API Endpoints

```bash
GET /api/phone/country?number={phoneNumber} 
```
Returns the country or countries corresponding to the provided phone number.

## Usage

### Fetch and Save Country Codes:
The application automatically fetches and saves country codes from Wikipedia upon startup.

### Look Up a Country by Phone Number:
Example request:
    
    curl http://localhost:8088/api/phone/country?number=11165384765

Example response:
    
    {
        "country": "United States, Canada"
    }
