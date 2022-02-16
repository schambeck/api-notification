package com.schambeck.notification.representation;

import com.schambeck.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CountUnreadMessage {

    private final Long countUnread;
    private final Notification notification;

}
