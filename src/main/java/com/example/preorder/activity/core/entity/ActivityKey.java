package com.example.preorder.activity.core.entity;

import com.example.preorder.common.event.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityKey implements Serializable {
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private Integer eventOrdinal;

    public static ActivityKey create(EventType type, Integer ordinal) {
        return new ActivityKey(type, ordinal);
    }
}
