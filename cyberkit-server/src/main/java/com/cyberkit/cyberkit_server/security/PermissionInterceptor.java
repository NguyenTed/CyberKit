package com.cyberkit.cyberkit_server.security;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.exception.UnauthorizedPermissionException;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import com.cyberkit.cyberkit_server.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ToolRepository toolRepository;


    @Transactional
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // Only handle api containing tool uuid
        String toolUUID = StringUtil.extractToolIdFromRequest(requestURI);
        Optional<ToolEntity> optToolEntity = toolRepository.findById(UUID.fromString(toolUUID));
        if(optToolEntity.isEmpty()){
            throw  new GeneralAllException("Not existing this tool!");
        }
        boolean isValid= true;
        if(requestURI.endsWith(".html")|| requestURI.endsWith(".js")|| requestURI.endsWith(".css")){
            return true;
        }

        String email = SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        UserDTO userDTO = null;
        if(!email.equals("anonymousUser")){
            userDTO = accountService.getUserInfoByEmail(email);
            if( userDTO.getRole().equals(RoleEnum.ADMIN)){
                return true;
            }
        }
        ToolEntity toolEntity = optToolEntity.get();
        if(!toolEntity.isEnabled()) isValid = false;
        if(toolEntity.isPremium()){
            if(userDTO == null||!userDTO.isPremium()) isValid = false;
        }

        if(isValid==false)
            throw new UnauthorizedPermissionException("You have not permission to use this function");
        return isValid;
    }
}
