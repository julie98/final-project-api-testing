package entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SprintResponse {
    private int id;
    private String self;
    private String state;
    private String name;
    private String startDate;
    private String endDate;
    private int originBoardId;
    private String goal;
    private boolean synced;
    private boolean autoStartStop;
    private String completeDate;
}
