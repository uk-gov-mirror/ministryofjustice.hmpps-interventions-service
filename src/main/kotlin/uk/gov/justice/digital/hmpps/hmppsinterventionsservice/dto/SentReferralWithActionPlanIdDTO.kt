package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto

import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.Referral
import java.time.OffsetDateTime
import java.util.UUID

class SentReferralWithActionPlanIdDTO(
  val id: UUID,
  val sentAt: OffsetDateTime,
  val sentBy: AuthUserDTO,
  val referenceNumber: String,
  val assignedTo: AuthUserDTO?,
  val referral: DraftReferralDTO,
  val actionPlanId: UUID?,
) {
  companion object {
    fun from(referral: Referral): SentReferralWithActionPlanIdDTO {
      return SentReferralWithActionPlanIdDTO(
        id = referral.id,
        sentAt = referral.sentAt!!,
        sentBy = AuthUserDTO.from(referral.sentBy!!),
        referenceNumber = referral.referenceNumber!!,
        assignedTo = referral.assignedTo?.let { AuthUserDTO.from(it) },
        referral = DraftReferralDTO.from(referral),
        actionPlanId = referral.actionPlan?.id
      )
    }
  }
}
