package com.slamine.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import com.slamine.core.model.ApplicationUser;
import com.slamine.core.property.JwtConfiguration;
import com.slamine.security.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtConfiguration jwtConfiguration;
    private final TokenCreator tokenCreator;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempting authentication ...");
        ApplicationUser applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
        if(applicationUser == null){
            throw new UsernameNotFoundException("Unable to retrieve username or password");
        }

        log.info("Creating the authentication object for the user calling userDetailsServiceImpl loadUserByUsername", applicationUser.getUsername());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
        usernamePasswordAuthenticationToken.setDetails(applicationUser);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.info("Authentication was successfully for the user '{}' generating JWE token", authResult.getName());
        SignedJWT signedJWT = tokenCreator.createSignedJWT(authResult);
        String encryptedToken = tokenCreator.encryptToken(signedJWT);
        log.info("Token generated successfully, adding it to the response header");

        //enabling javascript to get these values
        response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtConfiguration.getHeader().getName());

        response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + encryptedToken);
        super.successfulAuthentication(request, response, chain, authResult);
    }

}
