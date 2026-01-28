package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class GetProfileDTO {
    @Getter @Setter
    private UUID id;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String phoneNumber;
    @Getter @Setter
    private String address;
    @Getter @Setter
    private String profileImageUrl;
    @Getter @Setter @Nullable
    private MultipartFile profileImage;
}
