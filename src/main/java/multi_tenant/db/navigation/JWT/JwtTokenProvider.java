package multi_tenant.db.navigation.JWT;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {	
	
	private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("e9aANUQmjLR9k767rsb0Mj39GWtyy7jg1LlBjVMDvOM=".getBytes());
	private final long ACCESS_TOKEN_VALIDITY = 3600_000;
	private final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 3600_000;

	public String generateToken(String email, List<String> roles, long validity) {
		return Jwts.builder()			
				.setSubject(email)
				.claim("roles", roles) //serialize to JSON format 
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + validity))
				.signWith(SECRET_KEY) 
				.compact();

	}

	public String generateAccessToken(String email,List<String> roles) {
		return generateToken(email, roles, ACCESS_TOKEN_VALIDITY);
	}

	public String generateRefreshToken(String email, List<String> roles) {
		return generateToken(email,roles, REFRESH_TOKEN_VALIDITY);
	}

	public Claims validateToken(String token) {
		try {
		return  Jwts.parserBuilder()
				.setSigningKey(SECRET_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody();
		}catch (JwtException e) {
			throw new IllegalArgumentException("Invalid JWT token");
		}
				
	}
	//logout, take token then calculate remaining time from now to expired time
	public Long getRemainingTokenTTL(String token) {
		Date expiration = Jwts.parserBuilder()
								.setSigningKey(SECRET_KEY)
								.build()
								.parseClaimsJws(token)
								.getBody()
								.getExpiration();
		long now = System.currentTimeMillis();
		return (expiration.getTime() - now)/1000; //calculate by sec
	}
}
