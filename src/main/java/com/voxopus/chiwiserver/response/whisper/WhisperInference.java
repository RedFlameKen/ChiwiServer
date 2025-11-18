package com.voxopus.chiwiserver.response.whisper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhisperInference {

    String text;
    String error;
    
}
