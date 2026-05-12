package com.erp.services.implemented;

import com.erp.config.SecurityUtil;
import com.erp.dto.ChangePasswordDTO;
import com.erp.dto.FirstPasswordChangeDTO;
import com.erp.dto.UserDTO;
import com.erp.enities.Role;
import com.erp.enities.Users;
import com.erp.enums.UserStatus;
import com.erp.repositories.RoleRepository;
import com.erp.repositories.UsersRepository;
import com.erp.services.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {

    private final UsersRepository userRepo;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;


    private void createInitRole(){
        Role admin = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {

                    Role newRole = new Role();

                    newRole.setName("ADMIN");
                    return roleRepository.save(newRole);
                });
        Role hr = roleRepository.findByName("HR")
                .orElseGet(()->{
                    Role newRoleHr = new Role();
                    newRoleHr.setName("HR");
                    return roleRepository.save(newRoleHr);
                });
        Role employee = roleRepository.findByName("EMPLOYEE")
                .orElseGet(()->{
                    Role newRoleEmployee = new Role();
                    newRoleEmployee.setName("EMPLOYEE");
                    return roleRepository.save(newRoleEmployee);
                });

    }


    @Override
    public Users createUser(Users user) {

        user.setCreated_at(LocalDate.now());

        return userRepo.save(user);
    }

    @Override
    public List<Users> getAllUser() {

        return userRepo.findAll();
    }

    @Override
    public void initUserAndRole() {
        createInitRole();

        if (userRepo.findByUserName(
                "noyan@gmail.com"
        ).isPresent()) {

            return;
        }

        Role admin = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {

                    Role newRole = new Role();

                    newRole.setName("ADMIN");

                    return roleRepository.save(newRole);
                });

        Users adminUser = new Users();

        adminUser.setUserName("noyan@gmail.com");
        adminUser.setName("Noyan Ahamed");

        adminUser.setPassWord(
                passwordEncoder.encode("123456")
        );

        Set<Role> adminRoles = new HashSet<>();

        adminRoles.add(admin);

        adminUser.setRoles(adminRoles);

        adminUser.setPasswordChanged(false);

        adminUser.setStatus(UserStatus.ACTIVE);

        adminUser.setCreated_at(LocalDate.now());

        userRepo.save(adminUser);
    }

    @Override
    public void uploadProfileImage(
            MultipartFile file
    ) throws IOException {

        Users currentUser =
                securityUtil.getCurrentUser();

        if (file != null && !file.isEmpty()) {

            currentUser.setProfileImage(
                    file.getBytes()
            );

            currentUser.setProfileImageType(
                    file.getContentType()
            );

            userRepo.save(currentUser);
        }
    }

    @Override
    public UserDTO getCurrentUser() {

        Users user =
                securityUtil.getCurrentUser();

        String base64Image = null;

        if (user.getProfileImage() != null) {

            base64Image =
                    Base64.getEncoder()
                            .encodeToString(
                                    user.getProfileImage()
                            );
        }

        return UserDTO.builder()
                .name(user.getName())
                .username(user.getUsername())
                .email(
                        user.getEmployee() != null
                                ? user.getEmployee().getEmail()
                                : user.getUsername()
                )
                .status(user.getStatus().name())
                .createdAt(user.getCreated_at().toString())
                .imageBase64(base64Image)
                .imageType(user.getProfileImageType())
                .build();
    }

    @Override
    public void firstLoginPasswordChange(
            FirstPasswordChangeDTO dto
    ) {

        Users user = securityUtil.getCurrentUser();

        if(user.isPasswordChanged()){

            throw new RuntimeException(
                    "Password already changed"
            );
        }

        if(!dto.getNewPassword()
                .equals(dto.getConfirmPassword())){

            throw new RuntimeException(
                    "Passwords do not match"
            );
        }

        user.setPassWord(
                passwordEncoder.encode(
                        dto.getNewPassword()
                )
        );

        user.setPasswordChanged(true);

        userRepo.save(user);
    }

    @Override
    public void changePassword(
            ChangePasswordDTO dto
    ) {

        Users user = securityUtil.getCurrentUser();

        // current password check
        boolean matches =
                passwordEncoder.matches(
                        dto.getCurrentPassword(),
                        user.getPassword()
                );

        if(!matches){

            throw new RuntimeException(
                    "Current password incorrect"
            );
        }

        // confirm password check
        if(!dto.getNewPassword()
                .equals(dto.getConfirmPassword())){

            throw new RuntimeException(
                    "Passwords do not match"
            );
        }

        user.setPassWord(
                passwordEncoder.encode(
                        dto.getNewPassword()
                )
        );

        user.setPasswordChanged(true);

        userRepo.save(user);
    }


}