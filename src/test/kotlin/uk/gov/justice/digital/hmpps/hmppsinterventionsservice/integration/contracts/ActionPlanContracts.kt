package uk.gov.justice.digital.hmpps.hmppsinterventionsservice.integration.contracts

import au.com.dius.pact.provider.junitsupport.State
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.repository.ActionPlanAppointmentRepository
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.repository.ActionPlanRepository
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.jpa.repository.ReferralRepository
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.service.ActionPlanService
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.service.ReferralService
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.util.ActionPlanFactory
import uk.gov.justice.digital.hmpps.hmppsinterventionsservice.util.ReferralFactory
import java.util.UUID

class ActionPlanContracts(
  val entityManager: TestEntityManager,
  val actionPlanRepository: ActionPlanRepository,
  val referralRepository: ReferralRepository,
  val referralService: ReferralService,
  val actionPlanService: ActionPlanService,
  val actionPlanAppointmentRepository: ActionPlanAppointmentRepository,
  val referralFactory: ReferralFactory = ReferralFactory(entityManager),
  val actionPlanFactory: ActionPlanFactory = ActionPlanFactory(entityManager),
) {
  @State("an action plan with ID 81987e8b-aeb9-4fbf-8ecb-1a054ad74b2d exists with 1 appointment with recorded attendance")
  fun `create an empty action plan with 1 appointment that has had attendance recorded`() {
    val actionPlan = actionPlanFactory.create(id = UUID.fromString("81987e8b-aeb9-4fbf-8ecb-1a054ad74b2d"), numberOfSessions = 2)
    val appointment1 = actionPlanAppointmentRepository.findByActionPlanIdAndSessionNumber(actionPlan.id, 1)
    // println(appointment1)

    val referral = referralFactory.createDraft()
    println(referral)

    val ac = actionPlanService.createDraftActionPlan(actionPlan.referral.id, 2, listOf(), actionPlan.createdBy)
    actionPlanRepository.save(ac)

    // val referral = referralService.createDraftReferral(AuthUser("123", "delius", "tom"), "X862134", UUID.fromString("98a42c61-c30f-4beb-8062-04033c376e2d"))
    // referralRepository.save(referral)

    // val actionPlan = ActionPlan(
    //   id = UUID.fromString("81987e8b-aeb9-4fbf-8ecb-1a054ad74b2d"),
    //   createdBy = authUser,
    //   createdAt = OffsetDateTime.now(),
    //   referral = referral,
    //   activities = mutableListOf(),
    //   submittedAt = OffsetDateTime.now(),
    //   submittedBy = authUser,
    // )
    // actionPlanRepository.save(actionPlan)
    //
    // val appointment = appointmentsService.createAppointment(actionPlan, 1, OffsetDateTime.parse("2021-05-13T13:30:00+01:00"), 120, authUser)
    // appointment.attended = Attended.LATE
    // appointment.additionalAttendanceInformation = "Alex missed the bus"
    // appointment.attendanceSubmittedAt = OffsetDateTime.now()
    // actionPlanAppointmentRepository.save(appointment)
  }
}
