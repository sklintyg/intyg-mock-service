import { useParams, Link } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { fetchResource } from "@/lib/api"
import { embedded, hrefOptional } from "@/lib/hal"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import type { CollectionModel, StatusUpdateResponse } from "@/types/api"
import type { HalResource } from "@/types/hal"

function formatDateTime(ts: string | null): string {
  if (!ts) return "—"
  try {
    return new Date(ts).toLocaleString("sv-SE")
  } catch {
    return ts
  }
}

function Field({ label, value }: { label: string; value: string | number | null | undefined }) {
  return (
    <div className="space-y-1">
      <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">{label}</p>
      <p className="text-sm font-medium">{value != null ? String(value) : "—"}</p>
    </div>
  )
}

export function StatusUpdatesDetailPage() {
  const { certId } = useParams<{ certId: string }>()

  const query = useQuery<CollectionModel<StatusUpdateResponse & HalResource>>({
    queryKey: ["cert-status-updates-detail", certId],
    queryFn: () =>
      fetchResource<CollectionModel<StatusUpdateResponse & HalResource>>(
        `/api/navigate/certificates/${certId}/status-updates`
      ),
    enabled: !!certId,
  })

  if (query.isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-64" />
        <Skeleton className="h-48 w-full" />
      </div>
    )
  }

  if (query.isError || !query.data) {
    return <p className="text-destructive">Status updates not found.</p>
  }

  const updates = embedded<StatusUpdateResponse & HalResource>(query.data, "statusUpdateResponseList")

  return (
    <div className="space-y-10">
      <div>
        <p className="text-xs text-muted-foreground uppercase tracking-[0.05em] mb-2">
          Certificate{" "}
          {certId && (
            <Link to={`/certificates/${certId}`} className="text-primary hover:underline font-mono normal-case">
              {certId}
            </Link>
          )}
        </p>
        <h1 className="text-3xl font-bold text-foreground">Status Updates</h1>
        <p className="text-sm text-muted-foreground mt-1">{updates.length} update{updates.length !== 1 ? "s" : ""}</p>
      </div>

      {updates.length === 0 ? (
        <p className="text-muted-foreground text-sm py-12 text-center">No status updates.</p>
      ) : (
        <div className="space-y-6">
          {updates.map((su, i) => {
            const xmlHref = hrefOptional(su, "xml")
            return (
              <Card key={i}>
                <CardHeader className="pb-2">
                  <div className="flex items-start justify-between">
                    <CardTitle className="text-base font-semibold"
                      style={{ fontFamily: "var(--font-display)" }}
                    >
                      {su.eventDisplayName ?? su.eventCode ?? `Update ${i + 1}`}
                    </CardTitle>
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
                </CardHeader>
                <CardContent className="grid grid-cols-2 md:grid-cols-3 gap-4">
                  <Field label="Event Code" value={su.eventCode} />
                  <Field label="Display Name" value={su.eventDisplayName} />
                  <Field label="Timestamp" value={formatDateTime(su.eventTimestamp)} />
                  <Field label="Questions Sent" value={su.questionsSentTotal} />
                  <Field label="Questions Received" value={su.questionsReceivedTotal} />
                </CardContent>
              </Card>
            )
          })}
        </div>
      )}
    </div>
  )
}
