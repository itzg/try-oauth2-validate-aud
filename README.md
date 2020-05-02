This project tries out [customizing the Spring Security OAuth2 JWT validator](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#oauth2resourceserver-jwt-validation-custom). 

Specifically, it customizes
- The choice of RS256 or HS256, where Spring Boot resource servers [by default only support RS256](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#oauth2resourceserver-jwt-decoder-algorithm)
- Validation of the JWT token to include validation an expected audience value

The bulk of the code is in `JwtConfig` which uses the properties bean `ApiSecurityProperties`. It supports both RS256 and HS256 signing algorithms.

For RS256 the properties need to be
- `api.security.signing-algorithm=rs256`
- `api.security.issuer-uri=...`
- `api.security.audience=...`
- optionally `api.security.jwkSetUri`, if not specified ".well-known/jwks.json" is appended to `issuer-uri`

For HS256 the properties need to be
- `api.security.signing-algorithm=hs256`
- `api.security.issuer-uri=...`
- `api.security.audience=...`
- `api.security.key=...`


