package com.assignment.policy.listener

import com.assignment.policy.service.PolicyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * This service is utilized for listening to completed application on an event stream. This in-turn
 * delegates to the policy service to create a policy from the input payload.
 */

@Service
class CompletedApplicationListener {

    @Autowired
    lateinit var policyService: PolicyService

    fun listenForApplication(completedApplication: CompletedApplication) {
        policyService.createPolicy(completedApplication)
    }
}

data class CompletedApplication(var firstName: String, var lastName: String, var email: String, var phoneNumber: String)