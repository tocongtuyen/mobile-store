package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.common.JwtUtils;
import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.data.dto.JwtResponseDTO;
import com.r2s.mobilestore.data.dto.LoginRequestDTO;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.AccessDeniedException;
import com.r2s.mobilestore.service.UserService;
import com.r2s.mobilestore.service.impl.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(ApiURL.LOGIN)
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public final Logger LOGGER = LoggerFactory.getLogger("info");
    @Value("${domain.path}")
    private String myAppUrl;

    /**
     * Method login of user
     *
     * @param loginRequestDTO
     * @return JwtResponseDTO if user the login authentication is successful
     * @Author: Haunv
     */
    @PostMapping("")
    public ResponseEntity<?> authenticateUserGoogle(@RequestBody LoginRequestDTO loginRequestDTO) throws AccessDeniedException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = this.jwtUtils.generateJwtToken(authentication);

        //get info user from authentication
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        User userDto = userService.findByEmail(user.getEmail());
        long id = userDto.getId();


        //check status account
        String message  = "error.notAvailable";
        if(userDto.isStatus())
            message = "";

        //check account lock
        if(userDto.isLock_status())
            throw new AccessDeniedException();

        //Check role for show
        List<String> roles = user.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LOGGER.info("%s has successfully logged in.", user.getEmail());

        return new ResponseEntity<JwtResponseDTO>(
                new JwtResponseDTO(jwt, user.getEmail(), roles.get(0), id, message),
                HttpStatus.CREATED);

    }
}
