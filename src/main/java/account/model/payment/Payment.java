package account.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.YearMonth;

@Entity
@Table(name = "payments",
        uniqueConstraints =
                {@UniqueConstraint(name = "UniqueEmployeeAndPeriod", columnNames = {"employee", "period"})})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    private String employee;
    private YearMonth period;
    private Long salary;
}
