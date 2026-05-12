package com.erp.config;

import com.erp.enities.*;
import com.erp.repositories.RoleRepository;
import com.erp.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
//    public AuthenticationResponse register(RegisterRequest registerRequest) {
////        Role userRole = roleRepository.findByName("ADMIN")
////                .orElseThrow(() -> new RuntimeException("Role not found"));
////        var user = User.builder()
////                .username(registerRequest.getEmail())
////                .password(passwordEncoder.encode(registerRequest.getPassword()))
////                .roles(Set.of(userRole))
////                .build();
////
////        usersRepository.save(user);
////
////        var jwtToken = jwtService.generateToken(user);
//
//
//
//        // default role (e.g. USER)
//        Role userRole = roleRepository.findByName(registerRequest.getRole())
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//
//        Users user = new Users();
//        user.setUserName(registerRequest.getEmail());
//        user.setPassWord(passwordEncoder.encode(registerRequest.getPassword()));
//        user.setRoles(Set.of(userRole));
//
//        usersRepository.save(user);
//        var jwtToken = jwtService.generateTokenWithUserDetails(user);
//
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .roles(
//                user.getRoles()
//                        .stream()
//                        .map(Role::getName)
//                        .toList()
//                )
//                .build();
//
//    }



    //from authentication response entity
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUserName(),
                        authenticationRequest.getUserPassword()
                )
        );
        var user = usersRepository.findByUserName(authenticationRequest.getUserName()).orElseThrow();

        var jwtToken = jwtService.generateTokenWithUserDetails(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .roles(
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .toList()
                )
                .passwordChanged(user.isPasswordChanged())
                .build();
    }
}
