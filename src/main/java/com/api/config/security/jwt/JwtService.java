package com.api.config.security.jwt;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.api.exception.BaseException;
import com.api.utils.ConstantUtils;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${application.jwt.secret-key}")
    private String secretKey;

    @Value("${application.jwt.expiration}")
    public Long jwtExpiration;

    @Value("${application.jwt.refresh-token.expiration}")
    public Long jwtRefreshExpiration;

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails, jwtRefreshExpiration);
    }

    // checking
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    // end checking

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver ){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    // private method
    private String generateToken( Map<String, Object> extractClaims, UserDetails userDetails, long expiration){
        return buildToken(extractClaims, userDetails, expiration);
    }

    private String buildToken(Map<String, Object> extractClaims, UserDetails userDetails, Long expiration){
        return Jwts.builder()
        .setClaims(extractClaims)
        .setSubject(userDetails.getUsername())
        .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .setIssuedAt(new Date( System.currentTimeMillis() ))
        .setExpiration(new Date(System.currentTimeMillis() + expiration ))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256 )
        .compact();
    }

    private Claims extractAllClaims(String token){


        Claims claims = null;

        try{
            claims = Jwts
                    .parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            throw new BaseException(ConstantUtils.SC_UA, "Token Expiration");
        }catch (UnsupportedJwtException e){
            throw new BaseException(ConstantUtils.SC_UA, "Token's not Support");
        }catch (MalformedJwtException e){
            throw new BaseException(ConstantUtils.SC_UA, "Invalid Form 3 Part Of Token");
        }catch (SignatureException e){
            throw new BaseException(ConstantUtils.SC_UA, "Invalid Form Token");
        }catch (Exception e){
            throw new BaseException(ConstantUtils.SC_UA, e.getLocalizedMessage());
        }


        return claims;
    }

    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    private Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }
}
