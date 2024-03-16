package com.example.gotogetherbe.accompany.request.entity;

import com.example.gotogetherbe.accompany.request.type.AccompanyStatus;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.post.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Accompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_member_id")
    private Member requestMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_member_id")
    private Member requestedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccompanyStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    public void updateRequestStatus(AccompanyStatus accompanyStatus) {
        this.status = accompanyStatus;
    }

}
