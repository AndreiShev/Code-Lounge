package ru.skillbox.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.dto.PhotoDto;

import java.util.UUID;

public interface StorageService {
  PhotoDto uploadUserImage(MultipartFile file, String oldImage);

  PhotoDto uploadCaptcha(MultipartFile file);

  void deleteByLink(String linkToDelete);

  void eraseAllUserImages(UUID userId);
}
