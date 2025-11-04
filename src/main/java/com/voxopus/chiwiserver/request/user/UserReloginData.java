package com.voxopus.chiwiserver.request.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReloginData {

    String username;
    String auth_token;

}
