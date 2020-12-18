package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.ComplexityLevelDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.ServiceCategoryDTO
import java.util.*

@RestController
class ServiceCategoryController {
  @GetMapping("/service-category/{id}")
  fun getServiceCateoryByID(@PathVariable id: String): ServiceCategoryDTO {
    // dummy data for now

    val complexityLevels = listOf(
      ComplexityLevelDTO(
        UUID.fromString("d0db50b0-4a50-4fc7-a006-9c97530e38b2"),
        "Low complexity",
        "Service User has some capacity and means to secure and/or maintain suitable accommodation but requires some support and guidance to do so."
      ),
      ComplexityLevelDTO(
        UUID.fromString("110f2405-d944-4c15-836c-0c6684e2aa78"),
        "Medium complexity",
        "Service User is at risk of homelessness/is homeless, or will be on release from prison. Service User has had some success in maintaining atenancy but may have additional needs e.g. Learning Difficulties and/or Learning Disabilities or other challenges currently."
      ),
      ComplexityLevelDTO(
        UUID.fromString("c86be5ec-31fa-4dfa-8c0c-8fe13451b9f6"),
        "High complexity",
        "Service User is homeless or in temporary/unstable accommodation, or will be on release from prison. Service User has poor accommodation history, complex needs and limited skills to secure or sustain a tenancy."
      ),
    )

    return ServiceCategoryDTO(
      UUID.fromString("428ee70f-3001-4399-95a6-ad25eaaede16"),
    "accomodation",
    complexityLevels)
  }
}
