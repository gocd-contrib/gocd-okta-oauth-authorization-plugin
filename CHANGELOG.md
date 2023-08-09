## 2.0.0 - 2023-08-09

*   Switch to the Authorization extension API version 2.0
*   Add a basic devcontainer for easier builds in the future

## 1.1.0 - 2020-06-11

Switching the Okta client ID variable for secure to plain text one. The client ID is present in the authentication and refresh tokens requests to Okta. Having it encrypted resulted in bad requests which is now no longer the case.

- Fix for the Okta Client ID [#6](https://github.com/szamfirov/gocd-okta-oauth-authorization-plugin/pull/6)

## 1.0.0 - 2018-03-12

Initial release of plugin

- Supports authentication of users
- Supports authorization of users
