# Okta oauth plugin for GoCD

## Requirements

* GoCD server version v17.5.0 or above
* Okta [API credentials](https://developer.okta.com/signup/)

## Installation

Copy the file `build/libs/okta-oauth-authorization-plugin-VERSION.jar` to the GoCD server under `${GO_SERVER_DIR}/plugins/external` 
and restart the server. The `GO_SERVER_DIR` is usually `/var/lib/go-server` on Linux and `C:\Program Files\Go Server` 
on Windows.

## Configuration

**!!! Note that the configuration for Okta might be slightly different in case you have a corporate account setup.**

###  Configure Okta API Issuer

1. Sign in to Okta [API credentials](https://developer.okta.com/signup/)
2. Click on **_API_** _>_ **_Authorization Servers_**
3. Click on **_default_** as that will be your Authorization Server 
4. Navigate to **_Scopes_** _>_ **_Add Scope_**
5. Create a scope with name _groups_ and select `Include in public metadata`
6. Navigate to **_Claims_** _>_ **_Add Claim_**
7. Create a claim with name _groups_ as following:
    1. Choose the `Token type` to be: _ID Token_
    2. Select `Value type`: _Groups_
    3. Set the `Filter` to: _Regex_ and value: `.*` (there is a dot in there)

###  Configure Okta Application

1. Sign in to Okta [API credentials](https://developer.okta.com/signup/)
2. Click on **_Applications_** and from there **_Add Application_**.
3. Select type `Web`.
4. Fill in the `Login redirect URI` as follows: `https://{your_base_url}/go/plugin/cd.go.authorization.okta/authenticate`
5. Click **_Save_** and afterwards change the `Initiate login URI` to: `https://{your_base_url}/go/plugin/cd.go.authorization.okta/login`

### Create Authorization Configuration

1. Login to `GoCD server` as admin and navigate to **_Admin_** _>_ **_Security_** _>_ **_Authorization Configuration_**.
2. Click on **_Add_** to create new authorization configuration.
    1. Specify `id` for auth config.
    2. Select `Okta oauth authorization plugin for GoCD` for **_Plugin id_**
    3. Specify your Okta API Issuer: `https://{your_okta_url}/oauth2/default`
    4. Specify **_Client ID_** and **_Client Secret_** that come from the Application.
    5. Save your configuration and you'll be redirected to GoCD login page.
3. Click on the Okta button and you should be logged in.

### Create Role Configuration

1. Login to `GoCD server` as admin and navigate to **_Admin_** _>_ **_Security_** _>_ **_Role Configuration_**.
2. Click on **_Add_** to create new role configuration.
    1. Select `Plugin Role` as the type of role.
    2. Specify the name of the role in `Role name`.
    3. _(Optional)_ Use `Okta Groups` to choose which groups will use this role.
    4. _(Optional)_ Use `Okta Users` to choose which users will use this role.
3. All your users matching the criteria will have this role associated with their account in GoCD.
