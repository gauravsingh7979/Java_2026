package com.findit.service;

import com.findit.dto.common.PageResponse;
import com.findit.dto.item.FoundItemRequest;
import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.ItemSearchFilter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoundItemService {

    FoundItemResponse createFoundItem(FoundItemRequest request, List<MultipartFile> images, Long userId);

    FoundItemResponse getFoundItemById(Long id, Long currentUserId);

    PageResponse<FoundItemResponse> searchFoundItems(ItemSearchFilter filter);

    FoundItemResponse updateFoundItem(Long id, FoundItemRequest request, Long userId);

    void deleteFoundItem(Long id, Long userId);

    FoundItemResponse addImages(Long id, List<MultipartFile> images, Long userId);
}
