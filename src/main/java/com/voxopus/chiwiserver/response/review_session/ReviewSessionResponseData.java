package com.voxopus.chiwiserver.response.review_session;

import com.voxopus.chiwiserver.enums.ReviewCommandType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ReviewSessionResponseData<T> {

    @NonNull
    String message;

    @NonNull
    ReviewCommandType command;

    T data;
    
}
