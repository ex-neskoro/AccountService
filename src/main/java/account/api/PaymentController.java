package account.api;

import account.model.payment.PaymentDTO;
import account.model.StatusDTO;
import account.service.PaymentService;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class PaymentController {
    private PaymentService paymentService;

    @PostMapping("api/acct/payments")
    public StatusDTO addPayments(@RequestBody @UniqueElements List<@Valid PaymentDTO> payments) {
        return paymentService.addPayments(payments);
    }

    @PutMapping("api/acct/payments")
    public StatusDTO updatePayments(@RequestBody @Valid PaymentDTO paymentDTO) {
        return paymentService.updatePayment(paymentDTO);
    }

    @GetMapping("api/empl/payment")
    public Object getPayment(@RequestParam(required = false) @DateTimeFormat(pattern = "MM-yyyy") Calendar period) {
        if (period != null) {
            return paymentService.getCurrentEmployeeDataByPeriod(calendarToYearMonth(period));
        } else {
            return paymentService.getAllCurrentEmployeeData();
        }
    }

    private YearMonth calendarToYearMonth(Calendar calendar) {
        return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }
}
