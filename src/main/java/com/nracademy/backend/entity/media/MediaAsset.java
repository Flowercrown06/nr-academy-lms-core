package com.nracademy.backend.entity.media;

import com.nracademy.backend.entity.course.Course;
import com.nracademy.backend.entity.enums.MediaOwnerType;
import com.nracademy.backend.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "media_assets",
        indexes = @Index(name = "idx_media_course_owner", columnList = "course_id, owner_type, owner_id")
)
public class MediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "course_id")
    UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    Course course;

    @Column(name = "original_filename", nullable = false)
    String originalFilename;

    @Column(name = "storage_key", nullable = false)
    String storageKey;

    @Column(nullable = false)
    String url;

    @Column(name = "content_type", nullable = false)
    String contentType;

    @Column(name = "size_bytes", nullable = false)
    Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false)
    MediaOwnerType ownerType;

    @Column(name = "owner_id")
    UUID ownerId;

    @Column(name = "uploaded_by", nullable = false)
    UUID uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", insertable = false, updatable = false)
    User uploader;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaAsset other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
