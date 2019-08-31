package com.assignment.policy.controller

import com.assignment.policy.model.Policy
import com.assignment.policy.model.PolicyStatusEnum.*
import com.assignment.policy.service.PolicyService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest
class PolicyControllerTests {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var policyService: PolicyService

    private val objectMapper = ObjectMapper()

    private val newPolicy = Policy(
            firstName = "Ron",
            lastName = "Swanson",
            email = "rswanson@yahoo.com",
            phoneNumber = "444-555-6789"
    )

    private val deposit = Deposit(500.00)

    @Test
    fun `successfully deposit premium to policy`() {
        every { policyService.depositPremium(newPolicy.policyId, deposit) } returns
                Optional.of(newPolicy.copy(depositedAmount = deposit.amount, policyTotal = deposit.amount,
                        policyStatus = ACTIVE_CANCELLABLE.status))

        mvc.perform(put("/policy/{id}/depositPremium", newPolicy.policyId)
                .content(objectMapper.writeValueAsString(deposit))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk)
    }

    @Test
    fun `fails to deposit premium to policy`() {
        every { policyService.depositPremium(any(), any()) } returns Optional.empty()

        mvc.perform(put("/policy/{id}/depositPremium", UUID.randomUUID())
                .content(objectMapper.writeValueAsString(deposit))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `successfully handle monthiversary for policy`() {
        every { policyService.handleMonthiversary(newPolicy.policyId) } returns
                Optional.of(newPolicy.copy(policyTotal = deposit.amount + (deposit.amount * newPolicy.interestRate)))

        mvc.perform(put("/policy/{id}/monthiversary", newPolicy.policyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk)
    }

    @Test
    fun `fails to handle monthiversary for policy`() {
        every { policyService.handleMonthiversary(any()) } returns Optional.empty()

        mvc.perform(put("/policy/{id}/monthiversary", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `successfully cancels policy`() {
        every { policyService.cancelPolicy(newPolicy.policyId) } returns
                Optional.of(newPolicy.copy(policyStatus = CLOSED_CANCELLED.status))

        mvc.perform(put("/policy/{id}/cancelPolicy", newPolicy.policyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk)
    }

    @Test
    fun `fails to cancel policy`() {
        every { policyService.cancelPolicy(any()) } returns Optional.empty()

        mvc.perform(put("/policy/{id}/cancelPolicy", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `successfully complete policy term`() {
        every { policyService.completePolicyTerm(newPolicy.policyId) } returns
                Optional.of(newPolicy.copy(policyStatus = CLOSED_TERM_COMPLETED.status))

        mvc.perform(put("/policy/{id}/completePolicyTerm", newPolicy.policyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk)
    }

    @Test
    fun `fails to complete policy term`() {
        every { policyService.completePolicyTerm(any()) } returns Optional.empty()

        mvc.perform(put("/policy/{id}/completePolicyTerm", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `successfully close policy for death of owner`() {
        every { policyService.closePolicyForDeathOfOwner(newPolicy.policyId) } returns
                Optional.of(newPolicy.copy(policyStatus = CLOSED_OWNER_DIED.status))

        mvc.perform(put("/policy/{id}/closePolicyForDeath", newPolicy.policyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk)
    }

    @Test
    fun `fails to close policy for death of owner`() {
        every { policyService.closePolicyForDeathOfOwner(any()) } returns Optional.empty()

        mvc.perform(put("/policy/{id}/closePolicyForDeath", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound)
    }
}