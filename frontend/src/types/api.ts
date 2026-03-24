import type { HalResource } from "./hal"

export interface PatientData {
  personId: string
  firstName: string | null
  lastName: string | null
  streetAddress: string | null
  postalCode: string | null
  city: string | null
}

export interface CareProviderData {
  careProviderId: string
  careProviderName: string
}

export interface UnitData {
  unitId: string
  unitName: string
  streetAddress: string | null
  postalCode: string | null
  city: string | null
  phone: string | null
  email: string | null
  careProvider: CareProviderData | null
}

export interface StaffData {
  staffId: string
  fullName: string | null
  prescriptionCode: string | null
  unit: UnitData | null
}

export interface CertificateResponse extends HalResource {
  certificateId: string
  certificateType: string
  certificateTypeDisplayName: string
  signingTimestamp: string | null
  sentTimestamp: string | null
  version: string | null
  logicalAddress: string | null
  patient: PatientData | null
  issuedBy: StaffData | null
}

export interface PatientResponse extends HalResource {
  personId: string
  firstName: string | null
  lastName: string | null
  streetAddress: string | null
  postalCode: string | null
  city: string | null
}

export interface MessageResponse extends HalResource {
  messageId: string
  certificateId: string
  personId: string | null
  recipient: string | null
  subject: string | null
  heading: string | null
  body: string | null
  sentTimestamp: string | null
  sentByStaffId: string | null
  sentByFullName: string | null
}

export interface UnitResponse extends HalResource {
  unitId: string
  unitName: string
  streetAddress: string | null
  postalCode: string | null
  city: string | null
  phone: string | null
  email: string | null
  careProviderId: string | null
  careProviderName: string | null
}

export interface StaffResponse extends HalResource {
  staffId: string
  fullName: string | null
  prescriptionCode: string | null
  unitId: string | null
}

export interface StatusUpdateResponse extends HalResource {
  certificateId: string
  personId: string | null
  eventCode: string | null
  eventDisplayName: string | null
  eventTimestamp: string | null
  questionsSentTotal: number | null
  questionsReceivedTotal: number | null
}

export interface LogEntryResponse extends HalResource {
  logId: string
  systemId: string | null
  systemName: string | null
  activityType: string | null
  certificateId: string | null
  purpose: string | null
  activityStart: string | null
  userId: string | null
  userAssignment: string | null
  careUnitId: string | null
  careProviderName: string | null
}

export interface RevocationResponse extends HalResource {
  certificateId: string
  personId: string | null
  revokedAt: string | null
  reason: string | null
  revokedByStaffId: string | null
  revokedByFullName: string | null
}

export interface EntityModel<T> extends HalResource {
  content?: T
}

export interface CollectionModel<T> extends HalResource {
  _embedded?: { [key: string]: Array<T & HalResource> }
}

export interface PageMetadata {
  size: number
  totalElements: number
  totalPages: number
  number: number
}

export interface PagedModel<T> extends CollectionModel<T> {
  page: PageMetadata
}
