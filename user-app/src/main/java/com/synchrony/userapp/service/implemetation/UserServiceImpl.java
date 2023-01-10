package com.synchrony.userapp.service.implemetation;

import com.synchrony.userapp.config.JwtUtils;
import com.synchrony.userapp.entity.User;
import com.synchrony.userapp.entity.UserGallery;
import com.synchrony.userapp.exception.ImageNotFoundException;
import com.synchrony.userapp.exception.RecordAlreadyExistsException;
import com.synchrony.userapp.exception.RecordNotFoundException;
import com.synchrony.userapp.model.MyUserDetails;
import com.synchrony.userapp.model.UserModel;
import com.synchrony.userapp.repository.UserGalleryRepository;
import com.synchrony.userapp.repository.UserRepository;
import com.synchrony.userapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserGalleryRepository userGalleryRepository;

    /**
     * Decsription: This Service is to persist user data in db.
     * @param userModel
     * @return token
     * @throws RecordAlreadyExistsException
     */
    @Override
    public String registerUser(UserModel userModel) throws RecordAlreadyExistsException {
        Optional<User> userDetails = userRepository.findByEmail(userModel.getEmail());
        if (userDetails.isPresent()) {
            throw new RecordAlreadyExistsException("User Already Registered With given emailId");
        }
        User user = new User();
        BeanUtils.copyProperties(userModel, user);
        user = userRepository.save(user);
        return getToken(new MyUserDetails(user.getEmail(), user.getPassword()));
    }

    /**
     * Dscription : This method is to validate loggedIn user
     * @param userModel
     * @return token
     * @throws RecordNotFoundException
     */
    @Override
    public String login(UserModel userModel) throws RecordNotFoundException {
        User user = userRepository.findByEmailAndPassword(userModel.getEmail(), userModel.getPassword())
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
        return getToken(new MyUserDetails(user.getEmail(), user.getPassword()));
    }

    /**
     * Description:Locates the user based on the username from DB for Authentication.
     * @param username the username identifying the user whose data is required.
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new MyUserDetails(user.getEmail(), user.getPassword());
    }

    /**
     * Description : This method is to generate Token.
     * @param user
     * @return token
     */
    public String getToken(UserDetails user) {
        return jwtUtils.generateJwtToken(user);
    }

    /**
     * Description: This method is to persist uploaded images in db.
     * @param userName
     * @param image
     * @return Boolean flag
     * @throws IOException
     * @throws  RecordNotFoundException
     */
    @Override
    public Boolean uploadImage(final String userName, final MultipartFile image)
            throws IOException, RecordNotFoundException {
        User user = userRepository.findByEmail(userName).orElseThrow(() -> new RecordNotFoundException("User not found"));;
        UserGallery gallery = new UserGallery();
        gallery.setUser(user);
        gallery.setFileName(image.getOriginalFilename());
        gallery.setImage(image.getBytes());
        try {
            userGalleryRepository.save(gallery);
            return true;
        } catch (Exception e) {
            log.error("Error" + e);
            return false;
        }

    }

    /**
     * Description: This method is to fetch user and gallery data based on loggedIn user.
     * @param userName
     * @return JSONArray
     * @throws RecordNotFoundException
     * @throws ImageNotFoundException
     */
    @Override
    public JSONArray getImagesData(final String userName) throws RecordNotFoundException {
        User user = userRepository.findByEmail(userName).orElseThrow(() -> new RecordNotFoundException("User not found"));;
        List<UserGallery> userGalleries = userGalleryRepository.findByUserId(user.getId());
        JSONArray userImages = new JSONArray();
        log.debug("Grouping data based on userId");
        Map<UUID, List<UserGallery>> userGallerymap = userGalleries.stream()
                .collect(Collectors.groupingBy(g -> g.getUser()
                        .getId()));
        for (Map.Entry<UUID, List<UserGallery>> entry : userGallerymap.entrySet()) {
            JSONObject userObj = new JSONObject();
            userObj.put("userId", entry.getValue()
                    .get(0)
                    .getUser()
                    .getId());
            userObj.put("email", entry.getValue()
                    .get(0)
                    .getUser()
                    .getEmail());
            JSONArray galleries = new JSONArray();
            entry.getValue()
                    .stream()
                    .forEach(m -> {
                        JSONObject obj = new JSONObject();
                        obj.put("galleryId", m.getId());
                        obj.put("fileName", m.getFileName());
                        galleries.add(obj);
                    });
            userObj.put("imageData", galleries);
            userImages.add(userObj);
        }
        return userImages;
    }

    /**
     * Description: This method is to download image from db based on userGalleryId.
     * @param imageId
     * @return byte[]
     * @throws RecordNotFoundException
     */
    @Override
    public byte[] getImage(final UUID imageId) throws RecordNotFoundException {
        UserGallery gallery = userGalleryRepository.findById(imageId).
        orElseThrow(() -> new RecordNotFoundException("No record found with id: " + imageId));
        return gallery.getImage();
    }

    /**
     * Description: This method is to delete record in db based on  userGalleryId.
     * @param imageId
     * @throws RecordNotFoundException
     */
    public void deleteImage(final UUID imageId) throws RecordNotFoundException {
        try {
            userGalleryRepository.deleteById(imageId);
        } catch (Exception e) {
            throw new RecordNotFoundException("No record found with id: " + imageId);
        }
    }

}
