package com.cyberkit.cyberkit_server.util;


import com.cyberkit.cyberkit_server.data.AbstractUserEntity;
import com.cyberkit.cyberkit_server.data.AdminEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private  final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;


    @Value("${spring.jwt.access-expiration-in-seconds}")
    private Long accessJwtExpiration;

    @Value("${spring.jwt.refresh-expiration-in-seconds}")
    private Long refreshJwtExpiration;





    public SecurityUtil(JwtEncoder jwtEncoder, AccountService accountService, ModelMapper modelMapper) {
        this.jwtEncoder = jwtEncoder;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }


    public String createAccessToken(Authentication authentication){
        Instant now = Instant.now();
        Instant validity = now.plus(accessJwtExpiration, ChronoUnit.SECONDS);

        String userEmail = SecurityUtil.getCurrentUserLogin().isPresent()?
                SecurityUtil.getCurrentUserLogin().get(): "";

        JwtClaimsSet claims= JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(s->s.getAuthority())
                        .collect(Collectors.toList()) )
                .build();

        JwsHeader jwsHeader= JwsHeader.with(JWT_ALGORITHM).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }
    public String createRefreshToken(String email){
        Instant now = Instant.now();
        Instant validity = now.plus(refreshJwtExpiration, ChronoUnit.SECONDS);

        String userEmail = SecurityUtil.getCurrentUserLogin().isPresent()?
                SecurityUtil.getCurrentUserLogin().get(): "";
        AbstractUserEntity userEntity = accountService.getUserByEmail(userEmail);
        UserDTO userInfo = modelMapper.map(userEntity, UserDTO.class);
        userInfo.setRole(userEntity instanceof AdminEntity ? RoleEnum.ADMIN : RoleEnum.USER);


        JwtClaimsSet claims= JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInfo)
                .build();

        JwsHeader jwsHeader= JwsHeader.with(JWT_ALGORITHM).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUserLogin(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication){
        if(authentication==null){
            return null;
        } else if(authentication.getPrincipal() instanceof UserDetails springSecurityUser){
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof  Jwt jwt){
            return jwt.getSubject();
        } else if(authentication.getPrincipal() instanceof String s){
            return s;
        }
        return null;
    }


    public static Optional<String> getCurrentUserJWT(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }
}
