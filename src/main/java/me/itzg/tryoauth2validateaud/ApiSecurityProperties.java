package me.itzg.tryoauth2validateaud;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("api.security")
@Data
public class ApiSecurityProperties {
  public enum SigningAlgorithm {
    RS256,
    /**
     * HMAC-SHA256 digest
     */
    HS256
  }

  SigningAlgorithm signingAlgorithm = SigningAlgorithm.RS256;
  String issuerUri;
  String jwkSetUri;
  String audience;

  /**
   * When using {@link SigningAlgorithm#HS256} this is the required signing secret/key.
   */
  String key;
}
