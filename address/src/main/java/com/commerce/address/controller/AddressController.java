package com.commerce.address.controller;

import com.commerce.address.dto.AddressRequest;
import com.commerce.address.dto.AddressResponse;
import com.commerce.address.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/2 17:30
 * @package com.commerce.address.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api/v1/addresses")
class AddressController {

    @Autowired
    private AddressService addressService;

    /** ✅ Get All Addresses */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        List<AddressResponse> responses = addressService.getAllAddresses();
        return ResponseEntity.ok(responses);
    }

    /** ✅ Get Address by ID */
    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long addressId) {
        AddressResponse response = addressService.getAddressById(addressId);
        return ResponseEntity.ok(response);
    }

    /** ✅ Get Address by Logged-in User */
    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AddressResponse>> getAddressesByUser(@RequestHeader("X-User-Id") String keycloakId) {
        List<AddressResponse> responses = addressService.getAddressesByUser(keycloakId);
        return ResponseEntity.ok(responses);
    }

    /** ✅ Create Address */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> createAddress(@RequestHeader("X-User-Id") String keycloakId,
                                                         @Valid @RequestBody AddressRequest addressRequest) {
        AddressResponse response = addressService.createAddress(keycloakId, addressRequest);
        return ResponseEntity.created(URI.create("/api/v1/addresses/" + response.getAddressId())).body(response);
    }

    /** ✅ Update Address */
    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long addressId,
                                                         @RequestHeader("X-User-Id") String keycloakId,
                                                         @Valid @RequestBody AddressRequest addressRequest) {
        AddressResponse response = addressService.updateAddress(addressId, keycloakId, addressRequest);
        return ResponseEntity.ok(response);
    }

    /** ✅ Delete Address (Logical Delete) */
    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId,
                                              @RequestHeader("X-User-Id") String keycloakId) {
        addressService.deleteAddress(addressId, keycloakId);
        return ResponseEntity.noContent().build();
    }

}
