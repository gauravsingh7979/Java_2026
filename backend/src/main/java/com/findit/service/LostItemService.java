package com.findit.service;

import com.findit.dto.common.PageResponse;
import com.findit.dto.item.ItemSearchFilter;
import com.findit.dto.item.LostItemRequest;
import com.findit.dto.item.LostItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LostItemService {

    LostItemResponse createLostItem(LostItemRequest request, List<MultipartFile> images, Long userId);

    LostItemResponse getLostItemById(Long id);

    PageResponse<LostItemResponse> searchLostItems(ItemSearchFilter filter);

    LostItemResponse updateLostItem(Long id, LostItemRequest request, Long userId);

    void deleteLostItem(Long id, Long userId);

    LostItemResponse addImages(Long id, List<MultipartFile> images, Long userId);
}
