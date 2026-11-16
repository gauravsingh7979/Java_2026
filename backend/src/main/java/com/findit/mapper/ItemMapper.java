package com.findit.mapper;

import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.ItemImageResponse;
import com.findit.dto.item.LostItemResponse;
import com.findit.entity.FoundItem;
import com.findit.entity.ItemImage;
import com.findit.entity.LostItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public LostItemResponse toLostItemResponse(LostItem item) {
        if (item == null) return null;

        List<ItemImageResponse> images = item.getImages() != null ?
                item.getImages().stream().map(this::toImageResponse).collect(Collectors.toList()) :
                Collections.emptyList();

        int matchesCount = item.getMatches() != null ? item.getMatches().size() : 0;

        return LostItemResponse.builder()
                .id(item.getId())
                .trackingNumber(item.getTrackingNumber())
                .userId(item.getUser().getId())
                .reporterName(item.getUser().getFullName())
                .reporterEmail(item.getUser().getEmail())
                .title(item.getTitle())
                .category(item.getCategory())
                .description(item.getDescription())
                .brand(item.getBrand())
                .primaryColor(item.getPrimaryColor())
                .dateLost(item.getDateLost())
                .locationLost(item.getLocationLost())
                .distinctiveCharacteristics(item.getDistinctiveCharacteristics())
                .contactPreference(item.getContactPreference())
                .status(item.getStatus())
                .images(images)
                .matchCount(matchesCount)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    public FoundItemResponse toFoundItemResponse(FoundItem item, boolean includePrivateDetails) {
        if (item == null) return null;

        List<ItemImageResponse> images = item.getImages() != null ?
                item.getImages().stream().map(this::toImageResponse).collect(Collectors.toList()) :
                Collections.emptyList();

        int matchesCount = item.getMatches() != null ? item.getMatches().size() : 0;
        int claimsCount = item.getClaims() != null ? item.getClaims().size() : 0;

        return FoundItemResponse.builder()
                .id(item.getId())
                .trackingNumber(item.getTrackingNumber())
                .userId(item.getUser().getId())
                .finderName(item.getUser().getFullName())
                .finderEmail(item.getUser().getEmail())
                .title(item.getTitle())
                .category(item.getCategory())
                .publicDescription(item.getPublicDescription())
                // Strictly masked unless includePrivateDetails is true!
                .privateDetails(includePrivateDetails ? item.getPrivateDetails() : null)
                .brand(item.getBrand())
                .primaryColor(item.getPrimaryColor())
                .dateFound(item.getDateFound())
                .locationFound(item.getLocationFound())
                .storageLocation(item.getStorageLocation())
                .status(item.getStatus())
                .images(images)
                .matchCount(matchesCount)
                .claimCount(claimsCount)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    public ItemImageResponse toImageResponse(ItemImage img) {
        if (img == null) return null;

        return ItemImageResponse.builder()
                .id(img.getId())
                .fileName(img.getFileName())
                .originalFileName(img.getOriginalFileName())
                .url("/uploads/" + img.getFileName())
                .primary(img.isPrimary())
                .build();
    }
}
