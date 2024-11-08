package ru.tinkoff.fintech.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tinkoff.fintech.user.exceptions.UnauthorizedException;
import ru.tinkoff.fintech.user.mapper.UserMapper;
import ru.tinkoff.fintech.user.repository.UserRepository;
import ru.tinkoff.fintech.utils.JwtTokenUtils;

public class JwtFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtFilter(
            UserRepository userRepository,
            JwtTokenUtils jwtTokenUtils
    ) {
        this.userRepository = userRepository;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        var jwtToken = authHeader != null ? authHeader.replace("Bearer ", "") : null;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader(
                "Access-Control-Allow-Headers",
                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, X-Auth-Token"
        );

        if (jwtToken != null && !jwtToken.isEmpty()) {
            var validationFlag = jwtTokenUtils.validateToken(jwtToken);

            try {
                if (validationFlag) {
                    var user = userRepository
                            .findById(jwtTokenUtils.getUserIdFromToken(jwtToken))
                            .orElseThrow(UnauthorizedException::new);

                    var userDto = UserMapper.entityToDto(user);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDto,
                                jwtToken,
                                List.of(new SimpleGrantedAuthority("ROLE_" + userDto.getRole()))
                        );
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (Exception e) {
                log.error("При попытке авторизации что-то пошло не так", e);
            }
        }

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
