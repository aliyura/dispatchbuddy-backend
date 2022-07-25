package com.decagon.dispatchbuddy.interfaces;
import com.decagon.dispatchbuddy.pojos.APIResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BlobStorage {

    APIResponse upload (MultipartFile file, String preFix);
    void delete (String identifier);
}
