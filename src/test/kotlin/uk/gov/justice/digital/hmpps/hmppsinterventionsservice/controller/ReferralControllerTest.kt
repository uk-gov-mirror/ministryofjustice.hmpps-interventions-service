package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.DraftReferral
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.integration.IntegrationTestBase

class ReferralControllerTest : IntegrationTestBase() {

  @Test
  fun `access forbidden when unauthorised`() {
    webTestClient.get().uri("/draft-referral")
      .header("Content-Type", "application/json")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  // This is wrong as we should not be creating the createdDate and id
  fun `create a new referral returns a draft referral`() {

    val createdDate = "2020-12-04 10:42:43"
    val id = "70d3a47c-d539-4f76-8fc9-1c50e34aea29"

    val draftReferral = webTestClient.post().uri("/draft-referral")
      .bodyValue(DraftReferral(id = id, created = createdDate))
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<DraftReferral>()
      .returnResult()
      .responseBody

    assertThat(draftReferral?.id).isEqualTo("70d3a47c-d539-4f76-8fc9-1c50e34aea29")
    assertThat(draftReferral?.created).isEqualTo("2020-12-04T10:42:43Z")
  }
}
