package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto

import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.Referral
import java.time.format.DateTimeFormatter

data class DraftReferral(
  val id: String? = null,
  val created: String? = null,
  ) {
  constructor(referral: Referral): this(
      referral.id?.let { it.toString() },
      referral.created?.let { DateTimeFormatter.ISO_INSTANT.format(it.toInstant()) },
  )
}
