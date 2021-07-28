# Auth System
![Build Status](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white) ![Build Status](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white) ![BuildStatus](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

This is a simple authentication and authorization system made with Spring. 
It returns a JWT access and refresh token, which enables the user to get his access back.
The expiration time can be defined on the application.properties file.

## Features
- Register simple users
- Authenticate with JWT token
- Refresh JWT token
- Test you authentication in /api/hello endpoint

## Getting Started
To run this application all you need is:
- JAVA 11
- MySql 8

After that enter your database configuration in the application-dev.properties file and that is it!!!
