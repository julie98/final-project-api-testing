package entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectRole {
    private String name;
    private String description;
}
