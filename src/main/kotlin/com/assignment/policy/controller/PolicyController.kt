package com.assignment.policy.controller

import com.assignment.policy.model.Policy
import com.assignment.policy.service.PolicyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/policy")
class PolicyController(private val policyService: PolicyService) {

    @PutMapping("/{id}/depositPremium")
    fun depositPremium(@PathVariable id: UUID, @RequestBody deposit: Deposit): ResponseEntity<Policy> =
            policyService.depositPremium(id, deposit).map {
                ResponseEntity.ok().body(it)
            }.orElse(ResponseEntity.notFound().build())

    @PutMapping("/{id}/monthiversary")
    fun handleMonthiversary(@PathVariable id: UUID): ResponseEntity<Policy> =
            policyService.handleMonthiversary(id).map {
                ResponseEntity.ok().body(it)
            }.orElse(ResponseEntity.notFound().build())

    @PutMapping("/{id}/cancelPolicy")
    fun cancelPolicy(@PathVariable id: UUID): ResponseEntity<Policy> =
            policyService.cancelPolicy(id).map {
                ResponseEntity.ok().body(it)
            }.orElse(ResponseEntity.notFound().build())

    @PutMapping("/{id}/completePolicyTerm")
    fun completePolicyTerm(@PathVariable id: UUID): ResponseEntity<Policy> =
            policyService.completePolicyTerm(id).map {
                ResponseEntity.ok().body(it)
            }.orElse(ResponseEntity.notFound().build())

    @PutMapping("/{id}/closePolicyForDeath")
    fun closePolicyForDeathOfOwner(@PathVariable id: UUID): ResponseEntity<Policy> =
            policyService.closePolicyForDeathOfOwner(id).map {
                ResponseEntity.ok().body(it)
            }.orElse(ResponseEntity.notFound().build())
}

data class Deposit(var amount: Double)