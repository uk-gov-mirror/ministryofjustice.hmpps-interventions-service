package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.controller

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.dto.DraftReferral
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity.Referral
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.repository.ReferralRepository
import java.util.UUID
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

@RestController
class ReferralController(private val repository: ReferralRepository) {
  @PostMapping("/draft-referral")
  fun createDraftReferral(): ResponseEntity<DraftReferral> {
    val referral = Referral()
    repository.save(referral)

    val location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(referral.id)
      .toUri()

    return ResponseEntity
      .created(location)
      .body(DraftReferral(referral))
  }

  @GetMapping("/draft-referral/{id}")
  fun getDraftReferralByID(@PathVariable id: String): ResponseEntity<Any> {
    val uuid = try {
      UUID.fromString(id)
    } catch (e: IllegalArgumentException) {
      return ResponseEntity.badRequest().body("malformed id")
    }

    return repository.findByIdOrNull(uuid)
      ?.let { ResponseEntity.ok(DraftReferral(it)) }
      ?: ResponseEntity.notFound().build()
  }

  @PatchMapping("/draft-referral/{id}")
  fun updateDraftReferral(@PathVariable id: String, @RequestBody updates: Map<String, Any>):
    ResponseEntity<Any> {
      val uuid = try {
        UUID.fromString(id)
      } catch (e: IllegalArgumentException) {
        return ResponseEntity.badRequest().body("malformed id")
      }

      // Ensure referral exists
      val referral = repository.findByIdOrNull(uuid)
      referral ?: return ResponseEntity.notFound().build()

      // Set individual (patch data) fields
      updates.forEach { (key, value) ->
        val property = Referral::class.memberProperties.find { it.name == key }
        if (property is KMutableProperty<*>) {
          property.setter.call(referral, value)
        }
      }
      repository.save(referral)

      return ResponseEntity
        .ok()
        .body(DraftReferral(referral))
    }
}
