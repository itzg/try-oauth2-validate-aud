package me.itzg.tryoauth2validateaud;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableConfigurationProperties(ApiSecurityProperties.class)
public class JwtConfig {

  private final static OAuth2Error audienceError = new OAuth2Error("invalid_token");

  @Bean
  JwtDecoder jwtDecoder(ApiSecurityProperties properties) {
    final String issuerUri = properties.getIssuerUri();

    final NimbusJwtDecoder jwtDecoder;
    switch (properties.getSigningAlgorithm()) {
      case RS256:
        String jwkSetUri = properties.getJwkSetUri();
        if (jwkSetUri == null || jwkSetUri.isBlank()) {
          jwkSetUri = UriComponentsBuilder.fromHttpUrl(issuerUri)
              .path(".well-known/jwks.json")
              .build()
              .toUriString();
        }
        jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
            .build();
        break;
      case HS256:
        if (properties.getKey().isBlank()) {
          throw new IllegalStateException("ApiSecurityProperties.key needs to be set for HS256");
        }
        final byte[] keyBytes = properties.getKey().getBytes();
        jwtDecoder = NimbusJwtDecoder.withSecretKey(
            new SecretKeySpec(keyBytes, "HmacSHA256")
        ).build();
        break;
      default:
        throw new IllegalStateException("ApiSecurityProperties.signingAlgorithm needs to be set");
    }

    OAuth2TokenValidator<Jwt> audienceValidator = audienceValidator(properties.getAudience());
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
    OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(
        withIssuer, audienceValidator);

    jwtDecoder.setJwtValidator(withAudience);

    return jwtDecoder;
  }

  private OAuth2TokenValidator<Jwt> audienceValidator(String audience) {
    return jwt -> {
      if (jwt.getAudience().contains(audience)) {
        return OAuth2TokenValidatorResult.success();
      }
      else {
        return OAuth2TokenValidatorResult.failure(audienceError);
      }
    };
  }
}
