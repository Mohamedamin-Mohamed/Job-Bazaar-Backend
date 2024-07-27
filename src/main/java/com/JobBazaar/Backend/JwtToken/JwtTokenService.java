package com.JobBazaar.Backend.JwtToken;

import com.JobBazaar.Backend.Dto.UserDto;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.logging.Logger;
@Service
public class JwtToken {

    @Value("${security.jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    private Logger LOGGER = Logger.getLogger(JwtToken.class.getName());
    public String createJwtToken(UserDto userDto){
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(24 * 3600))
                .subject(userDto.getEmail())
                .claim("role", userDto.getRole())
                .build();

        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey.getBytes()));
        var params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        return encoder.encode(params).getTokenValue();
    }

    public JwtDecoder jwtDecoder(){
        var secretKey = new SecretKeySpec(jwtSecretKey.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    public boolean verifyJwtToken(String token){
        try {
            JwtDecoder jwtDecoder = jwtDecoder();
            Jwt decodedJwt = jwtDecoder.decode(token);

            //check claims
            if (!decodedJwt.getIssuer().equals(jwtIssuer)) {
                throw new JwtException("Invalid issuer");
            }
            return true; //token is valid
        }
        catch (JwtException e){
            LOGGER.warning("Invalid token: " + e.getMessage());
            return false;
        }
    }
}
