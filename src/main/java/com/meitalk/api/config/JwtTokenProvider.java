package com.meitalk.api.config;

import com.meitalk.api.common.enums.Role;
import com.meitalk.api.exception.CustomJwtException;
import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.model.user.JwtUser;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

/**
 * JWT resolver
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String resolveToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader("Authorization");
    }

    public boolean validateToken(String jwtToken, Role role) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(this.tokenBearer(jwtToken));
            if (role.equals(Role.STREAMER) && !claims.getBody().get("roles").equals(Role.STREAMER.name())) {
                throw new StreamException(ResponseCodeEnum.ACCESS_DENIED);
            }
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new CustomJwtException(ResponseCodeEnum.EXPIRED_TOKEN);
        } catch (SecurityException | MalformedJwtException e) {
            log.error(e.getMessage());
            throw new CustomJwtException(ResponseCodeEnum.WRONG_TYPE_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error(e.getMessage());
//            request.setAttribute("exception", ExceptionCode.UNSUPPORTED_TOKEN.getCode());
            throw new CustomJwtException(ResponseCodeEnum.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
//            request.setAttribute("exception", ExceptionCode.WRONG_TOKEN.getCode());
            throw new CustomJwtException(ResponseCodeEnum.WRONG_TOKEN);
        } catch (StreamException e) {
            throw new CustomJwtException(ResponseCodeEnum.ACCESS_DENIED);
        } catch (Exception e) {
            log.error(e.getMessage());
//            request.setAttribute("exception", ExceptionCode.UNKNOWN_ERROR.getCode());
            throw new CustomJwtException(ResponseCodeEnum.UNKNOWN_ERROR);
        }
    }

    public JwtUser getClaim(String jwtToken){
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(this.tokenBearer(jwtToken));
        Claims claimsBody = claims.getBody();
        return JwtUser.builder()
                .email(claimsBody.getSubject())
                .name(claimsBody.getOrDefault("name","").toString())
                .role(claimsBody.getOrDefault("roles","none").toString())
                .userNo(Long.parseLong(claimsBody.getOrDefault("userId","none").toString()))
                .build();
    }

    // Remove Bearer
    public String tokenBearer(String token) {
        return token.split("Bearer ")[1];
    }

}
