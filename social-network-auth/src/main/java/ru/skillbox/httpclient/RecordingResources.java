package ru.skillbox.httpclient;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.configuration.FeignSupportConfig;
import ru.skillbox.dto.feign.PhotoDto;

@FeignClient(name = "ResourcesFeignClient", url = "${geo.base_request_url}", configuration = FeignSupportConfig.class)
public interface RecordingResources {

    @PostMapping(value = "${geo.saveCaptchaUrl}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    PhotoDto uploadCaptcha(@RequestPart MultipartFile file) throws FeignException.NotFound;
}
