package ru.skillbox.service.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.dto.ImageRecordDto;
import ru.skillbox.dto.PhotoDto;
import ru.skillbox.exception.AccessResourseException;
import ru.skillbox.exception.BadArgumentException;
import ru.skillbox.mapper.ImageRecordMapper;
import ru.skillbox.repository.ImageRepository;
import ru.skillbox.security.JwtUtils;
import ru.skillbox.service.StorageService;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {
  private final AmazonS3 s3Client;
  private final ImageRepository repository;
  private final ImageRecordMapper mapper;
  private static final String ERROR_TRANSFER_DATA_TEMPLATE = "Error transfer data to cloud! {0}";
  private static final String ERROR_READ_FILE_TEMPLATE = "Can`t read input file! {0}";
  private static final String CAPTCHA_NAME_SIGN = "captcha_";

  @Value("${s3.bucket_name}")
  String bucketName;
  @Value("${s3.folder_name}")
  String folderName;
  @Value("${s3.service-endpoint}")
  String s3Endpoint;

  @Override
  public PhotoDto uploadCaptcha(MultipartFile file) {
    String fileToUploadName = UUID.randomUUID() + file.getOriginalFilename();
    try {
      return new PhotoDto(uploadAndGetLink(file, fileToUploadName));
    } catch (SdkClientException e) {
      throw new AccessResourseException(MessageFormat.format(ERROR_TRANSFER_DATA_TEMPLATE, e.getMessage()));
    } catch (IOException e) {
      throw new AccessResourseException(MessageFormat.format(ERROR_READ_FILE_TEMPLATE, e.getMessage()));
    }
  }

  @Override
  @Transactional
  public void deleteByLink(String linkToDelete) {
    try {
      deleteUnusedLink(linkToDelete);
    } catch (SdkClientException e) {
      throw new AccessResourseException(MessageFormat.format(ERROR_TRANSFER_DATA_TEMPLATE, e.getMessage()));
    }
  }

  @Override
  @Transactional
  public void eraseAllUserImages(UUID userId) {
    List<String> linksToDelete = repository.findAllValueByUserIdEquals(userId);
    if (linksToDelete.isEmpty()) {
      return;
    }
    linksToDelete = linksToDelete.stream()
          .map(l -> l.substring(s3Endpoint.length() + bucketName.length() + 2))
          .toList();
    try {
      List<S3ObjectSummary> summ = s3Client.listObjects(bucketName).getObjectSummaries();
      Long initialLoad = getBucketLoad(summ);
      List<String> linksInCloud = summ.stream()
            .map(S3ObjectSummary::getKey)
            .collect(toList());
      linksInCloud.retainAll(linksToDelete);
      List<DeleteObjectsRequest.KeyVersion> objectsToDelete = linksInCloud.stream()
            .map(DeleteObjectsRequest.KeyVersion::new)
            .toList();
      int deletedCount =  s3Client.deleteObjects(
                  new DeleteObjectsRequest(bucketName).withKeys(objectsToDelete))
            .getDeletedObjects()
            .size();
      Long currentLoad = getBucketLoad(s3Client.listObjects(bucketName).getObjectSummaries());
      log.info("For user with iD {} deleted {} images from cloud. {} kBytes released.",
            userId, deletedCount, (initialLoad - currentLoad) / 1024);
      repository.deleteAllByUserIdEquals(userId);
    } catch (SdkClientException e) {
      throw new AccessResourseException(MessageFormat.format("Exception occurred while attempting to remove unused images for user with iD {0} from cloud: {1}", userId, e.getLocalizedMessage()));
    }
  }

  @Override
  @Transactional
  public PhotoDto uploadUserImage(MultipartFile file, String oldImage) {
    isFileWithValidContent(file);
    String fileToUploadName = UUID.randomUUID() + normalizeFileName(file);

    try {
      String linkValue = uploadAndGetLink(file, fileToUploadName);
      ImageRecordDto newRecord = ImageRecordDto.builder()
            .value(linkValue)
            .lastUpdated(Instant.now())
            .userId(JwtUtils.getUserIdFromContext())
            .build();
      repository.saveAndFlush(mapper.dtoToEntity(newRecord));
      deleteUnusedLink(oldImage);
      return new PhotoDto(linkValue);
    } catch (SdkClientException e) {
      throw new AccessResourseException(MessageFormat.format(ERROR_TRANSFER_DATA_TEMPLATE, e.getMessage()));
    } catch (IOException e) {
      throw new AccessResourseException(MessageFormat.format(ERROR_READ_FILE_TEMPLATE, e.getMessage()));
    }
  }

  private String uploadAndGetLink(MultipartFile file, String fileToUploadName) throws SdkClientException, IOException {
    String storagePath = String.join("/", bucketName, folderName);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    s3Client.putObject(storagePath, fileToUploadName, file.getInputStream(), metadata);
    return s3Client.getUrl(storagePath, fileToUploadName).toExternalForm();
  }

  private void deleteUnusedLink(String linkToDelete) throws SdkClientException {
    if (!(linkToDelete.equals("null") || linkToDelete.isEmpty())) {
      DeleteObjectRequest request = new DeleteObjectRequest(bucketName,
            linkToDelete.substring(s3Endpoint.length() + bucketName.length() + 2));
      s3Client.deleteObject(request);
      repository.deleteByUserIdEqualsAndValueEquals(JwtUtils.getUserIdFromContext(), linkToDelete);
    }
  }

  @Scheduled(cron = "${s3.captcha_clear_interval}")
  public void deleteCaptches() {
    try {
      List<S3ObjectSummary> summ = s3Client.listObjects(bucketName).getObjectSummaries();
      Long initialLoad = getBucketLoad(summ);
      List<DeleteObjectsRequest.KeyVersion> objectsToDelete = summ.stream()
            .map(S3ObjectSummary::getKey)
            .filter(s -> s.contains(CAPTCHA_NAME_SIGN))
            .map(DeleteObjectsRequest.KeyVersion::new)
            .toList();
      int deletedCount =  s3Client.deleteObjects(
            new DeleteObjectsRequest(bucketName).withKeys(objectsToDelete))
            .getDeletedObjects()
            .size();
      Long currentLoad = getBucketLoad(s3Client.listObjects(bucketName).getObjectSummaries());
      log.info("The storage cleanup has been completed as scheduled. Deleted {} captcha images from cloud. {} kBytes released.",
            deletedCount, (initialLoad - currentLoad) / 1024);
    } catch (SdkClientException e) {
      throw new AccessResourseException(MessageFormat.format("Exception occurred while attempting to remove unused captcha images from cloud: {0}", e.getLocalizedMessage()));
    }
  }

  private Long getBucketLoad(List<S3ObjectSummary> summary) {
    return summary.stream()
          .map(S3ObjectSummary::getSize)
          .reduce(Long::sum)
          .orElse(0L);
  }

  private String normalizeFileName(MultipartFile file) {
    String originalFileName = file.getOriginalFilename();
    if (originalFileName == null) {
      throw  new BadArgumentException("Fail name is null!!!");
    }
    if (originalFileName.contains(CAPTCHA_NAME_SIGN)) {
      originalFileName = originalFileName.replace(CAPTCHA_NAME_SIGN, "_repl_cptch_");
    }
    int nameLength = originalFileName.length();
    if (nameLength > 130) {
      originalFileName = originalFileName.substring(nameLength - 129, nameLength);
    }
    return originalFileName;
  }

  private void isFileWithValidContent(MultipartFile file) {
    if (file.isEmpty()) {
      throw  new BadArgumentException(MessageFormat.format("Uploading file {0} is empty!", file.getOriginalFilename()));
    }
    String contentType = file.getContentType();
    if (contentType != null && !contentType.toLowerCase().contains("image")) {
      throw new BadArgumentException(MessageFormat.format("Content of uploading file {0} is not image!", file.getOriginalFilename()));
    }
  }
}
