package entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Sprint {
    private String name;
    private String startDate;
    private String endDate;
    private int originBoardId;
    private String goal;
}
