package com.example.gotogetherbe.accompany.review.entity;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.post.entity.Post;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Review {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "reviewer_id")
        private Member reviewer;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "target_member_id")
        private Member targetMember;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id")
        private Post post;

        @Column(nullable = false)
        private Double score;

        @Column(nullable = false)
        private boolean punctuality;

        @Column(nullable = false)
        private boolean responsiveness;

        @Column(nullable = false)
        private boolean photography;

        @Column(nullable = false)
        private boolean manner;

        @Column(nullable = false)
        private boolean navigation;

        @Column(nullable = false)
        private boolean humor;

        @Column(nullable = false)
        private boolean adaptability;

        @CreatedDate
        private LocalDateTime createdAt;

}
