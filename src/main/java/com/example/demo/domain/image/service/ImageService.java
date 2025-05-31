package com.example.demo.domain.image.service;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.image.dto.ImageDto;
import com.example.demo.domain.image.entity.Image;
import com.example.demo.domain.image.repository.ImageRepository;
import com.example.demo.domain.image.util.ImageUtil;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;
import com.example.demo.infra.aws.S3Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    /**
     * 이미지 생성
     */
    @Transactional
    public ImageDto.Response createImage(ImageDto.Create request) {
        MultipartFile file = request.getImage();

        String key = ImageUtil.generateDateKey(file.getOriginalFilename());
        String url = null;
        try (InputStream is = file.getInputStream()) {
            url = s3Service.upload(
                    key,
                    is,
                    file.getSize(),
                    file.getContentType());
        } catch (Exception e) {
            log.error("S3 업로드 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.IMAGE_S3_UPLOAD_FAILED);
        }

        Image image = imageRepository.save(request.toEntity(url));
        return ImageDto.Response.fromEntity(image);
    }

    /**
     * ! 🔒 Internal API — 컨트롤러에서 직접 호출하지 마세요.
     *
     * @param imageId 조회할 이미지의 UUID
     * @return 찾은 Image 엔티티 (없으면 예외 발생)
     * @throws CustomException IMAGE_NOT_FOUND
     */
    public Optional<Image> findByIdInternal(UUID id) {
        return imageRepository.findById(id);
    }

    public ImageDto.Response findById(UUID id) {
        Image image = findByIdInternal(id)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        return ImageDto.Response.fromEntity(image);
    }
}
