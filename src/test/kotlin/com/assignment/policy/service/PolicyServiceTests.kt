package com.assignment.policy.service

import com.assignment.policy.controller.Deposit
import com.assignment.policy.listener.CompletedApplication
import com.assignment.policy.model.Policy
import com.assignment.policy.model.PolicyStatusEnum.*
import com.assignment.policy.repository.PolicyRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import javax.transaction.Transactional

@Transactional
@SpringBootTest
class PolicyServiceTests {
    @Autowired
    lateinit var policyService: PolicyService

    @Autowired
    lateinit var policyRepository: PolicyRepository

    private var deposit = Deposit(500.00)

    lateinit var savedPolicy: Policy

    @BeforeEach
    internal fun setup() {
        savedPolicy = policyService.createPolicy(CompletedApplication(
                firstName = "Ron",
                lastName = "Swanson",
                email = "rswanson@yahoo.com",
                phoneNumber = "444-555-6789"))
    }

    @Test
    fun `create policy`() {
        val retrievedPolicy = policyRepository.findById(savedPolicy.policyId).get()

        assertThat(retrievedPolicy).isEqualTo(savedPolicy)
        assertThat(retrievedPolicy.policyStatus).isEqualTo(AWAITING_PREMIUM_DEPOSIT.status)
    }

    @Test
    fun `deposit premium amount for policy`() {
        val updatedPolicy = policyService.depositPremium(savedPolicy.policyId, deposit).get()

        assertThat(updatedPolicy.policyStatus).isEqualTo(ACTIVE_CANCELLABLE.status)
        assertThat(updatedPolicy.depositedAmount).isEqualTo(deposit.amount)
        assertThat(updatedPolicy.policyTotal).isEqualTo(deposit.amount)
    }

    @Test
    fun `successfully handle monthiversary for policy`() {
        policyService.depositPremium(savedPolicy.policyId, deposit)
        val updatedPolicyWithMonthiversary = policyService.handleMonthiversary(savedPolicy.policyId).get()

        assertThat(updatedPolicyWithMonthiversary.policyTotal).isEqualTo(updatedPolicyWithMonthiversary.depositedAmount + (updatedPolicyWithMonthiversary.depositedAmount * updatedPolicyWithMonthiversary.interestRate))
        assertThat(updatedPolicyWithMonthiversary.policyStatus).isEqualTo(ACTIVE_CANCELLABLE.status)
    }

    @Test
    fun `fail to handle monthiversary for policy`() {
        val updatedPolicyWithOutDeposit = policyService.handleMonthiversary(savedPolicy.policyId)

        assertThat(updatedPolicyWithOutDeposit).isEqualTo(Optional.empty<Policy>())
    }

    @Test
    fun `successfully cancel policy`() {
        policyService.depositPremium(savedPolicy.policyId, deposit)
        val cancelledPolicy = policyService.cancelPolicy(savedPolicy.policyId).get()

        assertThat(cancelledPolicy.policyStatus).isEqualTo(CLOSED_CANCELLED.status)
    }

    @Test
    fun `fail to cancel policy`() {
        val policyWithoutDeposit = policyService.cancelPolicy(savedPolicy.policyId)

        assertThat(policyWithoutDeposit).isEqualTo(Optional.empty<Policy>())
    }

    @Test
    fun `successfully complete policy term`() {
        policyService.depositPremium(savedPolicy.policyId, deposit)
        policyService.handleMonthiversary(savedPolicy.policyId)
        val completedPolicy = policyService.completePolicyTerm(savedPolicy.policyId).get()

        assertThat(completedPolicy.policyStatus).isEqualTo(CLOSED_TERM_COMPLETED.status)
    }

    @Test
    fun `fail to complete policy term`() {
        policyService.depositPremium(savedPolicy.policyId, deposit)
        policyService.handleMonthiversary(savedPolicy.policyId)
        policyService.cancelPolicy(savedPolicy.policyId)
        val alreadyCancelledPolicy = policyService.completePolicyTerm(savedPolicy.policyId)

        assertThat(alreadyCancelledPolicy).isEqualTo(Optional.empty<Policy>())
    }

    @Test
    fun `successfully close policy for death of owner`() {
        policyService.depositPremium(savedPolicy.policyId, deposit)
        policyService.handleMonthiversary(savedPolicy.policyId)
        val closedPolicy = policyService.closePolicyForDeathOfOwner(savedPolicy.policyId).get()

        assertThat(closedPolicy.policyStatus).isEqualTo(CLOSED_OWNER_DIED.status)
    }

    @Test
    fun `fail to close policy for death of owner`() {
        policyService.depositPremium(savedPolicy.policyId, deposit)
        policyService.handleMonthiversary(savedPolicy.policyId)
        policyService.completePolicyTerm(savedPolicy.policyId)
        val alreadyCompletedPolicy =  policyService.closePolicyForDeathOfOwner(savedPolicy.policyId)

        assertThat(alreadyCompletedPolicy).isEqualTo(Optional.empty<Policy>())
    }
}