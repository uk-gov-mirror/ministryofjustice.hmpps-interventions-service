package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.controller

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.component.LocationMapper
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.controller.mappers.EndOfServiceReportOutcomeMapper
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.controller.mappers.JwtAuthUserMapper
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.CreateEndOfServiceReportDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.CreateEndOfServiceReportOutcomeDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.EndOfServiceReportDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.UpdateEndOfServiceReportDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.AchievementLevel
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.AuthUser
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.SampleData
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.service.EndOfServiceReportService
import java.net.URI
import java.util.UUID

class EndOfServiceReportControllerTest {
  private val jwtAuthUserMapper = mock<JwtAuthUserMapper>()
  private val endOfServiceReportService = mock<EndOfServiceReportService>()
  private val locationMapper = mock<LocationMapper>()
  private val endOfServiceReportOutcomeMapper = mock<EndOfServiceReportOutcomeMapper>()

  private val endOfServiceReportController =
    EndOfServiceReportController(jwtAuthUserMapper, locationMapper, endOfServiceReportService, endOfServiceReportOutcomeMapper)

  @Test
  fun `create end of service report successfully`() {
    val referralId = UUID.randomUUID()
    val jwtAuthenticationToken = JwtAuthenticationToken(mock())
    val authUser = AuthUser("CRN123", "auth", "user")
    val endOfServiceReport = SampleData.sampleEndOfServiceReport()
    val endOfServiceReportDTO = EndOfServiceReportDTO.from(endOfServiceReport)
    val uri = URI.create("http://localhost/1234")
    val createEndOfServiceReportDTO = CreateEndOfServiceReportDTO(referralId)

    whenever(jwtAuthUserMapper.map(jwtAuthenticationToken)).thenReturn(authUser)
    whenever(endOfServiceReportService.createEndOfServiceReport(referralId, authUser)).thenReturn(endOfServiceReport)
    whenever(locationMapper.expandPathToCurrentRequestBaseUrl("/{id}", endOfServiceReportDTO.id)).thenReturn(uri)

    val endOfServiceReportResponse = endOfServiceReportController.createEndOfServiceReport(createEndOfServiceReportDTO, jwtAuthenticationToken)
    assertThat(endOfServiceReportResponse.let { it.body }).isEqualTo(endOfServiceReportDTO)
    assertThat(endOfServiceReportResponse.let { it.headers["location"] }).isEqualTo(listOf(uri.toString()))
  }

  @Test
  fun `get end of service report successfully`() {
    val endOfServiceReportId = UUID.randomUUID()
    val endOfServiceReport = SampleData.sampleEndOfServiceReport()
    val endOfServiceReportDTO = EndOfServiceReportDTO.from(endOfServiceReport)

    whenever(endOfServiceReportService.getEndOfServiceReport(endOfServiceReportId)).thenReturn(endOfServiceReport)

    val endOfServiceReportResponse = endOfServiceReportController.getEndOfServiceReportById(endOfServiceReportId)
    assertThat(endOfServiceReportResponse).isEqualTo(endOfServiceReportDTO)
  }

  @Test
  fun `get end of service report by referral id successfully`() {
    val referralId = UUID.randomUUID()
    val endOfServiceReport = SampleData.sampleEndOfServiceReport()
    val endOfServiceReportDTO = EndOfServiceReportDTO.from(endOfServiceReport)

    whenever(endOfServiceReportService.getEndOfServiceReportByReferralId(referralId)).thenReturn(endOfServiceReport)

    val endOfServiceReportResponse = endOfServiceReportController.getEndOfServiceReportByReferralId(referralId)
    assertThat(endOfServiceReportResponse).isEqualTo(endOfServiceReportDTO)
  }

  @Test
  fun `update an end of service report`() {
    val endOfServiceReportId = UUID.randomUUID()
    val outcome = CreateEndOfServiceReportOutcomeDTO(UUID.randomUUID(), AchievementLevel.ACHIEVED, null, null)
    val mappedOutcome = SampleData.sampleEndOfServiceReportOutcome()
    val updateEndOfServiceReportDTO = UpdateEndOfServiceReportDTO("info", outcome)

    val endOfServiceReport = SampleData.sampleEndOfServiceReport()
    val endOfServiceReportDTO = EndOfServiceReportDTO.from(endOfServiceReport)

    whenever(endOfServiceReportOutcomeMapper.mapCreateEndOfServiceReportOutcomeDtoToEndOfServiceReportOutcome(outcome))
      .thenReturn(mappedOutcome)
    whenever(endOfServiceReportService.updateEndOfServiceReport(endOfServiceReportId, "info", mappedOutcome))
      .thenReturn(endOfServiceReport)

    val endOfServiceReportResponse = endOfServiceReportController.updateEndOfServiceReport(endOfServiceReportId, updateEndOfServiceReportDTO)
    assertThat(endOfServiceReportResponse).isEqualTo(endOfServiceReportDTO)
  }

  @Test
  fun `update end of service report with no extra outcome`() {
    val endOfServiceReportId = UUID.randomUUID()
    val outcome = CreateEndOfServiceReportOutcomeDTO(UUID.randomUUID(), AchievementLevel.ACHIEVED, null, null)
    val updateEndOfServiceReportDTO = UpdateEndOfServiceReportDTO("info", outcome)

    val endOfServiceReport = SampleData.sampleEndOfServiceReport()
    val endOfServiceReportDTO = EndOfServiceReportDTO.from(endOfServiceReport)

    whenever(endOfServiceReportService.updateEndOfServiceReport(endOfServiceReportId, "info", null))
      .thenReturn(endOfServiceReport)
    verifyZeroInteractions(endOfServiceReportOutcomeMapper)
    val endOfServiceReportResponse = endOfServiceReportController.updateEndOfServiceReport(endOfServiceReportId, updateEndOfServiceReportDTO)
    assertThat(endOfServiceReportResponse).isEqualTo(endOfServiceReportDTO)
  }

  @Test
  fun `submit end of service report`() {
    val endOfServiceReportId = UUID.randomUUID()
    val jwtAuthenticationToken = JwtAuthenticationToken(mock())
    val authUser = AuthUser("CRN123", "auth", "user")
    val endOfServiceReport = SampleData.sampleEndOfServiceReport(id = endOfServiceReportId)
    val uri = URI.create("http://localhost/end-of-service-report/1234")

    whenever(jwtAuthUserMapper.map(jwtAuthenticationToken)).thenReturn(authUser)
    whenever(locationMapper.expandPathToCurrentRequestBaseUrl("/end-of-service-report/{id}", endOfServiceReportId)).thenReturn(uri)
    whenever(endOfServiceReportService.submitEndOfServiceReport(endOfServiceReportId, authUser)).thenReturn(endOfServiceReport)

    val responseEntity = endOfServiceReportController.submitEndOfServiceReport(endOfServiceReportId, jwtAuthenticationToken)

    assertThat(responseEntity.headers["location"]).isEqualTo(listOf(uri.toString()))
    assertThat(responseEntity.body).isEqualTo(EndOfServiceReportDTO.from(endOfServiceReport))
  }
}
