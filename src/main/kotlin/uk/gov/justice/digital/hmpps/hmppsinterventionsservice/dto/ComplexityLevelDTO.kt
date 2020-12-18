package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto

import java.util.UUID

data class ComplexityLevelDTO(
  val id: UUID,
  val title: String,
  val description: String,
)

