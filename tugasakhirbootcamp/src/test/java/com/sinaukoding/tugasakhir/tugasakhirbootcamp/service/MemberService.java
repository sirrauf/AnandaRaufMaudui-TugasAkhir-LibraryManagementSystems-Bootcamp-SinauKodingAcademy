package com.sinaukoding.tugasakhir.tugasakhirbootcamp.service;

import dto.MemberDto;
import entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.MemberRepository;
import service.MemberService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberDto memberDto;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhone("1234567890");
        member.setAddress("123 Main St");

        memberDto = new MemberDto();
        memberDto.setId(1L);
        memberDto.setName("John Doe");
        memberDto.setEmail("john.doe@example.com");
        memberDto.setPhone("1234567890");
        memberDto.setAddress("123 Main St");
    }

    @Test
    void getAllMembers_Success() {
        when(memberRepository.findAll()).thenReturn(Arrays.asList(member));

        List<Member> members = memberService.getAllMembers();

        assertNotNull(members);
        assertFalse(members.isEmpty());
        assertEquals(1, members.size());
        assertEquals("John Doe", members.get(0).getName());
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void getMemberById_Found() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        Optional<Member> foundMember = memberService.getMemberById(1L);

        assertTrue(foundMember.isPresent());
        assertEquals("John Doe", foundMember.get().getName());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void getMemberById_NotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Member> foundMember = memberService.getMemberById(1L);

        assertFalse(foundMember.isPresent());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void createMember_Success() {
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member createdMember = memberService.createMember(memberDto);

        assertNotNull(createdMember);
        assertEquals(memberDto.getName(), createdMember.getName());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_Success() {
        Member updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setName("Jane Doe");
        updatedMember.setEmail("jane.doe@example.com");
        updatedMember.setPhone("0987654321");
        updatedMember.setAddress("456 Oak Ave");

        MemberDto updatedMemberDto = new MemberDto();
        updatedMemberDto.setId(1L);
        updatedMemberDto.setName("Jane Doe");
        updatedMemberDto.setEmail("jane.doe@example.com");
        updatedMemberDto.setPhone("0987654321");
        updatedMemberDto.setAddress("456 Oak Ave");

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member)); // Return original member
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        Member result = memberService.updateMember(1L, updatedMemberDto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane.doe@example.com", result.getEmail());
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_NotFound_ThrowsException() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> memberService.updateMember(1L, memberDto));
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void deleteMember_Success() {
        doNothing().when(memberRepository).deleteById(anyLong());

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).deleteById(1L);
    }
}
