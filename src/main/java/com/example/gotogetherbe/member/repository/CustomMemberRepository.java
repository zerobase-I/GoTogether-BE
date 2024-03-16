package com.example.gotogetherbe.member.repository;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.member.entitiy.Member;
import java.util.List;

public interface CustomMemberRepository {

    List<Member> findAllByMemberIdIn(List<Long> memberIds);
}
