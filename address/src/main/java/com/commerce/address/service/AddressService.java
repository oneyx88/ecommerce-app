package com.commerce.address.service;

import com.commerce.address.dto.AddressRequest;
import com.commerce.address.dto.AddressResponse;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/2 17:38
 * @package com.commerce.address.service
 * <p>
 * Description:
 */
public interface AddressService {
    AddressResponse createAddress(String keycloakId, AddressRequest addressRequest);

    List<AddressResponse> getAllAddresses();

    AddressResponse getAddressById(Long addressId);

    List<AddressResponse> getAddressesByUser(String keycloakId);

    AddressResponse updateAddress(Long addressId, String keycloakId, AddressRequest addressRequest);

    void deleteAddress(Long addressId, String keycloakId);
}
