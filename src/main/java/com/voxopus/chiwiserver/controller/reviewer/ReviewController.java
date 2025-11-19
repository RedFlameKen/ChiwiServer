package com.voxopus.chiwiserver.controller.reviewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.user.AuthTokenService;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    ReviewerController reviewerController;

    @PostMapping("/record")
    public ResponseEntity<?> fileTest(@RequestPart(name = "audio", required = true) MultipartFile file){
        HttpStatus status = HttpStatus.OK;
        ResponseData<?> response;
        File sound = new File("/home/redflameken/Music/sound.wav");
        try {
            sound.createNewFile();
            FileOutputStream writer = new FileOutputStream(sound);
            writer.write(file.getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response = ResponseData.builder()
                .status_code(status.value())
                .message("failed to write file")
                .build();
        }
        response = ResponseData.builder()
            .status_code(status.value())
            .message("wrote the file")
            .build();
        return new ResponseEntity<>(response, status);
    }
    
}
