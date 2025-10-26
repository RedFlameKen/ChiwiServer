package com.voxopus.chiwiserver.request.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestData {

    String username;

    String password;

    String salt_iv;
    
}
