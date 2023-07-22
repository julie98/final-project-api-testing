package entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Project {
    private String key;
    private String name;
    private String projectTypeKey;
    private String lead;
}
