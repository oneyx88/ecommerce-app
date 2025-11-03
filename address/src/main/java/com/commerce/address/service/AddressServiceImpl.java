package com.commerce.address.service;

import com.commerce.address.dto.AddressRequest;
import com.commerce.address.dto.AddressResponse;
import com.commerce.address.exceptions.ApiException;
import com.commerce.address.exceptions.ResourceNotFoundException;
import com.commerce.address.model.Address;
import com.commerce.address.repository.AddressRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/2 17:49
 * @package com.commerce.address.service
 * <p>
 * Description:
 */
@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserClientService userClientService;
    @Autowired
    private ModelMapper modelMapper;

    /** Create Address */
    @Override
    public AddressResponse createAddress(String keycloakId, AddressRequest addressRequest) {
        // 检查用户是否存在
        boolean isActive = userClientService.isUserActive(keycloakId);
        if (!isActive) {
            throw new ApiException("User is not active or does not exist", HttpStatus.UNAUTHORIZED);
        }

        Address address = modelMapper.map(addressRequest, Address.class);
        address.setKeycloakId(keycloakId);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        Address saved = addressRepository.save(address);
        return modelMapper.map(saved, AddressResponse.class);
    }

    /** Get All Addresses */
    @Override
    public List<AddressResponse> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll().stream()
                .filter(a -> !Boolean.TRUE.equals(a.getDeleted()))
                .toList();

        if (addresses.isEmpty()) {
            throw new ResourceNotFoundException("No addresses found");
        }

        return addresses.stream()
                .map(a -> modelMapper.map(a, AddressResponse.class))
                .toList();
    }

    /** Get Address by ID */
    @Override
    public AddressResponse getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));

        if (address.getDeleted()) {
            throw new ApiException("Address has been deleted: ID " + addressId, HttpStatus.BAD_REQUEST);
        }

        return modelMapper.map(address, AddressResponse.class);
    }

    /** Get Address by User */
    @Override
    public List<AddressResponse> getAddressesByUser(String keycloakId) {
        List<Address> addresses = addressRepository.findByKeycloakIdAndDeletedFalse(keycloakId);

        if (addresses.isEmpty()) {
            throw new ResourceNotFoundException("No addresses found for user: " + keycloakId);
        }

        return addresses.stream()
                .map(a -> modelMapper.map(a, AddressResponse.class))
                .toList();
    }

    /** Update Address */
    @Override
    public AddressResponse updateAddress(Long addressId, String keycloakId, AddressRequest addressRequest) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        if (!address.getKeycloakId().equals(keycloakId)) {
            throw new ApiException("Unauthorized: Cannot update another user's address", HttpStatus.UNAUTHORIZED);
        }

        if (Boolean.TRUE.equals(address.getDeleted())) {
            throw new ApiException("Cannot update a deleted address ID: " + addressId, HttpStatus.BAD_REQUEST);
        }

        // 映射更新字段
        modelMapper.map(addressRequest, address);
        address.setKeycloakId(keycloakId);;
        address.setUpdatedAt(LocalDateTime.now());

        Address updatedAddress = addressRepository.save(address);
        return modelMapper.map(updatedAddress, AddressResponse.class);
    }

    /** Delete Address (Logical Delete) */
    @Override
    public void deleteAddress(Long addressId, String keycloakId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        if (!address.getKeycloakId().equals(keycloakId)) {
            throw new ApiException("Unauthorized: Cannot delete another user's address", HttpStatus.UNAUTHORIZED);
        }

        if (Boolean.TRUE.equals(address.getDeleted())) {
            throw new ApiException("Address already deleted ID: " + addressId, HttpStatus.BAD_REQUEST);
        }

        address.setDeleted(true);
        addressRepository.save(address);
    }


}
