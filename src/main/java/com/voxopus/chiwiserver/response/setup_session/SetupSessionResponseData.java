package com.voxopus.chiwiserver.response.setup_session;

import com.voxopus.chiwiserver.enums.SetupCommandType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SetupSessionResponseData {

    String message;
    SetupCommandType command;
    
}
