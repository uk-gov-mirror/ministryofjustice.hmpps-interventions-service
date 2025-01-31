package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.entity

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.OffsetDateTime
import java.util.UUID
import javax.persistence.CollectionTable
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "end_of_service_report")
data class EndOfServiceReport(
  @Id val id: UUID,

  @NotNull val createdAt: OffsetDateTime,
  @NotNull @ManyToOne @Fetch(FetchMode.JOIN) val createdBy: AuthUser,

  var submittedAt: OffsetDateTime? = null,
  @ManyToOne @Fetch(FetchMode.JOIN) var submittedBy: AuthUser? = null,

  var furtherInformation: String? = null,

  // Outcomes
  @ElementCollection
  @CollectionTable(name = "end_of_service_report_outcome", joinColumns = [JoinColumn(name = "end_of_service_report_id")])
  @NotNull val outcomes: MutableSet<EndOfServiceReportOutcome> = mutableSetOf(),

)
