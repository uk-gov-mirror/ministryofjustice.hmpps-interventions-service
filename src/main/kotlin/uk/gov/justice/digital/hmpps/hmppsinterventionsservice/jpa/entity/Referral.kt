package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.OneToOne
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotNull

@Entity
@Table(indexes = arrayOf(Index(columnList = "created_by_userid")))
data class Referral(
  // this seems like it should be a OneToMany relationship, however it is OneToOne
  // for good reason. all service user information we pull from nDelius can change,
  // and often does. storing a snapshot of the current information for each
  // referral avoids any issues which may arise due to out of date information.
  @OneToOne(mappedBy = "referral", cascade = arrayOf(CascadeType.ALL)) @PrimaryKeyJoinColumn var serviceUser: ServiceUser? = null,
  var additionalRiskInformation: String? = null,
  var furtherInformation: String? = null,
  var additionalNeedsInformation: String? = null,
  var accessibilityNeeds: String? = null,
  var needsInterpreter: Boolean? = null,
  var interpreterLanguage: String? = null,
  var hasAdditionalResponsibilities: Boolean? = null,
  var whenUnavailable: String? = null,
  var complexityLevelID: UUID? = null,
  var serviceCategoryID: UUID? = null,
  var usingRarDays: Boolean? = null,
  var maximumRarDays: Int? = null,
  @NotNull var createdByUserAuthSource: String? = null,
  @Column(name = "created_by_userid") @NotNull var createdByUserID: String? = null,
  @ElementCollection
  @CollectionTable(
    name = "referral_desired_outcome",
    uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("referral_id", "desired_outcome_id"))]
  )
  @Column(name = "desired_outcome_id")
  var desiredOutcomeIDs: List<UUID>? = null,
  var completionDeadline: LocalDate? = null,
  @CreationTimestamp var createdAt: OffsetDateTime? = null,
  @Id @GeneratedValue var id: UUID? = null,
)
