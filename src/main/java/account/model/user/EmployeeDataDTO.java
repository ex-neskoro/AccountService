package account.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeDataDTO {
    private String name;
    private String lastname;
    @JsonFormat(pattern = "MMMM-yyyy", locale = "en")
    private YearMonth period;
    private String salary;
}
