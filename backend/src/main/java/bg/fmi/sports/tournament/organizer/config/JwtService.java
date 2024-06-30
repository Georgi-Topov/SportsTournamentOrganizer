package bg.fmi.sports.tournament.organizer.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final long JWT_TERM = 1000 * 60 * 24 * 60;

    private static final String SECRET_KEY = "v+OIJ0Qg9A5zEWUWP5SCng8JpP+8/SoDBUkqHuVYF/ZgDp48Prts9G/KRc61DfnTOg6EbufjWCRdeRzOcZOEMbvxiC1D7KLrsYzTHzLJduY/qwJvVDGP5ldj/1INgU320SecR7edApK0lvepEsaGwvI9LDCO0WaZL6I58woaZL+LSpPCYhZLn4nXHpftB/cnoELa2QAwsg8q0In2jSViGMW2ArXCf8tA8y7ExOjQiqXo+vp8I/bYoPz7LClrpwn6belazbK45i4v+EZLvNLWgki6OCN0MTDSz221dhBsbfaiBNC7WlJ5YyxqHEyvrNLp5JUxABuZoAGZwqh7TcYIgK8kV3P3z/+9lTLOG0KdvQp3QRDl3H/yUP9i4QtO5wwVohmMnQ8wkXy5TxaBzl1A6j7nRKqav93Y7VIs7ZpYzZ08Yqj2CdmoBLGTeo8OdvaEOLIzlA9Kxm45pzCpzUs7vIXvfLrYXWI2Xyn/yCu+JuFjIqoqwN3c4Ch1pbPCymIY/CpWZYx3kdABBNY6WhrrqhVrG10nad1VwdIE4OFNc58qxhzJCqou8qhiz3Ha+M32p4O+B0asOs1AEzyLRJt7hN4defJzBOJ8PGQg6Cp6/kM/Qad6AI3g6Wvjzm4v9GR0/r/Z1bHf/xKaHHvEG1GCw3czUmr2CKYZvhsBAV7n6fQlpABmVRjGr4zt22JcHvHi";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, new HashMap<>());
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TERM))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean tokenValidator(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
