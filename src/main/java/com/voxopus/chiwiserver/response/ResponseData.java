package com.voxopus.chiwiserver.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseData<T> {

    int status_code;
    String message;
    T data;
    
}
