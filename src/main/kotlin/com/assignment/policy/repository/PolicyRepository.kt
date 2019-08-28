package com.assignment.policy.repository

import com.assignment.policy.model.Policy
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PolicyRepository : JpaRepository<Policy, UUID>