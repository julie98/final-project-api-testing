package entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SprintState {
    private String goal;
    private String name;
    private String state;
    private String startDate;
    private String endDate;
    private String completeDate;
}
