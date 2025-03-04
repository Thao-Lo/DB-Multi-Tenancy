package multi_tenant.db.navigation.JWT;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
//call 1 time per request
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// not running through JWTFilter if there is no JWT involve
		String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/login")) {
            filterChain.doFilter(request, response);
            return;
        }
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				Claims claims = jwtTokenProvider.validateToken(token);
				String email = claims.getSubject();
				
				//fron Json to Java Obj
				ObjectMapper objMapper = new ObjectMapper();				
				List<String> roles = objMapper.convertValue(claims.get("roles"), new TypeReference<List<String>>() {});
				
				if (roles == null) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().write("{\"error\": \"Role is not found\"}");
					return;
				}
				//Convert roles<String> top GrantedAuthority Obj
				System.out.println("jwt filter roles: " + roles.toString());
				List<GrantedAuthority> authorities = roles.stream()
													.map(role -> new SimpleGrantedAuthority(role))
													.collect(Collectors.toList());

				// create new authentication and save to SecurityContext
				if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
							null, authorities);
					// IP address of device, sessionId....
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);

				}
			} catch (ExpiredJwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\": \"Token expired\"}");
				return;
			} catch (JwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\": \"Invalid token\"}");
				return;
			}
		}
		filterChain.doFilter(request, response);

	}
}
