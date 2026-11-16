package com.findit.service;

import com.findit.entity.ItemImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    ItemImage storeImage(MultipartFile file);

    void deleteImage(String fileName);

    List<ItemImage> storeImages(List<MultipartFile> files);
}
