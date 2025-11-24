package com.voxopus.chiwiserver.response.setup_session;

import com.voxopus.chiwiserver.enums.SetupCommandType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SetupSessionResponseData<T> {

    @NonNull
    String message;

    @NonNull
    SetupCommandType command;

    T data;
    
}
