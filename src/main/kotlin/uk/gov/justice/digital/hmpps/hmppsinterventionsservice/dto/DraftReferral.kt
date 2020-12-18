package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto

import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.Referral
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class DraftReferral(
  val id: UUID? = null,
  val createdAt: OffsetDateTime? = null,
  val completionDeadline: LocalDate? = null,
  val createdByUserId: String? = null,
  val serviceCategoryId: UUID? = null,
  val complexityLevelId: UUID? = null,
) {
  constructor(referral: Referral) : this(
    id = referral.id!!,
    createdAt = referral.createdAt!!,
    completionDeadline = referral.completionDeadline,
    createdByUserId = referral.createdByUserID,
  )
}
