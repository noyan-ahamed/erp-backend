package com.erp.enities;

import com.erp.enums.CustomerPaymentStatus;
import com.erp.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "customer_payments")
public class CustomerPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String voucherNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // optional specific invoice payment
    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrderHeader salesOrder;

    // who received the payment
    @ManyToOne
    @JoinColumn(name = "received_by_employee_id")
    private Employee receivedByEmployee;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String remarks;

    @Enumerated(EnumType.STRING)
    private CustomerPaymentStatus status = CustomerPaymentStatus.PENDING_APPROVAL;

    // admin approval tracking
    @ManyToOne
    @JoinColumn(name = "approved_by_user_id")
    private Users approvedBy;

    private LocalDate approvedDate;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
    }
}
