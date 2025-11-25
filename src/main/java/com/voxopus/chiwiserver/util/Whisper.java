package com.voxopus.chiwiserver.util;

import java.net.URI;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.voxopus.chiwiserver.response.whisper.WhisperInference;

public class Whisper {

    public static WhisperInference transcribe(byte[] audioData){
        RestTemplate template = new RestTemplate();
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition disposition = ContentDisposition.builder("form-data")
            .name("file")
            .filename("audio.wav")
            .build();

        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, disposition.toString());
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(audioData, fileMap);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", entity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request =
            new HttpEntity<MultiValueMap<String,Object>>(body, headers);

        URI uri = URI.create("http://localhost:5050/inference");
        ResponseEntity<WhisperInference> response =
            template.postForEntity(uri, request, WhisperInference.class);
        return response.getBody();
    }
    
}
