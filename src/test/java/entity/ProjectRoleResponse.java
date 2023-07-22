package entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectRoleResponse {
    private String self;
    private String name;
    private String id;
    private String description;
}
