package com.JobBazaar.Backend.JwtToken;

import com.JobBazaar.Backend.Dto.UserDto;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class JwtTokenService {

    @Value("${security.jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    private final Logger LOGGER = Logger.getLogger(JwtTokenService.class.getName());

    public String createJwtToken(UserDto userDto){
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(12 * 3600))
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

            //get claims
            Map<String, Object> decodedJwtClaims = decodedJwt.getClaims();
            String issuer = (String) decodedJwtClaims.get("iss");
            String subject = (String) decodedJwtClaims.get("subj");

            //check issuer
            if (!issuer.equals(jwtIssuer)) {
                throw new JwtException("Invalid issuer");
            }
            return true; //token is valid
        }
        catch (JwtException e){
            LOGGER.warning("Invalid token: " + e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token){
        Jwt decodedJwt = jwtDecoder().decode(token);
        String email = decodedJwt.getSubject();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        User userDetails  = new User(email, "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }
}
