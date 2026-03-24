import { useParams, Link } from "react-router-dom"
import { useQueries } from "@tanstack/react-query"
import { useEntityDetail } from "@/hooks/useEntityDetail"
import { RelatedResourceCard } from "@/components/RelatedResourceCard"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Skeleton } from "@/components/ui/skeleton"
import { fetchResource } from "@/lib/api"
import { embedded, hrefOptional } from "@/lib/hal"
import type {
  CertificateResponse,
  CollectionModel,
  MessageResponse,
  StatusUpdateResponse,
  LogEntryResponse,
  RevocationResponse,
} from "@/types/api"

function formatDateTime(ts: string | null): string {
  if (!ts) return "—"
  try {
    return new Date(ts).toLocaleString("sv-SE")
  } catch {
    return ts
  }
}

export function CertificateDetailPage() {
  const { id } = useParams<{ id: string }>()
  const certUrl = id ? `/api/navigate/certificates/${id}` : null

  const { data: cert, links, isLoading, isError } = useEntityDetail<CertificateResponse>(certUrl)

  const relatedQueries = useQueries({
    queries: [
      {
        queryKey: ["cert-messages", id],
        queryFn: () =>
          fetchResource<CollectionModel<MessageResponse>>(links!["messages"].href),
        enabled: !!links?.["messages"],
      },
      {
        queryKey: ["cert-status-updates", id],
        queryFn: () =>
          fetchResource<CollectionModel<StatusUpdateResponse>>(links!["status-updates"].href),
        enabled: !!links?.["status-updates"],
      },
      {
        queryKey: ["cert-log-entries", id],
        queryFn: () =>
          fetchResource<CollectionModel<LogEntryResponse>>(links!["log-entries"].href),
        enabled: !!links?.["log-entries"],
      },
      {
        queryKey: ["cert-revocation", id],
        queryFn: () => fetchResource<RevocationResponse>(links!["revocation"].href),
        enabled: !!links?.["revocation"],
        retry: false,
      },
    ],
  })

  const [messagesQuery, statusUpdatesQuery, logEntriesQuery, revocationQuery] = relatedQueries

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-40 w-full" />
      </div>
    )
  }

  if (isError || !cert) {
    return <p className="text-destructive">Certificate not found.</p>
  }

  const messages = messagesQuery.data
    ? embedded<MessageResponse>(messagesQuery.data, "messageResponseList")
    : []
  const statusUpdates = statusUpdatesQuery.data
    ? embedded<StatusUpdateResponse>(statusUpdatesQuery.data, "statusUpdateResponseList")
    : []
  const logEntries = logEntriesQuery.data
    ? embedded<LogEntryResponse>(logEntriesQuery.data, "logEntryResponseList")
    : []
  const revocation = revocationQuery.data ?? null
  const revocationNotFound =
    revocationQuery.isError &&
    (revocationQuery.error as Error & { status?: number }).status === 404

  const xmlHref = hrefOptional(cert, "xml")
  const patientPersonId = cert.patient?.personId
  const unitId = cert.issuedBy?.unit?.unitId
  const staffId = cert.issuedBy?.staffId

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-muted-foreground mb-1">Certificate</p>
          <h2 className="text-xl font-semibold font-mono">{cert.certificateId}</h2>
          <p className="text-muted-foreground text-sm mt-1">
            {cert.certificateTypeDisplayName ?? cert.certificateType}
          </p>
        </div>
        <div className="flex gap-2">
          {cert.sentTimestamp && <Badge variant="secondary">Sent</Badge>}
          {xmlHref && (
            <a
              href={xmlHref}
              target="_blank"
              rel="noreferrer"
              className="text-xs text-primary hover:underline"
            >
              View XML ↗
            </a>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
              Patient
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-1">
            {cert.patient ? (
              <>
                <p className="font-medium">
                  {[cert.patient.firstName, cert.patient.lastName].filter(Boolean).join(" ") || "—"}
                </p>
                {patientPersonId && (
                  <Link to={`/patients/${patientPersonId}`} className="text-primary hover:underline text-xs font-mono">
                    {patientPersonId}
                  </Link>
                )}
                {cert.patient.streetAddress && (
                  <p className="text-muted-foreground text-xs">
                    {cert.patient.streetAddress}
                    {cert.patient.city && `, ${cert.patient.city}`}
                  </p>
                )}
              </>
            ) : (
              <p className="text-muted-foreground">—</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
              Issuer
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-1">
            {cert.issuedBy ? (
              <>
                <p className="font-medium">{cert.issuedBy.fullName ?? "—"}</p>
                {staffId && (
                  <Link to={`/staff/${staffId}`} className="text-primary hover:underline text-xs font-mono">
                    {staffId}
                  </Link>
                )}
                {cert.issuedBy.prescriptionCode && (
                  <p className="text-muted-foreground text-xs">Rx: {cert.issuedBy.prescriptionCode}</p>
                )}
              </>
            ) : (
              <p className="text-muted-foreground">—</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
              Unit
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-1">
            {cert.issuedBy?.unit ? (
              <>
                <p className="font-medium">{cert.issuedBy.unit.unitName}</p>
                {unitId && (
                  <Link to={`/units/${unitId}`} className="text-primary hover:underline text-xs font-mono">
                    {unitId}
                  </Link>
                )}
                {cert.issuedBy.unit.careProvider && (
                  <p className="text-muted-foreground text-xs">
                    {cert.issuedBy.unit.careProvider.careProviderName}
                  </p>
                )}
              </>
            ) : (
              <p className="text-muted-foreground">—</p>
            )}
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="space-y-1 text-sm">
          <p className="text-muted-foreground text-xs uppercase tracking-wide">Signing time</p>
          <p>{formatDateTime(cert.signingTimestamp)}</p>
        </div>
        {cert.sentTimestamp && (
          <div className="space-y-1 text-sm">
            <p className="text-muted-foreground text-xs uppercase tracking-wide">Sent time</p>
            <p>{formatDateTime(cert.sentTimestamp)}</p>
          </div>
        )}
        {cert.version && (
          <div className="space-y-1 text-sm">
            <p className="text-muted-foreground text-xs uppercase tracking-wide">Version</p>
            <p>{cert.version}</p>
          </div>
        )}
        {cert.logicalAddress && (
          <div className="space-y-1 text-sm">
            <p className="text-muted-foreground text-xs uppercase tracking-wide">Logical address</p>
            <p className="font-mono text-xs">{cert.logicalAddress}</p>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <RelatedResourceCard
          title="Messages"
          isLoading={messagesQuery.isLoading}
          isError={messagesQuery.isError}
          isEmpty={messages.length === 0}
          emptyMessage="No messages"
        >
          <ul className="space-y-2 text-sm">
            {messages.map((m) => (
              <li key={m.messageId}>
                <Link to={`/messages/${m.messageId}`} className="flex flex-col gap-0.5 hover:bg-muted/50 rounded p-1 -m-1 transition-colors">
                  <span className="font-medium text-primary hover:underline">{m.subject ?? m.heading ?? "Message"}</span>
                  <span className="text-xs text-muted-foreground font-mono">{m.messageId}</span>
                  {m.sentTimestamp && (
                    <span className="text-xs text-muted-foreground">
                      {formatDateTime(m.sentTimestamp)}
                    </span>
                  )}
                </Link>
              </li>
            ))}
          </ul>
        </RelatedResourceCard>

        <RelatedResourceCard
          title="Status Updates"
          isLoading={statusUpdatesQuery.isLoading}
          isError={statusUpdatesQuery.isError}
          isEmpty={statusUpdates.length === 0}
          emptyMessage="No status updates"
        >
          <ul className="space-y-2 text-sm">
            {statusUpdates.map((su, i) => (
              <li key={i}>
                <Link to={`/certificates/${id}/status-updates`} className="flex flex-col gap-0.5 hover:bg-muted/50 rounded p-1 -m-1 transition-colors">
                  <span className="font-medium text-primary hover:underline">{su.eventDisplayName ?? su.eventCode ?? "Update"}</span>
                  {su.eventTimestamp && (
                    <span className="text-xs text-muted-foreground">
                      {formatDateTime(su.eventTimestamp)}
                    </span>
                  )}
                </Link>
              </li>
            ))}
          </ul>
        </RelatedResourceCard>

        <RelatedResourceCard
          title="Log Entries"
          isLoading={logEntriesQuery.isLoading}
          isError={logEntriesQuery.isError}
          isEmpty={logEntries.length === 0}
          emptyMessage="No log entries"
        >
          <ul className="space-y-2 text-sm">
            {logEntries.map((le) => (
              <li key={le.logId}>
                <Link to={`/log-entries/${le.logId}`} className="flex flex-col gap-0.5 hover:bg-muted/50 rounded p-1 -m-1 transition-colors">
                  <span className="font-medium text-primary hover:underline">{le.activityType ?? "Log Entry"}</span>
                  <span className="text-xs text-muted-foreground font-mono">{le.logId}</span>
                  {le.activityStart && (
                    <span className="text-xs text-muted-foreground">
                      {formatDateTime(le.activityStart)}
                    </span>
                  )}
                </Link>
              </li>
            ))}
          </ul>
        </RelatedResourceCard>

        <RelatedResourceCard
          title="Revocation"
          isLoading={revocationQuery.isLoading}
          isError={revocationQuery.isError && !revocationNotFound}
          isEmpty={!revocation && revocationNotFound}
          emptyMessage="Not revoked"
        >
          {revocation && (
            <Link to={`/certificates/${id}/revocation`} className="block hover:bg-muted/50 rounded p-1 -m-1 transition-colors">
              <div className="text-sm space-y-1">
                <p className="font-medium text-destructive hover:underline">Revoked</p>
                {revocation.revokedAt && (
                  <p className="text-xs text-muted-foreground">
                    {formatDateTime(revocation.revokedAt)}
                  </p>
                )}
                {revocation.reason && <p className="text-xs">{revocation.reason}</p>}
                {revocation.revokedByFullName && (
                  <p className="text-xs text-muted-foreground">by {revocation.revokedByFullName}</p>
                )}
              </div>
            </Link>
          )}
        </RelatedResourceCard>
      </div>
    </div>
  )
}
