package com.synchrony.userapp.service;

import com.synchrony.userapp.config.JwtUtils;
import com.synchrony.userapp.entity.User;
import com.synchrony.userapp.entity.UserGallery;
import com.synchrony.userapp.exception.ImageNotFoundException;
import com.synchrony.userapp.exception.RecordAlreadyExistsException;
import com.synchrony.userapp.exception.RecordNotFoundException;
import com.synchrony.userapp.model.UserModel;
import com.synchrony.userapp.repository.UserGalleryRepository;
import com.synchrony.userapp.repository.UserRepository;
import com.synchrony.userapp.service.implemetation.UserServiceImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGalleryRepository userGalleryRepository;

    @Mock
    private JwtUtils jwtTokenUtil;

    @Mock
    private UserDetailsService userDetailsService;

    private UserModel userModel;

    private User user;

    UserGallery gallery = new UserGallery();
    String token = "eyJhbGciOiJIUzUxMiJ9."
            + "eyJzdWIiOiJ2aWRoeWFAZ21haWwuY29tIiwiZXhwIjoxNjQyNDM3MTQ2LCJpYXQiOjE2NDI0MTkxNDZ9."
            + "Bv5OMK2O4xHs5RhDh_x9EojT_vKxfrlsGHWzpEPznddF-N4PlVaCy8jeTDuxZxrJyGLd002cfqUlLSTw9Sotqg";

    @BeforeEach
    public void setUp() {
        userModel = new UserModel("vidhya@yopmail.com", "testing");
        user = new User(UUID.fromString("dc7812f3-3830-43e9-b637-9f3d39584bef"),"vidhya@yopmail.com","testing" );
        gallery.setUser(user);
        gallery.setFileName("image.png");
        byte[] image = new byte[100];
        gallery.setImage(image);
        JSONArray json = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("user", user);
        JSONArray galleries = new JSONArray();
        JSONObject galleryObj = new JSONObject();
        galleryObj.put("gallery", gallery);
        obj.put("galleries",galleryObj);
        json.add(obj);
    }

    @Test
    public void registerUserTest() throws RecordAlreadyExistsException {
        Mockito.when(userRepository.save(any())).thenReturn(user);
        Mockito.when(jwtTokenUtil.generateJwtToken(any())).thenReturn(token);
        String token = userService.registerUser(userModel);
    }

    @Test
    public void registerUserExceptionTest() {
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(jwtTokenUtil.generateJwtToken(any())).thenReturn(token);
        assertThrows(RecordAlreadyExistsException.class, () -> {
            userService.registerUser(userModel);
        });
    }

   @Test
    public void loginTest() throws RecordNotFoundException {
        Mockito.when(userRepository.findByEmailAndPassword(any(),any())).thenReturn(Optional.ofNullable(user));
       Mockito.when(jwtTokenUtil.generateJwtToken(any())).thenReturn(token);
       String token = userService.login(userModel);
    }

    @Test
    public void loginWhenUserNotFoundTest() throws RecordNotFoundException {
        Mockito.when(userRepository.findByEmailAndPassword(any(),any())).thenReturn(Optional.ofNullable(null));
        assertThrows(RecordNotFoundException.class, () -> {
            userService.login(userModel);
        });
    }

    @Test
    public void uploadImageTest() throws IOException, RecordNotFoundException {
        MockMultipartFile file =
                new MockMultipartFile("image", "img.jpg", "MediaType.IMAGE_JPEG_VALUE", new byte[1024]);
        UserGallery gallery = mock(UserGallery.class);
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(any())).thenReturn(gallery);
        assertTrue(userService.uploadImage("vidhya@yopmail.com",file));
    }

    @Test
    public void getImageTest() throws RecordNotFoundException {
        Mockito.when(userGalleryRepository.findById(any())).thenReturn(Optional.ofNullable(gallery));
        byte[] result =  userService.getImage(UUID.fromString("dc7812f3-3830-43e9-b637-9f3d39584bef"));
        assertEquals(100, result.length);
    }

    @Test
    public void getImageNegativeTest() {
        Mockito.when(userRepository.findById( any())).thenReturn(Optional.ofNullable(null));
        assertThrows(RecordNotFoundException.class, () -> {
            userService.getImage(UUID.fromString("dc7812f3-3830-43e9-b637-9f3d39584bef"));
        });
    }
    @Test
    public void deleteImageTest() throws RecordNotFoundException {
        Mockito.when(userGalleryRepository.findByUserId(any()));
        assertThrows(RecordNotFoundException.class, () -> {
            userService.deleteImage(UUID.fromString("dc7812f3-3830-43e9-b637-9f3d39584bef"));
        });
    }

    @Test
    public void getImageDataTest() throws RecordNotFoundException {
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        List<UserGallery> galleries = new ArrayList<>();
        galleries.add(gallery);
        Mockito.when(userGalleryRepository.findByUserId(any())).thenReturn(galleries);
        userService.getImagesData("vidhya.yopmail.com");
    }

}
