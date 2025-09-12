package service;

//import com.sinaukoding.tugasakhir.tugasakhirbootcamp.dto.MemberDto;
//import com.sinaukoding.tugasakhir.tugasakhirbootcamp.entity.Member;
//import com.sinaukoding.tugasakhir.tugasakhirbootcamp.repository.MemberRepository;
import dto.MemberDto;
import entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member createMember(MemberDto memberDto) {
        Member member = new Member();
        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());
        member.setAddress(memberDto.getAddress());

        return memberRepository.save(member);
    }

    public Member updateMember(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());
        member.setAddress(memberDto.getAddress());

        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
