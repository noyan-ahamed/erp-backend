package com.erp.services;

import com.erp.dto.ChangePasswordDTO;
import com.erp.dto.FirstPasswordChangeDTO;
import com.erp.dto.UserDTO;
import com.erp.enities.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    Users createUser(Users user);
    List<Users> getAllUser();

   void initUserAndRole();
    void uploadProfileImage(
            MultipartFile file
    ) throws IOException;

    UserDTO getCurrentUser();

    void firstLoginPasswordChange(
            FirstPasswordChangeDTO dto
    );

    void changePassword(
            ChangePasswordDTO dto
    );
}
