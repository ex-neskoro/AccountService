package account.model.payment;

import account.model.payment.Payment;
import account.model.payment.PaymentDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public Payment PaymentDTOToPayment(PaymentDTO PaymentDTO) {
        return Payment
                .builder()
                .employee(PaymentDTO.getEmployee())
                .period(PaymentDTO.getPeriod())
                .salary(PaymentDTO.getSalary())
                .build();
    }

    public List<Payment> PaymentDTOsToPayments(List<PaymentDTO> payments) {
        return payments
                .stream()
                .map(this::PaymentDTOToPayment)
                .collect(Collectors.toList());
    }

    public PaymentDTO paymentToPaymentDTO(Payment payment){
        return PaymentDTO
                .builder()
                .employee(payment.getEmployee())
                .period(payment.getPeriod())
                .salary(payment.getSalary())
                .build();
    }
}
