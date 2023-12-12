package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.service.MailService;
import com.r2s.mobilestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(ApiURL.MAIL)
public class MailController {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;

    /**
     * Method send mail active to user account
     *
     * @param email
     * @return Returns an "ok" response if the address update is successful
     * @Author: Haunv
     */
    @GetMapping("/active-user")
    public ResponseEntity<?> sendMailActive(@RequestParam String email) {
        return ResponseEntity.ok(mailService.sendMailActive(email));
    }

    /**
     * Method send mail otp for change password to user account
     *
     * @param email
     * @return Returns an "ok" response if the address update is successful
     * @Author: Haunv
     */
    @GetMapping("/forgot-password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email) {
        return ResponseEntity.ok(mailService.sendMailForgotPassword(email));
    }
}
