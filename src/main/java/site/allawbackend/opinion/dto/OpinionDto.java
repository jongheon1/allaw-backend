package site.allawbackend.opinion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record OpinionDto(
        Long id,
        String userName,
        int billsNo,
        String detail,
        int grade,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}
