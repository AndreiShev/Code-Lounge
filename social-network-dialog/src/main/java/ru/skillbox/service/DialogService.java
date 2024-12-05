package ru.skillbox.service;


import org.springframework.data.domain.Pageable;
import ru.skillbox.dto.*;

import java.util.UUID;

public interface DialogService {

    public SetStatusMessageReadRs setStatusMessageRead(Long dialogId);

    public GetDialogsRs getAllDialogs(Pageable pageable);

    public UnreadCountRs getUnreadMessageCount();

    public GetMessagesRs getAllMessages(UUID companionId, Pageable pageable);

    public DialogDto getDialog(UUID companionId);

}
