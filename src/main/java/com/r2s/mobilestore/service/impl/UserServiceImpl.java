package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.enumeration.ERole;
import com.r2s.mobilestore.common.util.Validation;
import com.r2s.mobilestore.data.dto.ChangePasswordByOTPDTO;
import com.r2s.mobilestore.data.dto.ChangePasswordDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.user.UserCreationDTO;
import com.r2s.mobilestore.data.dto.user.UserDTO;
import com.r2s.mobilestore.data.dto.user.UserProfileDTO;
import com.r2s.mobilestore.data.entity.Status;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.mapper.RoleMapper;
import com.r2s.mobilestore.data.mapper.UserMapper;
import com.r2s.mobilestore.data.repository.RoleRepository;
import com.r2s.mobilestore.data.repository.StatusRepository;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.*;
import com.r2s.mobilestore.service.FileService;
import com.r2s.mobilestore.service.RoleService;
import com.r2s.mobilestore.service.StatusService;
import com.r2s.mobilestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private Validation validation;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AuditorAware auditorAware;

    /**
     * Method get current user by id when login success
     *
     * @return long id
     */
    @Override
    public Long getCurrentUserId() {
        return (Long) auditorAware.getCurrentAuditor().orElse(null);
    }

    /**
     * Mehod find user by email
     *
     * @param email
     * @return User
     */
    @Override
    public User findByEmail(String email) {
        if (email.isEmpty()) {
            throw new ValidationException(Collections.singletonMap("User email ", email));
        }
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new InternalServerErrorException(Collections.singletonMap(email, "Not found")));
    }

    /**
     * Method update otp of user
     *
     * @param email
     * @param otp
     */
    @Override
    public void updateTokenActive(String email, String otp) {
        Map<String, Object> errors = new HashMap<>();
        if (email.isEmpty())
            errors.put("email ", email);
        else if (otp.isEmpty())
            errors.put("otp ", otp);
        else if (!errors.isEmpty())
            throw new ResourceNotFoundException(errors);
        User user = this.findByEmail(email);
        user.setOtp(otp);

        userRepository.save(user);
    }

    /**
     * Method load user by name for authentication
     *
     * @param email the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        if (email.isEmpty()) {
            throw new ValidationException(Collections.singletonMap("User email ", email));
        }
        return UserDetailsImpl.build(findByEmail(email));
    }

    /**
     * Method find user by id
     *
     * @param id
     * @return show UserDTO if success
     */
    @Override
    public UserDTO findById(long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(Collections.singletonMap("email", email)));

        int emptyCheck = -1;
        if (id == emptyCheck)
            throw new ValidationException(Collections.singletonMap("User id ", id));
        else if (user.getId() != id)
            throw new AccessDeniedException();

        return this.userMapper.toDTO(this.userRepository.findById(id).orElseThrow(() ->
                new InternalServerErrorException(Collections.singletonMap(id, "Not found"))));
    }

    /**
     * Method active user if otp true
     *
     * @param Otp
     */
    @Override
    public void activeUser(String Otp) {
        if (Otp.isEmpty())
            throw new ValidationException(Collections.singletonMap("User Otp ", Otp));

        User user = this.userRepository.findByOtp(Otp).orElseThrow(() ->
                new InternalServerErrorException(Collections.singletonMap(Otp, "Not found")));

        user.setStatus(true);
        user.setOtp("");

        this.userRepository.save(user);
    }

    /**
     * Method check exists email
     *
     * @param email
     * @return
     */
    @Override
    public boolean existsByEmail(String email) {
        if (email.isEmpty())
            throw new ValidationException(Collections.singletonMap("User email ", email));

        return false;
    }

    /**
     * Method update password by otp if user forgot password
     *
     * @param email
     * @param otp
     */
    @Override
    public void updateTokenForgetPassword(String email, String otp) {
        Map<String, Object> errors = new HashMap<>();
        if (email.isEmpty())
            errors.put("email ", email);
        else if (otp.isEmpty())
            errors.put("otp ", otp);
        else if (!errors.isEmpty())
            throw new ResourceNotFoundException(errors);

        User user = this.findByEmail(email);
        user.setOtp(otp);

        userRepository.save(user);
    }

    /**
     * Method check valid old password for chang password
     *
     * @param oldPass
     * @param confirmPass
     * @return boolean
     */
    @Override
    public boolean checkValidOldPassword(String oldPass, String confirmPass) {
        if (confirmPass.isEmpty()) {
            throw new ValidationException(Collections.singletonMap("confirm password ", confirmPass));
        }
        return passwordEncoder.matches(confirmPass, oldPass);
    }

    /**
     * Method create user
     *
     * @param userCreationDTO
     * @return show UserDTO if success
     */
    @Override
    public UserDTO create(UserCreationDTO userCreationDTO) {
        if (Objects.isNull(userCreationDTO)) {
            throw new ResourceNotFoundException(Collections.singletonMap("Creation user ", userCreationDTO));
        }
        // check existing user info
        Map<String, Object> errors = new HashMap<String, Object>();
        if (userRepository.existsByEmail(userCreationDTO.getEmail())) {
            errors.put("email", userCreationDTO.getEmail());
        }
        if (errors.size() > 0) {
            throw new ConflictException(Collections.singletonMap("userCreationDTO", errors));
        }

        // set info for user
        User user = userMapper.toEntity(userCreationDTO);

        // set default role and status will be set default
        // base on auth provider register
        ERole defaultRole;
        boolean defaultStatus = false;
        boolean defaultLockStatus = false;


        user.setPassword(passwordEncoder.encode(userCreationDTO.getPassword()));
        user.setRole(
                roleRepository.findByName(ERole.Customer.toString())
                        .orElseThrow(() -> new InternalServerErrorException(
                                Collections.singletonMap("Role", ERole.Customer.toString()))));

        user.setStatus(defaultStatus);
        user.setLock_status(defaultLockStatus);

        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    /**
     * Method update profile user
     *
     * @param id
     * @param userProfileDTO
     * @return UserDTO
     */
    @Override
    public UserDTO update(long id, UserProfileDTO userProfileDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));

        //Authentication user want update profile
        if (user.getId() != id)
            throw new AccessDeniedException();

        User usr = this.userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Collections.singletonMap("User ID ", id)));

        //Check null userProfileDTO input
        if (Objects.isNull(userProfileDTO))
            throw new ValidationException(Collections.singletonMap("user profile dto ", userProfileDTO));

//        User userUpdate = userMapper.toEntity(userProfileDTO);
//        userUpdate.setId(usr.getId());
//        userUpdate.setAddressList(null);

        usr.setEmail(userProfileDTO.getEmail());
        usr.setFullName(userProfileDTO.getFullName());
        usr.setGender(userProfileDTO.getGender());
        usr.setBirthDay(userProfileDTO.getBirthDay());
        usr.setAddressList(null);


        return userMapper.toDTO(userRepository.save(usr));
    }

    /**
     * Method find all user
     *
     * @param no
     * @param limit
     * @return PaginationDTO
     * @author lyhai
     */
    @Override
    public PaginationDTO findAllPagination(int no, int limit) {
        Page<UserDTO> page = this.userRepository.findByStatusIsTrue(PageRequest.of(no, limit)).map(item -> userMapper.toDTO(item));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(), page.getSize(),
                page.getNumber());
    }

    /**
     * Method Find user by name
     *
     * @param keyword
     * @param no
     * @param limit
     * @return PaginationDTO
     * @author lyhai
     */
    @Override
    public PaginationDTO findAllByKeywordsPagination(String keyword, int no, int limit) {

        Page<User> userPage = userRepository.findAllByFullNameContainingOrEmailContaining(keyword, keyword, PageRequest.of(no, limit));

        List<UserDTO> users = userPage.getContent().stream().map(user -> userMapper.toDTO(user))
                .collect(Collectors.toList());

        return new PaginationDTO(users, userPage.isFirst(), userPage.isLast(), userPage.getTotalPages(),
                userPage.getTotalElements(), userPage.getSize(), userPage.getNumberOfElements());
    }


    /**
     * Method show user detail
     *
     * @return UserDTO
     */
    @Override
    public UserDTO showUserDetail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = this.userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(Collections.singletonMap("email", email)));

        return userMapper.toDTO(user);
    }


//    @Override
//    public boolean changePassword(ChangePasswordDTO changePasswordDTO) {
//        if (Objects.isNull(changePasswordDTO)) {
//            throw new ResourceNotFoundException(Collections.singletonMap("Password ", changePasswordDTO));
//        }
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email).orElseThrow(
//                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
//                        null, null)));
//
//        if (checkValidOldPassword(user.getPassword(), changePasswordDTO.getOldPassword())) {
//            if (!validation.passwordValid(changePasswordDTO.getNewPassword()))
//                throw new InternalServerErrorException(messageSource.getMessage("error.passwordRegex",
//                        null, null));
//            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
//            userRepository.save(user);
//        } else {
//            throw new InvalidExcelVersionException(
//                    Collections.singletonMap(user.getPassword(), messageSource.getMessage("error.passwordIncorrect",
//                            null, null)));
//        }
//
//        return true;
//    }


    /**
     * lock user
     *
     * @param id
     * @return UserDTO
     * @author lyhai
     */
    @Override
    public UserDTO disable(long id) {
        User usr = this.userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Collections.singletonMap("User ID ", id)));

        usr.setLock_status(true);
        usr.setAddressList(null);

        userRepository.save(usr);

        return userMapper.toDTO(usr);
    }

//    @Override
//    public boolean changePassword(ChangePasswordDTO changePasswordDTO) {
//        if (Objects.isNull(changePasswordDTO)) {
//            throw new ResourceNotFoundException(Collections.singletonMap("Password ", changePasswordDTO));
//        }
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email).orElseThrow(
//                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
//                        null, null)));
//
//        if (checkValidOldPassword(user.getPassword(), changePasswordDTO.getOldPassword())) {
//            if (!validation.passwordValid(changePasswordDTO.getNewPassword()))
//                throw new InternalServerErrorException(messageSource.getMessage("error.passwordRegex",
//                        null, null));
//            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
//            userRepository.save(user);
//        } else {
//            throw new InvalidExcelVersionException(
//                    Collections.singletonMap(user.getPassword(), messageSource.getMessage("error.passwordIncorrect",
//                            null, null)));
//        }
//
//        return true;
//    }

    /**
     * Method change password user (pre login successful)
     *
     * @param changePasswordDTO
     * @return Returns an "ok" response if the address update is successful
     */
    @Override
    public boolean changePasswordByToken(ChangePasswordDTO changePasswordDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthenr",
                        null, null)));

        String oldPassword = user.getPassword();
        String newPassword = changePasswordDTO.getNewPassword();

        // Check newPassword is similar to oldPassword
        if (checkValidOldPassword(oldPassword, newPassword))
            throw new InternalServerErrorException(messageSource.getMessage("error.passwordNotSimilar",
                    null, null));

        // Check confirm old password similar to old password
        if (checkValidOldPassword(oldPassword, changePasswordDTO.getOldPassword())) {
            if (!validation.passwordValid(newPassword))
                throw new InternalServerErrorException(messageSource.getMessage("error.passwordRegex",
                        null, null));

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setAddressList(null);

            userRepository.save(user);

        } else {
            throw new InvalidExcelVersionException(
                    Collections.singletonMap(oldPassword, messageSource.getMessage("error.passwordIncorrect",
                            null, null)));
        }

        return true;
    }

    /**
     * Method change password by otp if user forgot password
     *
     * @param changePasswordByOTPDTO
     */
    @Override
    public void changePasswordByOTP(ChangePasswordByOTPDTO changePasswordByOTPDTO) {
        if (Objects.isNull(changePasswordByOTPDTO)) {
            throw new ResourceNotFoundException(Collections.singletonMap("Password ", changePasswordByOTPDTO));
        }
        User user = this.userRepository.findByOtp(changePasswordByOTPDTO.getOtp()).orElseThrow(
                () -> new InternalServerErrorException(
                        this.messageSource.getMessage("error.otp", null, null)));

        if (!this.validation.passwordValid(changePasswordByOTPDTO.getNewPassword())) // Check Password is strong
            throw new InternalServerErrorException(
                    messageSource.getMessage("error.passwordRegex", null, null));
        user.setPassword(new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 10)
                .encode(changePasswordByOTPDTO.getNewPassword()));
        user.setOtp("");
        this.userRepository.save(user);
    }


    /**
     * Method check status user
     *
     * @param status
     * @return message
     */
    @Override
    public String checkStatusUser(Status status) {
        if (Objects.isNull(status)) {
            throw new ResourceNotFoundException(Collections.singletonMap("status ", status));
        }
        String message = "";
        switch (status.getId()) {
            case 1:
                message = "";
                break;
            case 2:
                message = messageSource.getMessage("error.notAvailable", null, null);
                break;
            case 3:
                message = messageSource.getMessage("error.lockUser", null, null);
                break;
        }
        return message;
    }
}