package com.example.gotogetherbe.member.repository.impl;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.QMember;
import com.example.gotogetherbe.member.repository.CustomMemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final JPAQueryFactory jpa;

    @Override
    public List<Member> findAllByMemberIdIn(List<Long> memberIds) {
        QMember member = QMember.member;

        return jpa.selectFrom(member)
            .where(member.id.in(memberIds))
            .fetch();
    }
}
