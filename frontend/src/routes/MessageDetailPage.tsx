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
    <div className="space-y-0.5">
      <p className="text-xs text-muted-foreground uppercase tracking-wide">{label}</p>
      <p className={mono ? "font-mono text-xs" : "text-sm"}>{value ?? "—"}</p>
    </div>
  )
}

export function MessageDetailPage() {
  const { messageId } = useParams<{ messageId: string }>()
  const url = messageId ? `/api/navigate/messages/${messageId}` : null

  const { data: message, isLoading, isError } = useEntityDetail<MessageResponse>(url)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-40 w-full" />
      </div>
    )
  }

  if (isError || !message) {
    return <p className="text-destructive">Message not found.</p>
  }

  const xmlHref = hrefOptional(message, "xml")

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-muted-foreground mb-1">Message</p>
          <h2 className="text-xl font-semibold">{message.subject ?? message.heading ?? "Message"}</h2>
          <p className="font-mono text-sm text-muted-foreground mt-1">{message.messageId}</p>
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
              Message Details
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-3">
            <Field label="Message ID" value={message.messageId} mono />
            <Field label="Subject" value={message.subject} />
            <Field label="Heading" value={message.heading} />
            <Field label="Recipient" value={message.recipient} />
            <Field label="Sent" value={formatDateTime(message.sentTimestamp)} />
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
              Sender &amp; References
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-3">
            <div className="space-y-0.5">
              <p className="text-xs text-muted-foreground uppercase tracking-wide">Certificate</p>
              {message.certificateId ? (
                <Link to={`/certificates/${message.certificateId}`} className="text-primary hover:underline font-mono text-xs">
                  {message.certificateId}
                </Link>
              ) : (
                <p className="text-sm">—</p>
              )}
            </div>
            <div className="space-y-0.5">
              <p className="text-xs text-muted-foreground uppercase tracking-wide">Patient</p>
              {message.personId ? (
                <Link to={`/patients/${message.personId}`} className="text-primary hover:underline font-mono text-xs">
                  {message.personId}
                </Link>
              ) : (
                <p className="text-sm">—</p>
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
            <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
              Body
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm whitespace-pre-wrap">{message.body}</p>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
