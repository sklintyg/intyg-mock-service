import { useParams, Link } from "react-router-dom"
import { useEntityDetail } from "@/hooks/useEntityDetail"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Skeleton } from "@/components/ui/skeleton"
import { hrefOptional } from "@/lib/hal"
import type { RevocationResponse } from "@/types/api"

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

export function RevocationDetailPage() {
  const { certId } = useParams<{ certId: string }>()
  const url = certId ? `/api/navigate/certificates/${certId}/revocation` : null

  const { data: revocation, isLoading, isError } = useEntityDetail<RevocationResponse>(url)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-40 w-full" />
      </div>
    )
  }

  if (isError || !revocation) {
    return <p className="text-destructive">Revocation not found.</p>
  }

  const xmlHref = hrefOptional(revocation, "xml")

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-muted-foreground mb-1">Revocation</p>
          <div className="flex items-center gap-2">
            <h2 className="text-xl font-semibold">Revoked Certificate</h2>
            <Badge variant="destructive">Revoked</Badge>
          </div>
          <p className="font-mono text-sm text-muted-foreground mt-1">{revocation.certificateId}</p>
        </div>
        {xmlHref && (
          <a href={xmlHref} target="_blank" rel="noreferrer" className="text-xs text-primary hover:underline">
            View XML ↗
          </a>
        )}
      </div>

      <Card>
        <CardHeader className="pb-2">
          <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
            Revocation Details
          </CardTitle>
        </CardHeader>
        <CardContent className="text-sm space-y-3">
          <div className="space-y-0.5">
            <p className="text-xs text-muted-foreground uppercase tracking-wide">Certificate</p>
            {revocation.certificateId ? (
              <Link to={`/certificates/${revocation.certificateId}`} className="text-primary hover:underline font-mono text-xs">
                {revocation.certificateId}
              </Link>
            ) : (
              <p className="text-sm">—</p>
            )}
          </div>
          <div className="space-y-0.5">
            <p className="text-xs text-muted-foreground uppercase tracking-wide">Patient</p>
            {revocation.personId ? (
              <Link to={`/patients/${revocation.personId}`} className="text-primary hover:underline font-mono text-xs">
                {revocation.personId}
              </Link>
            ) : (
              <p className="text-sm">—</p>
            )}
          </div>
          <Field label="Revoked at" value={formatDateTime(revocation.revokedAt)} />
          <Field label="Reason" value={revocation.reason} />
          <Field label="Revoked by" value={revocation.revokedByFullName} />
          <Field label="Revoked by ID" value={revocation.revokedByStaffId} mono />
        </CardContent>
      </Card>
    </div>
  )
}
