package com.nracademy.backend.repository;

import com.nracademy.backend.entity.enums.MediaOwnerType;
import com.nracademy.backend.entity.media.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID>, JpaSpecificationExecutor<MediaAsset> {

    List<MediaAsset> findByCourseIdAndOwnerTypeAndOwnerId(UUID courseId, MediaOwnerType ownerType, UUID ownerId);
}
