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
    <div className="space-y-1">
      <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">{label}</p>
      <p className={mono ? "font-mono text-xs" : "text-sm font-medium"}>{value ?? "—"}</p>
    </div>
  )
}

export function LogEntryDetailPage() {
  const { logId } = useParams<{ logId: string }>()
  const url = logId ? `/api/navigate/log-entries/${logId}` : null

  const { data: entry, isLoading, isError } = useEntityDetail<LogEntryResponse>(url)

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-64" />
        <Skeleton className="h-48 w-full" />
      </div>
    )
  }

  if (isError || !entry) {
    return <p className="text-destructive">Log entry not found.</p>
  }

  const xmlHref = hrefOptional(entry, "xml")

  return (
    <div className="space-y-10">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs text-muted-foreground uppercase tracking-[0.05em] mb-2">Log Entry</p>
          <h1 className="text-3xl font-bold text-foreground">{entry.activityType ?? "Log Entry"}</h1>
          <p className="font-mono text-sm text-muted-foreground mt-1">{entry.logId}</p>
        </div>
        {xmlHref && (
          <a href={xmlHref} target="_blank" rel="noreferrer" className="text-xs text-primary hover:underline pt-2">
            View XML ↗
          </a>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 bg-[var(--surface-container)] rounded-2xl p-6">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
              style={{ fontFamily: "var(--font-display)" }}
            >
              Activity
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-4">
            <Field label="Log ID" value={entry.logId} mono />
            <Field label="Activity Type" value={entry.activityType} />
            <Field label="Purpose" value={entry.purpose} />
            <Field label="Activity Start" value={formatDateTime(entry.activityStart)} />
            <div className="space-y-1">
              <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">Certificate</p>
              {entry.certificateId ? (
                <Link to={`/certificates/${entry.certificateId}`} className="text-primary hover:underline font-mono text-xs">
                  {entry.certificateId}
                </Link>
              ) : (
                <p className="text-sm font-medium">—</p>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
              style={{ fontFamily: "var(--font-display)" }}
            >
              System &amp; User
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-4">
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
