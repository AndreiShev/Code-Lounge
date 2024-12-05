package ru.skillbox.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.dto.PhotoDto;
import ru.skillbox.service.StorageService;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
  private final StorageService storageService;

  @PostMapping("/storageUserImage")
  @ResponseStatus(value = HttpStatus.OK)
  public PhotoDto uploadUserImage(@RequestPart(name = "file") MultipartFile file, @RequestParam String oldImage) {
    return storageService.uploadUserImage(file, oldImage);
  }

  @PostMapping
  @ResponseStatus(value = HttpStatus.OK)
  public PhotoDto uploadapCaptcha(@RequestPart(name = "file") MultipartFile file) {
    return storageService.uploadCaptcha(file);
  }

  @GetMapping("/deleteByLink")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteByLink (@RequestParam String linkToDelete) {
    storageService.deleteByLink(linkToDelete);
  }
}
