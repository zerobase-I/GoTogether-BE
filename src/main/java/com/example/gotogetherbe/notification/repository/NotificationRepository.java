package com.example.gotogetherbe.notification.repository;

import com.example.gotogetherbe.notification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByMember_EmailOrderByCreatedAtDesc(String email);

}
