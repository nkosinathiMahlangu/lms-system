package team2.lmssystem.dto.respond;

import lombok.*;

/** Response payload for a leave type — returned on list, create, and update. */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class LeaveTypeResponse {

    private Long id;
    private String name;
    private int defaultDays;
}
