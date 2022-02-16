package com.schambeck.notification.domain;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "type")
    private TypeNotification type;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "link")
    private String link;

    @Column(name = "read")
    private Boolean read;

}
