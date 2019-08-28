package com.assignment.policy.service

import com.assignment.policy.controller.Deposit
import com.assignment.policy.listener.CompletedApplication
import com.assignment.policy.model.Policy
import com.assignment.policy.model.PolicyStatusEnum.*
import com.assignment.policy.repository.PolicyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PolicyService {
    @Autowired
    lateinit var policyRepository: PolicyRepository

    fun createPolicy(completedApplication: CompletedApplication) =
        policyRepository.save(Policy(
                firstName = completedApplication.firstName,
                lastName = completedApplication.lastName,
                email = completedApplication.email,
                phoneNumber = completedApplication.phoneNumber
        ))

    fun depositPremium(policyId: UUID, deposit: Deposit): Optional<Policy> =
            policyRepository.findById(policyId).filter { it.policyStatus == AWAITING_PREMIUM_DEPOSIT.status }.map{
                policyRepository.save(it.copy(
                        depositedAmount = deposit.amount,
                        policyTotal = deposit.amount,
                        policyStatus = ACTIVE_CANCELLABLE.status)
            )}

    fun handleMonthiversary(policyId: UUID): Optional<Policy> =
            policyRepository.findById(policyId).filter { it.policyStatus == ACTIVE_CANCELLABLE.status }.map {
                policyRepository.save(it.copy(policyTotal = it.policyTotal + (it.policyTotal * it.interestRate))
            )}

    fun cancelPolicy(policyId: UUID): Optional<Policy> =
            policyRepository.findById(policyId).filter { it.policyStatus == ACTIVE_CANCELLABLE.status }.map {
                policyRepository.save(it.copy(policyStatus = CLOSED_CANCELLED.status)
            )}

    fun completePolicyTerm(policyId: UUID): Optional<Policy> =
            policyRepository.findById(policyId).filter { it.policyStatus == ACTIVE_CANCELLABLE.status }.map {
                policyRepository.save(it.copy(policyStatus = CLOSED_TERM_COMPLETED.status)
            )}

    fun closePolicyForDeathOfOwner(policyId: UUID): Optional<Policy> =
            policyRepository.findById(policyId).filter { it.policyStatus == ACTIVE_CANCELLABLE.status }.map {
                policyRepository.save(it.copy(policyStatus = CLOSED_OWNER_DIED.status)
            )}
}