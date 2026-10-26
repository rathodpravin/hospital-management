package com.hospital.config;

import com.hospital.util.JwtUtil;
import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
/**
 * Reads "Authorization: Bearer <token>", validates it and logs the user in for
 * this request.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final JwtUtil jwt;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			try {
				Claims c = jwt.parse(header.substring(7));
				var auth = new UsernamePasswordAuthenticationToken(c.getSubject(), null,
						List.of(new SimpleGrantedAuthority("ROLE_" + c.get("role", String.class))));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (JwtException | IllegalArgumentException ignored) {
				/* invalid/expired token => stays anonymous => 401 */ }
		}
		chain.doFilter(req, res);
	}
}
