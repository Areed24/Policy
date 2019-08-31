package com.assignment.policy.model

import com.assignment.policy.model.PolicyStatusEnum.AWAITING_PREMIUM_DEPOSIT
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "POLICY")
data class Policy (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var policyId: UUID = UUID.randomUUID(),

        @Column(name = "createdDateTime")
        val createdDateTime: LocalDateTime = LocalDateTime.now(),

        @Column(name = "depositedAmount")
        var depositedAmount: Double = 0.0,

        @Column(name = "interestRate")
        var interestRate: Double = 0.05,

        @Column(name = "policyTotal")
        var policyTotal: Double = 0.0,

        @Column(name = "policyStatus")
        var policyStatus: String = AWAITING_PREMIUM_DEPOSIT.status,

        @Column(name = "firstName")
        var firstName: String = "",

        @Column(name = "lastName")
        var lastName: String = "",

        @Column(name = "email")
        var email: String = "",

        @Column(name = "phoneNumber")
        var phoneNumber: String = ""
)

enum class PolicyStatusEnum(val status: String) {
        AWAITING_PREMIUM_DEPOSIT("Awaiting Premium Deposit"),
        ACTIVE_CANCELLABLE("Active/Cancellable"),
        CLOSED_CANCELLED("Closed/Cancelled"),
        CLOSED_TERM_COMPLETED("Closed/Term Completed"),
        CLOSED_OWNER_DIED("Closed/Owner Died")
}