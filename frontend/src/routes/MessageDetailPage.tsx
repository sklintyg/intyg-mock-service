import { useParams, Link } from "react-router-dom"
import { useEntityDetail } from "@/hooks/useEntityDetail"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import { hrefOptional } from "@/lib/hal"
import type { MessageResponse } from "@/types/api"

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

export function MessageDetailPage() {
  const { messageId } = useParams<{ messageId: string }>()
  const url = messageId ? `/api/navigate/messages/${messageId}` : null

  const { data: message, isLoading, isError } = useEntityDetail<MessageResponse>(url)

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-64" />
        <Skeleton className="h-48 w-full" />
      </div>
    )
  }

  if (isError || !message) {
    return <p className="text-destructive">Message not found.</p>
  }

  const xmlHref = hrefOptional(message, "xml")

  return (
    <div className="space-y-10">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs text-muted-foreground uppercase tracking-[0.05em] mb-2">Message</p>
          <h1 className="text-3xl font-bold text-foreground">{message.subject ?? message.heading ?? "Message"}</h1>
          <p className="font-mono text-sm text-muted-foreground mt-1">{message.messageId}</p>
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
              Message Details
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-4">
            <Field label="Message ID" value={message.messageId} mono />
            <Field label="Subject" value={message.subject} />
            <Field label="Heading" value={message.heading} />
            <Field label="Recipient" value={message.recipient} />
            <Field label="Sent" value={formatDateTime(message.sentTimestamp)} />
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
              style={{ fontFamily: "var(--font-display)" }}
            >
              Sender &amp; References
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-4">
            <div className="space-y-1">
              <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">Certificate</p>
              {message.certificateId ? (
                <Link to={`/certificates/${message.certificateId}`} className="text-primary hover:underline font-mono text-xs">
                  {message.certificateId}
                </Link>
              ) : (
                <p className="text-sm font-medium">—</p>
              )}
            </div>
            <div className="space-y-1">
              <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">Patient</p>
              {message.personId ? (
                <Link to={`/patients/${message.personId}`} className="text-primary hover:underline font-mono text-xs">
                  {message.personId}
                </Link>
              ) : (
                <p className="text-sm font-medium">—</p>
              )}
            </div>
            <Field label="Sent by" value={message.sentByFullName} />
            <Field label="Sent by ID" value={message.sentByStaffId} mono />
          </CardContent>
        </Card>
      </div>

      {message.body && (
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
              style={{ fontFamily: "var(--font-display)" }}
            >
              Body
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm whitespace-pre-wrap leading-relaxed">{message.body}</p>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
