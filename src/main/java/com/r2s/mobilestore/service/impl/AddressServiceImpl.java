package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.data.dto.address.AddressDTO;
import com.r2s.mobilestore.data.dto.address.AddressShowDTO;
import com.r2s.mobilestore.data.entity.Address;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.mapper.AddressMapper;
import com.r2s.mobilestore.data.mapper.UserMapper;
import com.r2s.mobilestore.data.repository.AddressRepository;
import com.r2s.mobilestore.data.repository.OrderDetailRepository;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.*;
import com.r2s.mobilestore.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    OrderDetailRepository orderDetailRepository;


    public AddressDTO getById(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen", null, null)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Address address = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Admin"))) {
            return addressMapper.toDTO(address);
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Customer"))) {

            if (address.getUser().getId() != user.getId())
                throw new InternalServerErrorException(messageSource.getMessage("error.userAuthen", null, null));

        }

        return addressMapper.toDTO(address);

    }


    @Override
    public List<AddressShowDTO> getAllAddress() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen", null, null)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Role_Admin"))) {

            return addressRepository.findAddressByUserIdAdmin().stream().map(u -> addressMapper.toShowDTO(u)).collect(Collectors.toList());

        } else {

            return addressRepository.findAddressByUserIdCustomer(user.getId()).stream().map(u -> addressMapper.toShowDTO(u)).collect(Collectors.toList());

        }
    }

    /**
     * Method create address
     *
     * @param addressDTO
     * @return AddressUserDTO
     * @Author VoTien
     */
    public AddressDTO createAddress(AddressDTO addressDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen", null, null)));

        if (!addressRepository.existsByLocation(addressDTO.getLocation(), user.getId()).isEmpty()) {
            //check status
//            if()

            throw new ConflictException(Collections.singletonMap("id", addressDTO.getLocation()));
        }


        if (addressRepository.findAddressByUserIdCustomer(user.getId()).isEmpty()) {

            addressDTO.setDefaults(true);

        } else if (addressDTO.isDefaults()) {

            throw new BadCredentialsException("Can't set default!! please update later!");
        }

        Address address = addressMapper.toEnity(addressDTO);
        address.setUser(user);

        return addressMapper.toDTO(addressRepository.save(address));
    }

    /**
     * Method update address
     *
     * @param addressDTO
     * @param id
     * @return show AddressUserDTO if is successful
     */
    public AddressDTO updateAddress(AddressDTO addressDTO, long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen", null, null)));

        Address oldAddress = addressRepository.findAddressById(id).orElseThrow(() -> new ValidationException(Collections.singletonMap("id", id)));

        Address addressDefault = addressRepository.findDefaultAddress(user.getId());

        if (addressDefault.getId() == oldAddress.getId() && !addressDTO.isDefaults()) {

            //update address set default false, but it was the default true
            throw new BadCredentialsException("Must have at least defaults true !");
        }

        if (!addressDTO.getLocation().equals(oldAddress.getLocation())) {

            if (!addressRepository.existsByLocation(addressDTO.getLocation(), user.getId()).isEmpty()) {
                throw new ConflictException(Collections.singletonMap("id", id));
            }
        }

        if (addressDTO.isDefaults()) { //update address set true -> set addressDefaults false

            addressDefault.setDefaults(false);
        }

        Address updatAddress = addressMapper.toEnity(addressDTO);
        updatAddress.setId(oldAddress.getId());
        updatAddress.setUser(user);

        AddressDTO updateAddressDTO = addressMapper.toDTO(addressRepository.save(updatAddress));

        return updateAddressDTO;
    }

    /**
     * Method delete address by id (serviceimpl)
     *
     * @param id
     * @return return true if delete successful
     * @author huuduc
     */
    @Override
    public boolean deleteAddress(long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AccessDeniedException());

        Address foundAddress = this.addressRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        //Check if the address is in use
        if (foundAddress.isDefaults()) {
            throw new CannotDeleteException(id);
        }

        if (user.getId() != foundAddress.getUser().getId()) {
            throw new CannotDeleteException(id);
        }

        if (!orderDetailRepository.existsAddress(id).isEmpty()) {
            throw new CannotDeleteException(id);
        }

        this.addressRepository.deleteById(id);

        return true;
    }
}
