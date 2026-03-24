import { useParams, Link } from "react-router-dom"
import { useEntityDetail } from "@/hooks/useEntityDetail"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import { hrefOptional } from "@/lib/hal"
import type { LogEntryResponse } from "@/types/api"

function formatDateTime(ts: string | null): string {
  if (!ts) return "—"
  try {
    return new Date(ts).toLocaleString("sv-SE")
  } catch {
    return ts
  }
}

function Field({ label, value, mono }: { label: string; value: string | null | undefined; mono?: boolean }) {
  return (
    <div className="space-y-0.5">
      <p className="text-xs text-muted-foreground uppercase tracking-wide">{label}</p>
      <p className={mono ? "font-mono text-xs" : "text-sm"}>{value ?? "—"}</p>
    </div>
  )
}

export function LogEntryDetailPage() {
  const { logId } = useParams<{ logId: string }>()
  const url = logId ? `/api/navigate/log-entries/${logId}` : null

  const { data: entry, isLoading, isError } = useEntityDetail<LogEntryResponse>(url)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-40 w-full" />
      </div>
    )
  }

  if (isError || !entry) {
    return <p className="text-destructive">Log entry not found.</p>
  }

  const xmlHref = hrefOptional(entry, "xml")

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-muted-foreground mb-1">Log Entry</p>
          <h2 className="text-xl font-semibold">{entry.activityType ?? "Log Entry"}</h2>
          <p className="font-mono text-sm text-muted-foreground mt-1">{entry.logId}</p>
        </div>
        {xmlHref && (
          <a href={xmlHref} target="_blank" rel="noreferrer" className="text-xs text-primary hover:underline">
            View XML ↗
          </a>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
              Activity
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-3">
            <Field label="Log ID" value={entry.logId} mono />
            <Field label="Activity Type" value={entry.activityType} />
            <Field label="Purpose" value={entry.purpose} />
            <Field label="Activity Start" value={formatDateTime(entry.activityStart)} />
            <div className="space-y-0.5">
              <p className="text-xs text-muted-foreground uppercase tracking-wide">Certificate</p>
              {entry.certificateId ? (
                <Link to={`/certificates/${entry.certificateId}`} className="text-primary hover:underline font-mono text-xs">
                  {entry.certificateId}
                </Link>
              ) : (
                <p className="text-sm">—</p>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
              System &amp; User
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-3">
            <Field label="System" value={entry.systemName} />
            <Field label="System ID" value={entry.systemId} mono />
            <Field label="User" value={entry.userId} mono />
            <Field label="Assignment" value={entry.userAssignment} />
            <Field label="Care Unit ID" value={entry.careUnitId} mono />
            <Field label="Care Provider" value={entry.careProviderName} />
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
