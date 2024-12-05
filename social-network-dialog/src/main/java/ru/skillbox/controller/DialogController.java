package ru.skillbox.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.dto.*;
import ru.skillbox.service.DialogService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;


    @GetMapping("/recipientId/{recipientId}")
    public ResponseEntity<DialogDto> getDialog(@PathVariable UUID recipientId) {
        DialogDto response = dialogService.getDialog(recipientId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{dialogId}")
    public ResponseEntity<SetStatusMessageReadRs> setStatusMessageRead(@PathVariable Long dialogId) {
        SetStatusMessageReadRs response = dialogService.setStatusMessageRead(dialogId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<GetDialogsRs> getAllDialogs(
            @RequestParam(value = "offset", defaultValue = "0", required=false) Integer offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) Integer itemPerPage) {
        GetDialogsRs response = dialogService.getAllDialogs(PageRequest.of(offset, itemPerPage));
        return ResponseEntity.ok(response);
    }


    @GetMapping("/unread")
    public ResponseEntity<UnreadCountRs> getUnreadMessageCount() {
        UnreadCountRs response = dialogService.getUnreadMessageCount();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<GetMessagesRs> getAllMessages(
            @RequestParam("recipientId") UUID recipientId,
            @RequestParam(value = "offset", defaultValue = "0", required=false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20",required=false) int itemPerPage) {
        GetMessagesRs response = dialogService.getAllMessages(recipientId, PageRequest.of(offset, itemPerPage));
        return ResponseEntity.ok(response);
    }


}
