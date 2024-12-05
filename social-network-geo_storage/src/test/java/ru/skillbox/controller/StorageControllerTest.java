package ru.skillbox.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.skillbox.AbstractGeoStorageTest;
import ru.skillbox.repository.ImageRepository;
import ru.skillbox.service.impl.StorageServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StorageControllerTest extends AbstractGeoStorageTest {
  @Value("${s3.bucket_name}")
  String bucketName;
  @Value("${s3.service-endpoint}")
  String s3Endpoint;
  @Autowired
  StorageServiceImpl storageService;
  @MockBean
  AmazonS3 client;
  @Autowired
  ImageRepository repository;

  @Test
  void whenUploadCaptchaUnauthorized_thenGetLink () throws Exception {
    Mockito.when(client.getUrl(any(), any()))
          .thenReturn(new URL("http://any.any/Link_to_cloud_storage"));
    Resource fileResource = new ClassPathResource("/files/captcha_111.png");
    MockMultipartFile file =  new MockMultipartFile("file", fileResource.getFilename(),
          MediaType.IMAGE_PNG_VALUE, fileResource.getInputStream());
    String response = mockMvc.perform(
                multipart("/api/v1/storage")
                      .file(file))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();
    assertTrue(response.contains("http://any.any/Link_to_cloud_storage"));
  }

  @Test
  @WithMockUser(username = "31265ba7-e000-4283-a07b-94bbbd7b4c88")
  void whenUploadNotImage_thenReturnError () throws Exception {
    String actualResponse = getMockResponse(MediaType.TEXT_PLAIN_VALUE, HttpStatus.BAD_REQUEST);
    assertTrue(actualResponse.contains("is not image!"));
  }

  @Test
  void whenUploadFileUnauthorized_thenReturnException () throws Exception {
    Mockito.when(client.getUrl(any(), any()))
          .thenReturn(new URL("http://any.any/Link_to_cloud_storage"));
    mockMvc.perform(
                multipart("/api/v1/storage/storageUserImage")
                      .file(uploadingFile(MediaType.IMAGE_PNG_VALUE))
                      .param("oldImage", "https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/665620621bdc.jpg"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(status().is(401));
  }

  @Test
  void whenUploadImage_thenReturnLinkAndDeleteOldLink () throws Exception {
    MockMultipartFile uploadingImage = uploadingFile(MediaType.IMAGE_PNG_VALUE);
    String oldImage = "https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/anyLink2";
    assertEquals(6, repository.count());
    assertTrue(repository.findAllValueByUserIdEquals(UUID.fromString("aa15c025-eed1-46e4-b136-d6538df83703"))
          .contains("https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/anyLink2"));
    assertFalse(repository.findAllValueByUserIdEquals(UUID.fromString("aa15c025-eed1-46e4-b136-d6538df83703"))
          .contains("https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/anyLink8"));
    Mockito.when(client.getUrl(any(), any()))
            .thenReturn(new URL("https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/anyLink8"));
    String actualResponse = mockMvc.perform(
                multipart("/api/v1/storage/storageUserImage")
                      .file(uploadingImage)
                      .param("oldImage", oldImage)
                      .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWQiOiJhYTE1YzAyNS1lZWQxLTQ2ZTQtYjEzNi1kNjUzOGRmODM3MDMiLCJleHAiOjE3MjkyMDU5OTYsImlhdCI6MTcyOTExOTU5Nn0.13AH73hgap01ObMpJkj-foS5GzpgAvkufubN3S9GJJ4"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();
    Mockito.verify(client).deleteObject(any());
    assertEquals(6, repository.count());
    assertTrue(repository.findAllValueByUserIdEquals(UUID.fromString("aa15c025-eed1-46e4-b136-d6538df83703"))
          .contains("https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/anyLink8"));
    assertFalse(repository.findAllValueByUserIdEquals(UUID.fromString("aa15c025-eed1-46e4-b136-d6538df83703"))
          .contains("https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/anyLink2"));
    assertTrue(actualResponse.contains("/skb-team-project-50/anyLink8"));
  }


  @Test
  void whenDeleteCaptchas_thenDeleteCaptchas () throws  Exception {
    prepareMockCloud(repository, bucketName.length() + s3Endpoint.length() + 2);
    Mockito.when(client.listObjects((String) any())).thenReturn(listing);
    Mockito.when(listing.getObjectSummaries()).thenReturn(summ);
    Mockito.when(client.deleteObjects(any())).thenReturn(delResult);
    Mockito.when(delResult.getDeletedObjects()).thenReturn(List.of(new DeleteObjectsResult.DeletedObject(),
          new DeleteObjectsResult.DeletedObject()));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream originalSystemOut = System.out;
    System.setOut(new PrintStream(baos));

    storageService.deleteCaptches();

    System.setOut(originalSystemOut);
    String logOutput = baos.toString();

    Mockito.verify(client, Mockito.times(2)).listObjects((String) any());
    assertThat(logOutput).contains("The storage cleanup has been completed as scheduled. Deleted 2 captcha images from cloud.");
  }

  @Test
  @WithMockUser("aa15c025-eed1-46e4-b136-d6538df83703")
  void deleteByLink() {
    String link = "https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/anyLink1";
    UUID userId = UUID.fromString("aa15c025-eed1-46e4-b136-d6538df83703");
    assertTrue(repository.findAllValueByUserIdEquals(userId).contains(link));

    storageService.deleteByLink(link);

    assertFalse(repository.findAllValueByUserIdEquals(userId).contains(link));
    Mockito.verify(client,Mockito.times(1)).deleteObject(any());
  }

  @Test
  void eraseAllUserImages() {
    Mockito.when(client.listObjects((String) any())).thenReturn(listing);
    Mockito.when(listing.getObjectSummaries()).thenReturn(summ);
    Mockito.when(client.deleteObjects(any())).thenReturn(delResult);
    Mockito.when(delResult.getDeletedObjects()).thenReturn(List.of(new DeleteObjectsResult.DeletedObject(),
          new DeleteObjectsResult.DeletedObject()));

    assertEquals(6L, repository.count());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream originalSystemOut = System.out;
    System.setOut(new PrintStream(baos));

    storageService.eraseAllUserImages(UUID.fromString("31265ba7-e000-4283-a07b-94bbbd7b4c88"));

    System.setOut(originalSystemOut);
    String logOutput = baos.toString();

    Mockito.verify(client, Mockito.times(2)).listObjects((String) any());
    Mockito.verify(client, Mockito.times(1)).deleteObjects(any());
    assertEquals(4L, repository.count());
    assertThat(logOutput).contains("For user with iD 31265ba7-e000-4283-a07b-94bbbd7b4c88 deleted 2 images from cloud.");

  }

  private String getMockResponse(String fileMediaType, HttpStatus responseStatus) throws Exception {
    return mockMvc.perform(
          multipart("/api/v1/storage/storageUserImage")
                .file(uploadingFile(fileMediaType))
                .param("oldImage", "https://storage.yandexcloud.net/social-network-storage/skb-team-project-50/6c40512e-0057-4053-9dc9-3e11c1ef1b83661a7b6d3c4ea2101314465620621bdc.jpg"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(status().is(responseStatus.value()))
          .andReturn()
          .getResponse()
          .getContentAsString();
  }

  private MockMultipartFile uploadingFile (String mediaType) throws Exception{
    Resource fileResource = new ClassPathResource("/files/sample.png");
    return new MockMultipartFile("file",fileResource.getFilename(),
          mediaType, fileResource.getInputStream());
  }
}