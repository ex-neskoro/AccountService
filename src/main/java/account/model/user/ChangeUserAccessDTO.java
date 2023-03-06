package account.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangeUserAccessDTO {
    @JsonProperty("user")
    private String userEmail;
    private UserOperation operation;

    public ChangeUserAccessDTO(String userEmail, UserOperation operation) {
        this.userEmail = userEmail;
        this.operation = operation;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserOperation getOperation() {
        return operation;
    }

    public void setOperation(UserOperation operation) {
        this.operation = operation;
    }
}
