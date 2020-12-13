package com.slamine.getaway.security.filter;

import com.netflix.zuul.context.RequestContext;
import com.slamine.core.property.JwtConfiguration;
import com.slamine.security.filter.JwtTokenAuthorizationFilter;
import com.slamine.security.token.converter.TokenConverter;
import com.slamine.security.util.SecurityContextUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

public class GatewayJwtTokenAuthorizationFilter extends JwtTokenAuthorizationFilter {

    public GatewayJwtTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
        super(jwtConfiguration, tokenConverter);
    }

    @lombok.SneakyThrows
    @Override
    @SuppressWarnings("Duplicates")
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(jwtConfiguration.getHeader().getName());

        if(header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())){//Filter request that doesn't need authentication. e.g: login
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

        String signedToken = tokenConverter.decryptToken(token);

        tokenConverter.validateTokenSignature(signedToken);

        SecurityContextUtil.setSecurityContext(com.nimbusds.jwt.SignedJWT.parse(signedToken));

        if(jwtConfiguration.getType().equalsIgnoreCase("signed")){
            RequestContext.getCurrentContext().addZuulRequestHeader("Authorization", jwtConfiguration.getHeader().getPrefix() + signedToken);
        }

        filterChain.doFilter(request, response);
    }
}
