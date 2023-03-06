package account.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChangeUserRoleDTO {
    @JsonProperty("user")
    private String userEmail;
    @JsonProperty("role")
    private String userRoleName;
    @JsonProperty("operation")
    private UserRoleOperation operation;

}
