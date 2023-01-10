package com.synchrony.userapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.userapp.config.JwtAuthenticationEntryPoint;
import com.synchrony.userapp.config.JwtUtils;
import com.synchrony.userapp.exception.ImageNotFoundException;
import com.synchrony.userapp.exception.RecordAlreadyExistsException;
import com.synchrony.userapp.model.UserModel;
import com.synchrony.userapp.service.UserService;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    UserController userController;

    @MockBean
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtTokenUtil;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    ObjectMapper mapper = new ObjectMapper();

    private UserModel userModel;

    String token =
            "eyJhbGciOiJIUzUxMiJ9." + "eyJzdWIiOiJ2aWRoeWFAZ21haWwuY29tIiwiZXhwIjoxNjQyNDM3MTQ2LCJpYXQiOjE2NDI0MTkxNDZ9." + "Bv5OMK2O4xHs5RhDh_x9EojT_vKxfrlsGHWzpEPznddF-N4PlVaCy8jeTDuxZxrJyGLd002cfqUlLSTw9Sotqg";

    @BeforeEach
    public void setUp() {
        userModel = new UserModel("vidhya@yopmail.com", "testing");
    }

    @Test
    public void registerUserTest() throws Exception {
        String request = mapper.writeValueAsString(userModel);
        Mockito.when(userService.registerUser(any(UserModel.class)))
                .thenReturn(token);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .content(request)
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();
        assertEquals(201, result.getResponse()
                .getStatus());
    }

    @Test
    public void registerUserWhenRecordAlreadyExistsTest() throws Exception {
        String request = mapper.writeValueAsString(userModel);
        Mockito.when(userService.registerUser(any(UserModel.class)))
                .thenThrow(RecordAlreadyExistsException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .content(request)
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RecordAlreadyExistsException));
    }

    @Test
    public void uploadImageTest() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("image", "img.jpg", "MediaType.IMAGE_JPEG_VALUE", new byte[1024]);
        doReturn("vidhya@yopmail.com").when(jwtTokenUtil)
                .getLoggedInUserName();
        Mockito.when(userService.uploadImage(any(), any()))
                .thenReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/uploadImage")
                        .file(file))
                .andReturn();
        assertEquals(200, result.getResponse()
                .getStatus());
    }

    @Test
    public void uploadImageNegativeTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", null, "MediaType.IMAGE_JPEG_VALUE", new byte[0]);
        Mockito.when(userService.uploadImage(any(),any())).thenReturn(true);
       mockMvc.perform(MockMvcRequestBuilders
                .multipart("/uploadImage").file(file))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ImageNotFoundException));

    }

   @Test
    public void getUserImageTest() throws Exception {
        Mockito.when(userService.getImage(any())).thenReturn(new byte[1024]);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/image/{id}", "dc7812f3-3830-43e9-b637-9f3d39584bef")
                        .contentType(MediaType.IMAGE_JPEG_VALUE))
                        .andReturn();
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void getUserImageDataTest() throws Exception {
        JSONArray json = new JSONArray();
        Mockito.when(userService.getImage(any())).thenReturn(null);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/imageData")
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        assertEquals(200, result.getResponse().getStatus());
    }

   @Test
    public void getLoginTest() throws Exception {
       String request = mapper.writeValueAsString(userModel);
       Mockito.when(userService.registerUser(any(UserModel.class)))
               .thenReturn(token);
       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                       .content(request)
                       .characterEncoding("utf-8")
                       .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
               .andReturn();
       assertEquals(200, result.getResponse()
               .getStatus());
    }

    @Test
    public void deleteImageTest() throws Exception {
        Mockito.doNothing().when(userService).deleteImage(any());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/image/{id}", "dc7812f3-3830-43e9-b637-9f3d39584bef").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(202, result.getResponse().getStatus());
    }
}
