package com.voxopus.chiwiserver.session_state;

import com.voxopus.chiwiserver.enums.StateStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class StateResult {

    private String message;
    private StateStatus status;
    
}
