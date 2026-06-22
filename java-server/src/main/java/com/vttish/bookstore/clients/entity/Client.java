package com.vttish.bookstore.clients.entity;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client extends BaseTimestampEntity {

    @Id
    UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Formula("(SELECT COUNT(o.id) FROM orders o WHERE o.client_id = id)")
    private Integer ordersCount;
}
