package com.cyberkit.cyberkit_server.security;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
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
    private  AccountService accountService;
    @Autowired
    private  ToolRepository toolRepository;


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

        // if file .html .css .js return
        if(requestURI.endsWith(".html")) return true;
        if(requestURI.endsWith(".css")) return true;
        if(requestURI.endsWith(".js")) return true;

        StringBuilder stringBuilder = new StringBuilder("");
        if(requestURI.contains("-")){
            String toolUUID = requestURI.substring(requestURI.lastIndexOf("/") + 1);
            Optional<ToolEntity> toolEntity = toolRepository.findById(UUID.fromString(toolUUID));
            if(toolEntity.isEmpty()){
                throw  new GeneralAllException("Not existing this tool!");
            }
            requestURI= requestURI.substring(0, requestURI.lastIndexOf("/"));
            stringBuilder.append(requestURI);
            stringBuilder.append("/");
            stringBuilder.append(toolEntity.get().getPluginId());
        }
        else{
            stringBuilder.append(requestURI);
        }

        //check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        if(!email.equals("")){
            UserDTO userDTO = accountService.getUserInfoByEmail(email);
            if(userDTO.isPremium()|| userDTO.getRole().equals(RoleEnum.ADMIN)){
                return true;
            }
        }
        List<ToolEntity> toolEntities = toolRepository.findByPremium(true);
        List<String> premiumEndpoints =  toolEntities.stream().map(s-> ("/cyberkit/api/v1/tools/"+s.getPluginId())).toList();

        boolean isValid= true;
        // anonymous and not premium user are not allowed to use premium tool.
        requestURI= stringBuilder.toString();
        for (String endpoint: premiumEndpoints){
            if(requestURI.equals(endpoint)){
                isValid =false;
                break;
            }
        }
        if(isValid==false)
            throw new GeneralAllException("You have not permission to use this function");
        return isValid;
    }
}
