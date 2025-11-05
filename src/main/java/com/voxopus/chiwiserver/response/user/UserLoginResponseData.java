package com.voxopus.chiwiserver.response.user;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponseData {

    Long user_id;
    String username;
    Date date_logged_in;

    @JsonIgnore
    String auth_token;
    
}
