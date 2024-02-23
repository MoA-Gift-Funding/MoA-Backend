package moa.announcement.query.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import moa.announcement.domain.Announcement;

public record AnnouncementResponse(
        Long id,
        String title,
        String content,

        @Schema(description = "공지일자", example = "2024-01-13 12:00:34")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate
) {
    public static List<AnnouncementResponse> from(List<Announcement> announcements) {
        return announcements.stream()
                .map(it -> new AnnouncementResponse(
                        it.getId(),
                        it.getTitle(),
                        it.getContent(),
                        it.getCreatedDate())
                ).toList();
    }
}
