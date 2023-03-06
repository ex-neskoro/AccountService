package account.rep;

import account.model.payment.Payment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findByEmployeeOrderByPeriodDesc(String email);

    @Transactional
    @Modifying
    @Query("update Payment p set p.salary = ?1 where p.employee = ?2 and p.period = ?3")
    void updateSalary(Long salary, String employee, YearMonth period);

    @Override
            @Transactional
    <S extends Payment> Iterable<S> saveAll(Iterable<S> entities);

    Optional<Payment> findByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);

    List<Payment> findAllByEmployeeIgnoreCaseOrderByPeriodDesc(String employee);

    Optional<Payment> findByEmployeeAndPeriod(String employee, YearMonth period);

    boolean existsByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);
}
