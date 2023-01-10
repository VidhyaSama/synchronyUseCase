package com.synchrony.userapp.service;

import com.synchrony.userapp.exception.ImageNotFoundException;
import com.synchrony.userapp.exception.RecordAlreadyExistsException;
import com.synchrony.userapp.exception.RecordNotFoundException;
import com.synchrony.userapp.model.UserModel;
import org.json.simple.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserService {

    /**
     * Decription: interface is to register User in db.
     * @param userModel
     * @return token
     * @throws RecordAlreadyExistsException
     */
   String registerUser(UserModel userModel) throws RecordAlreadyExistsException;

    /**
     * Description: interface is to validate loggedIn user.
     * @param userModel
     * @return token
     * @throws RecordNotFoundException
     */
   String login(UserModel userModel) throws RecordNotFoundException;

    /**
     * Description: Interface is to save image uploaded by user.
     * @param userName
     * @param file
     * @return Boolean obj
     * @throws IOException
     */
   Boolean uploadImage(String userName, MultipartFile file) throws IOException, RecordNotFoundException;

    /**
     * Description: Interface is to fetch User and image Data.
     * @param userName
     * @return  JSONArray
     */
  JSONArray getImagesData(String userName) throws RecordNotFoundException;

    /**
     * Description: Interface is to  delete image based on userGallery Id.
     * @param imageId
     * @throws RecordNotFoundException
     */
    void deleteImage(UUID imageId) throws RecordNotFoundException;

    /**
     * Description: Interface is to fetch image based on userGallery Id.
     * @param imageId
     * @return byte[]
     * @throws RecordNotFoundException
     */
    byte[] getImage(UUID imageId) throws RecordNotFoundException;
}
