package account.service;

import account.model.*;
import account.model.payment.Payment;
import account.model.payment.PaymentDTO;
import account.model.payment.PaymentMapper;
import account.model.user.CurrentUser;
import account.model.user.EmployeeDataDTO;
import account.model.user.User;
import account.rep.PaymentRepository;
import account.rep.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PaymentService {
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;
    private PaymentMapper paymentMapper;
    private CurrentUser currentUser;

    @Transactional
    public StatusDTO addPayments(List<PaymentDTO> paymentDTOs) {

        List<Payment> payments = paymentMapper.PaymentDTOsToPayments(paymentDTOs);

        for (var payment : payments) {

            if (!userRepository.existsByEmailIgnoreCase(payment.getEmployee())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee with specified email not found");
            }

            if (paymentRepository.existsByEmployeeIgnoreCaseAndPeriod(payment.getEmployee(), payment.getPeriod())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record already created");
            }

            paymentRepository.save(payment);
        }

        return new StatusDTO("Added successfully!");
    }

    @Transactional
    public StatusDTO updatePayment(PaymentDTO PaymentDTO) {
        Payment payment = paymentRepository
                .findByEmployeeIgnoreCaseAndPeriod(PaymentDTO.getEmployee(), PaymentDTO.getPeriod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record not found"));

        payment.setSalary(PaymentDTO.getSalary());
        paymentRepository.save(payment);

        return new StatusDTO("Updated successfully!");
    }

    public EmployeeDataDTO getCurrentEmployeeDataByPeriod(YearMonth period) {
        User currUser = currentUser
                .getCurrentUser()
                .getUserEntity();

        Payment payment = paymentRepository
                .findByEmployeeIgnoreCaseAndPeriod(currUser.getEmail(), period)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record with the specified period not found"));

        return EmployeeDataDTO
                .builder()
                .name(currUser.getName())
                .lastname(currUser.getLastName())
                .period(payment.getPeriod())
                .salary(centsToStrDollarsCents(payment.getSalary()))
                .build();
    }

    public List<EmployeeDataDTO> getAllCurrentEmployeeData() {
        User currUser = currentUser
                .getCurrentUser()
                .getUserEntity();

        List<Payment> payments = paymentRepository.findAllByEmployeeIgnoreCaseOrderByPeriodDesc(currUser.getEmail());

        return payments
                .stream()
                .map(p -> EmployeeDataDTO
                        .builder()
                        .name(currUser.getName())
                        .lastname(currUser.getLastName())
                        .period(p.getPeriod())
                        .salary(centsToStrDollarsCents(p.getSalary()))
                        .build())
                .collect(Collectors.toList());
    }

    private String centsToStrDollarsCents(long cents) {
        return String.format("%d dollar(s) %d cent(s)", cents / 100, cents % 100);
    }
}
