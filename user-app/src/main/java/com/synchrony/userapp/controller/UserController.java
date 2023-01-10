package com.synchrony.userapp.controller;

import com.synchrony.userapp.config.JwtUtils;
import com.synchrony.userapp.exception.ImageNotFoundException;
import com.synchrony.userapp.exception.RecordAlreadyExistsException;
import com.synchrony.userapp.exception.RecordNotFoundException;
import com.synchrony.userapp.model.UserModel;
import com.synchrony.userapp.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Description: This endpoint is a method to register User.
     * @param userModel
     * @return ResponseEntity<String> obj token and statuscode is send as Response
     * @throws RecordAlreadyExistsException if user already registered with same emailId
     */
    @PostMapping("/register")
    public ResponseEntity<String>  register(@RequestBody @Valid UserModel userModel) throws RecordAlreadyExistsException {
        log.info("User Registration");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(userModel));

    }

    /**
     * Description: This endpoint is a method for user login.
     * @param userModel
     * @return ResponseEntity<String> obj token and statuscode is send as Response
     * @throws RecordNotFoundException if user not found
     */
    @PostMapping("/login")
    public ResponseEntity<String>  login(@RequestBody @Valid UserModel userModel) throws RecordNotFoundException {
        log.info("User login");
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.login(userModel));
    }

    /**
     * Description: Upload user image of loggedIn user.
     * @param image
     * @return message if the user image save .
     * @throws IOException
     * @throws ImageNotFoundException
     */
    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") final MultipartFile image)
            throws IOException, ImageNotFoundException, RecordNotFoundException {
        log.info("Upload user image of loggedIn user");
        if (image.getSize() <= 0) {
            log.error("Image is Empty");
            throw new ImageNotFoundException("No image Found");
        }
        Boolean uploadStatus = userService.uploadImage(jwtUtils.getLoggedInUserName(), image);
        return uploadStatus
                ?  ResponseEntity.ok().body("Image uploaded successfully")
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Description: Display the user and image Data of loggedIn user.
     *
     * @return user image.
     */
    @GetMapping(value = "/imageData", produces="application/json")
    public ResponseEntity<JSONArray> getUserImageData() throws RecordNotFoundException {
        log.info("Fetch user and image Data respective to loggedIn User");
        return new ResponseEntity<>(userService.getImagesData(jwtUtils.getLoggedInUserName()), HttpStatus.OK);
    }

    /**
     * Description: Display the user image based on userGallery Id.
     * @param imageId
     * @return  image
     * @throws IOException
     * @throws RecordNotFoundException
     */
    @GetMapping(value = "/image/{id}", produces = {MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<?> downloadImage(
            @PathVariable("id") final UUID imageId) throws IOException, RecordNotFoundException {
        log.info("Download image based on userGallery Id");
        byte[] image = userService.getImage(imageId);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    /**
     * Description: Delete image based on userGallery Id.
     * @param imageId
     * @return message
     * @throws RecordNotFoundException
     */

    @DeleteMapping("/image/{id}")
    public ResponseEntity<?> deleteUserImage(@PathVariable("id") final UUID imageId) throws RecordNotFoundException {
        log.info("Delete image based on userGallery Id");
        userService.deleteImage(imageId);
        return new ResponseEntity<>("Image deleted successfully",
                HttpStatus.ACCEPTED);
    }
}
