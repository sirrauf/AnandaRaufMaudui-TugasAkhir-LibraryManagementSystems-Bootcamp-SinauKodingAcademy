package dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime membershipDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}