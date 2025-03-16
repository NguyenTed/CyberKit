package com.cyberkit.cyberkit_server.controlller;


import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.LoginDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.dto.response.ResLoginDTO;
import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Value("${spring.jwt.refresh-expiration-in-seconds}")
    private Long refreshJwtExpiration;
    private final AccountService accountService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    public AuthController(AccountService accountService, AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<RestResponse<Object>> register(@RequestBody @Valid RegisterDTO registerDTO){
        UserDTO userDTO= accountService.createAccount(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body( new RestResponse<>( 201,"","Created user successfully!",userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<RestResponse<Object>> login(@RequestBody @Valid LoginDTO loginDTO){

        // Load input including username/password into Security.
        UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),
                loginDTO.getPassword()
        );
        // After load in to AuthenticationManager have to overwrite the function loadUserByUserName() and function passwordDecoder()
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Create accessToken
        String userEmail= loginDTO.getEmail();
        String accessToken = securityUtil.createAccessToken(userEmail);

        // Format ResLoginDTO response
        ResLoginDTO resLoginDTO= new ResLoginDTO();
        // Extract the user info

        UserDTO userInfo = accountService.getUserInfoByEmail(userEmail);
        // Set the user info and access token to resLoginDTO
        resLoginDTO.setUser(userInfo);
        resLoginDTO.setAccessToken(accessToken);
        // Create and update refresh token
        String refreshToken = securityUtil.createRefreshToken(loginDTO.getEmail());
        accountService.updateRefreshToken(refreshToken,loginDTO.getEmail());
        // Save cookie
        ResponseCookie responseCookie= ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshJwtExpiration)
                .build();
        return ResponseEntity.status(200)
                .header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body(new RestResponse<>( 200,"","Login successfully!",resLoginDTO));

    }

    @GetMapping("account")
    public ResponseEntity<RestResponse> getAccount() {
        String userEmail = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";
        UserDTO userInfo = accountService.getUserInfoByEmail(userEmail);
        return ResponseEntity.status(200).body(new RestResponse(200,"","Get account succesfully!",userInfo));
    }

    @GetMapping("refresh")
    public ResponseEntity<RestResponse> getRefreshToken
            (@CookieValue(name="refresh_token")String refreshToken){
        Jwt decodedToken=securityUtil.checkValidateRefreshToken(refreshToken);
        String userEmail = decodedToken.getSubject();
        if(!accountService.checkValidRefreshToken(refreshToken,userEmail))
            throw new GeneralAllException("Invalid cookie!");
        String accessToken  = securityUtil.createAccessToken(userEmail);
        String newRefreshToken = securityUtil.createRefreshToken(userEmail);
        // Format ResLoginDTO response
        ResLoginDTO resLoginDTO= new ResLoginDTO();
        // Extract the user info
        UserDTO userInfo = accountService.getUserInfoByEmail(userEmail);
        // Set the user info and access token to resLoginDTO
        resLoginDTO.setUser(userInfo);
        resLoginDTO.setAccessToken(accessToken);
        accountService.updateRefreshToken(newRefreshToken,userEmail);
        // Save cookie
        ResponseCookie responseCookie= ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshJwtExpiration)
                .build();
        return ResponseEntity.status(200)
                .header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body(new RestResponse<>( 200,"","Login successfully!",resLoginDTO));

    }
    @PostMapping("logout")
    public ResponseEntity logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        accountService.updateRefreshToken(null,authentication.getName());
        ResponseCookie deleteSpringCookie=ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body("log out thanh cong");
    }

}
