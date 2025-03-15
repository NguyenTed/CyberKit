package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.data.AbstractUserEntity;
import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;

import javax.swing.text.StyledEditorKit;

public interface AccountService {
    public UserDTO createAccount(RegisterDTO registerDTO);
    public AbstractUserEntity getUserByEmail(String email);
    public void updateRefreshToken(String token, String email);
    public Boolean existsAccountByEmail(String email);
    public UserDTO getUserInfoByEmail(String email);
    public Boolean checkValidRefreshToken(String refreshToken, String email);
}
