package com.voxopus.chiwiserver.response.user;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponseData {

    String username;
    Date dateCreated;
    
}
