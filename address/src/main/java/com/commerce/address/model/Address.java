package com.commerce.address.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/10/23 23:46
 * @package com.commerce.user.model
 * <p>
 * Description:
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank(message = "Keycloak ID cannot be empty")
    private String keycloakId;

    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters long")
    private String street;

    @NotBlank
    @Size(min = 2, message = "City name must be at least 2 characters long")
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name must be at least 2 characters long")
    private String state;

    @NotBlank
    @Size(min = 2, message = "Country name must be at least 2 characters long")
    private String country;

    @NotBlank
    @Size(min = 5, message = "Zip code must be at least 5 characters long")
    private String zipCode;

    private String label; // e.g. "Home", "Office", "Warehouse"
    private Boolean isDefault = false; // Default address flag

    private Boolean deleted = false;   // Logical delete

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
