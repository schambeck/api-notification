package com.schambeck.notification.representation;

import com.schambeck.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@RequiredArgsConstructor
public class CountUnreadMessage {

    private final Long countUnread;
    private final Notification notification;

}
