package com.websecurity.websecurity.security.jwt;

import com.websecurity.websecurity.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenUtil {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    private static final String AUDIENCE_WEB = "web";
    @Value("Shuttle-back")
    private String APP_NAME;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expirationDateInMs}")
    private int JWT_EXPIRATION;
    // Naziv headera kroz koji ce se prosledjivati JWT u komunikaciji server-klijent
    @Value("Authorization")
    private String AUTH_HEADER;

    //	private static final String AUDIENCE_UNKNOWN = "unknown";
    //	private static final String AUDIENCE_MOBILE = "mobile";
    //	private static final String AUDIENCE_TABLET = "tablet";
    @Value("${jwt.refreshExpirationDateInMs}")
    private int REFRESH_EXPIRATION;
    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;


    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * @param email
     * @param authorities
     * @return JWT token
     */
    public String generateToken(String id, String email, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(email)
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .setExpiration(generateJWTExpirationDate())
                .claim("id", id)
                .claim("role", authorities)
                .signWith(SIGNATURE_ALGORITHM, secret).compact();


    }

    public String generateRefreshToken(String id, String email) {

        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(email)
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .setExpiration(generateRefreshExpirationDate())
                .claim("id", id)
                .signWith(SIGNATURE_ALGORITHM, secret).compact();


    }

    /**
     * @return Tip uređaja.
     */
    private String generateAudience() {

        //	https://spring.io/projects/spring-mobile

//        	String audience = AUDIENCE_UNKNOWN;
//        		if (device.isNormal()) {
//        			audience = AUDIENCE_WEB;
//        		} else if (device.isTablet()) {
//        			audience = AUDIENCE_TABLET;
//        		} else if (device.isMobile()) {
//        			audience = AUDIENCE_MOBILE;
//        		}

        return AUDIENCE_WEB;
    }

    /**
     * @return date until valid
     */
    private Date generateJWTExpirationDate() {
        return new Date(new Date().getTime() + JWT_EXPIRATION);
    }

    private Date generateRefreshExpirationDate() {
        return new Date(new Date().getTime() + REFRESH_EXPIRATION);
    }

    /**
     * @param request HTTP request
     * @return JWT token
     */
    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        // Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).replace("\"", "");
        }

        return null;
    }

    /**
     * @param token JWT token.
     * @return username or null
     */
    public String getEmailFromToken(String token) {
        String username;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }

        return username;
    }

    /**
     * @param token JWT token.
     * @return date issued
     */
    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    /**
     * @param token JWT token.
     * @return audience
     */
    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    /**
     * @param token JWT token.
     * @return date until valid
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            expiration = null;
        }

        return expiration;
    }

    /**
     * @param token JWT token.
     * @return .
     */
    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.replace("\"", ""))
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            claims = null;
        }


        return claims;
    }


    /**
     * @param token       JWT token.
     * @param userDetails user details
     * @return is it valid or not
     */

//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = getEmailFromToken(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);


        return (username != null
                && username.equals(userDetails.getUsername()));

    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }


    public int getExpiredIn() {
        return JWT_EXPIRATION;
    }


    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

    public String getUsernameFromToken(String token) {
        String username;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }

        return username;
    }
}
