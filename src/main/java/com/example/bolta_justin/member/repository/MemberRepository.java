package com.example.bolta_justin.member.repository;

import com.example.bolta_justin.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByIdentifier(Integer identifier);
    Boolean existsMemberByEmail(String email);

}
