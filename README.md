# Custom APIM Token Issuer

A sample extended `APIMTokenIssuer` implementation to append a custom value to the generated Opaque Access Tokens.

[A Medium Blog: Customizing Opaque Access Token Generation](https://medium.com/@athiththan11/customizing-opaque-access-token-generation-58ec7e493405)

## Implementation

This is a sample implementation to demonstrate on how-to extract a custom header sent to with the `Token` request and append it to the generate Opaque access token. 

The custom header used here is called as `devhash` which is a hash value as `data-urlencode` with the `Token` request.

Given below is a sample `/token` requst

```http
POST https://localhost:8243/token

Authorization: Basic <Base64 {Client ID}:{Client Secret}>
Content-Type: application/x-www-form-urlencoded

grant_type=password
username=admin
password=admin
scope=defualt
devhash=af1c4ca13ab7d6c8d2a887d7ce8250a2
```

```curl
curl --location --request POST 'https://localhost:8243/token' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --header 'Authorization: Basic <Base64 {Client ID}:{Client Secret}> \
    --data-urlencode 'grant_type=password' \
    --data-urlencode 'username=admin' \
    --data-urlencode 'password=admin' \
    --data-urlencode 'scope=default' \
    --data-urlencode 'devhash=af1c4ca13ab7d6c8d2a887d7ce8250a2'
```

And the response should be as follows...

```json
{
  "access_token": "25b9ded7-7441-3b69-bb6b-b1f1828bfff9af1c4ca13ab7d6c8d2a887d7ce8250a2",
  "refresh_token": "d86ac9b8-a3aa-3664-9d39-090ca49a9435",
  "scope": "default",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

## Build, Deploy & Run

Execute the following command to build the project

```shell
mvn clean package
```

Copy and place the built JAR artifact from the `/target/custom-apimtoken-issuer-x.x.x.jar` to the `<APIM>/repository/components/lib` directory.

Navigate to `<APIM>/repository/conf/identity/identity.xml` and edit the `<IdentityOAuthTokenGenerator>` with the custom package...

```xml
<OAuth>
  ...
  <IdentityOAuthTokenGenerator>com.sample.token.MyAPIMTokenIssuer</IdentityOAuthTokenGenerator>
  ...
</OAuth>
```
