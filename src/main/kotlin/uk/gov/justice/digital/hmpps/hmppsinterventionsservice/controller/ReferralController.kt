package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.CreateReferralRequestDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.DraftReferralDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.SentReferralDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.ServiceCategoryDTO
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.AuthUser
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.service.HMPPSAuthService
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.service.ReferralService
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.service.ServiceCategoryService
import java.util.UUID
import javax.persistence.EntityNotFoundException

@RestController
class ReferralController(
  private val referralService: ReferralService,
  private val serviceCategoryService: ServiceCategoryService,
  private val hmppsAuthService: HMPPSAuthService,
) {
  @PostMapping("/draft-referral/{id}/send")
  fun sendDraftReferral(@PathVariable id: UUID, authentication: JwtAuthenticationToken): ResponseEntity<SentReferralDTO> {
    val draftReferral = referralService.getDraftReferral(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "draft referral not found [id=$id]")

    val user = parseAuthUserToken(authentication)
    val sentReferral = referralService.sendDraftReferral(draftReferral, user)

    val location = ServletUriComponentsBuilder
      .fromCurrentContextPath()
      .path("/sent-referral/{id}")
      .buildAndExpand(sentReferral.id)
      .toUri()

    return ResponseEntity
      .created(location)
      .body(SentReferralDTO.from(sentReferral))
  }

  @GetMapping("/sent-referral/{id}")
  fun getSentReferral(@PathVariable id: UUID): SentReferralDTO {
    return referralService.getSentReferral(id)
      ?.let { SentReferralDTO.from(it) }
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "sent referral not found [id=$id]")
  }

  @GetMapping("/sent-referrals")
  fun getSentReferrals(authentication: JwtAuthenticationToken): List<SentReferralDTO> {
    val user = parseAuthUserToken(authentication)
    val organization = hmppsAuthService.getServiceProviderOrganizationForUser(user)
    return organization?.let {
      referralService.getSentReferralsForServiceProviderID(organization)
    } ?: throw AccessDeniedException("user is not associated with a service provider organization")
  }

  @PostMapping("/draft-referral")
  fun createDraftReferral(@RequestBody createReferralRequestDTO: CreateReferralRequestDTO, authentication: JwtAuthenticationToken): ResponseEntity<DraftReferralDTO> {
    val user = parseAuthUserToken(authentication)

    val referral = try {
      referralService.createDraftReferral(
        user,
        createReferralRequestDTO.serviceUserCrn,
        createReferralRequestDTO.interventionId,
      )
    } catch (e: EntityNotFoundException) {
      throw ServerWebInputException("invalid intervention id [id=${createReferralRequestDTO.interventionId}]")
    }

    val location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(referral.id)
      .toUri()

    return ResponseEntity
      .created(location)
      .body(DraftReferralDTO.from(referral))
  }

  @GetMapping("/draft-referral/{id}")
  fun getDraftReferralByID(@PathVariable id: UUID): DraftReferralDTO {
    return referralService.getDraftReferral(id)
      ?.let { DraftReferralDTO.from(it) }
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "draft referral not found [id=$id]")
  }

  @PatchMapping("/draft-referral/{id}")
  fun patchDraftReferralByID(@PathVariable id: UUID, @RequestBody partialUpdate: DraftReferralDTO): DraftReferralDTO {
    val referralToUpdate = referralService.getDraftReferral(id)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "draft referral not found [id=$id]")

    val updatedReferral = referralService.updateDraftReferral(referralToUpdate, partialUpdate)
    return DraftReferralDTO.from(updatedReferral)
  }

  @GetMapping("/draft-referrals")
  fun getDraftReferralsCreatedByUserID(@RequestParam userID: String): List<DraftReferralDTO> {
    return referralService.getDraftReferralsCreatedByUserID(userID)
  }

  @GetMapping("/service-category/{id}")
  fun getServiceCategoryByID(@PathVariable id: UUID): ServiceCategoryDTO {
    return serviceCategoryService.getServiceCategoryByID(id)
      ?.let { ServiceCategoryDTO.from(it) }
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "service category not found [id=$id]")
  }

  private fun parseAuthUserToken(authentication: JwtAuthenticationToken): AuthUser {
    // note: this does not allow tokens for client_credentials grant types use this API

    val userID = authentication.token.getClaimAsString("user_id")
      ?: throw ServerWebInputException("no 'user_id' claim in authentication token")

    val userName = authentication.token.getClaimAsString("user_name")
      ?: throw ServerWebInputException("no 'user_name' claim in authentication token")

    val authSource = authentication.token.getClaimAsString("auth_source")
      ?: throw ServerWebInputException("no 'auth_source' claim in authentication token")

    return AuthUser(id = userID, authSource = authSource, userName = userName)
  }
}
