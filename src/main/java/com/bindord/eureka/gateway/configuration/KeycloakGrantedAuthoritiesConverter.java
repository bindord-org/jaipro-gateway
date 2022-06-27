package com.bindord.eureka.gateway.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final String GROUPS_CLAIM = "groups";
    private static final String ROLE_PREFIX = "ROLE_";


    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.grantedAuthorityCollection(jwt);

        CustomUserDetails customUserDetails = new CustomUserDetails(
                jwt.getClaim("email"), "", true, true,
                true, true, authorities);
        customUserDetails.setRenwo(jwt.getClaim("sub"));

        return Mono.just(
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        "n/a",
                        authorities)
        );
    }

    public Collection<GrantedAuthority> grantedAuthorityCollection(Jwt source) {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.stream()
                .map(rn -> new SimpleGrantedAuthority(ROLE_PREFIX + rn.toUpperCase()))
                .collect(Collectors.toList());
    }
}
