package com.voxopus.chiwiserver.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class AuthTokenResponse {

    String token;
    
}
