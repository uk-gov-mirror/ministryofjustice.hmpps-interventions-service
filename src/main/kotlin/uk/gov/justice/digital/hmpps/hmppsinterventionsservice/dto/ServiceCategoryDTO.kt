package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto

import java.util.UUID

data class ServiceCategoryDTO(
  val id: UUID,
  val name: String,
  val complexityLevels: List<ComplexityLevelDTO>,
)
