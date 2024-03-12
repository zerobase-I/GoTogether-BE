package com.example.gotogetherbe.notification.repository;

import com.example.gotogetherbe.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
