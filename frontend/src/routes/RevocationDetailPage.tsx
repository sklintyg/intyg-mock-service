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
    <div className="space-y-1">
      <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">{label}</p>
      <p className={mono ? "font-mono text-xs" : "text-sm font-medium"}>{value ?? "—"}</p>
    </div>
  )
}

export function RevocationDetailPage() {
  const { certId } = useParams<{ certId: string }>()
  const url = certId ? `/api/navigate/certificates/${certId}/revocation` : null

  const { data: revocation, isLoading, isError } = useEntityDetail<RevocationResponse>(url)

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-64" />
        <Skeleton className="h-48 w-full" />
      </div>
    )
  }

  if (isError || !revocation) {
    return <p className="text-destructive">Revocation not found.</p>
  }

  const xmlHref = hrefOptional(revocation, "xml")

  return (
    <div className="space-y-10">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs text-muted-foreground uppercase tracking-[0.05em] mb-2">Revocation</p>
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-foreground">Revoked Certificate</h1>
            <Badge variant="destructive">Revoked</Badge>
          </div>
          <p className="font-mono text-sm text-muted-foreground mt-2">{revocation.certificateId}</p>
        </div>
        {xmlHref && (
          <a href={xmlHref} target="_blank" rel="noreferrer" className="text-xs text-primary hover:underline pt-2">
            View XML ↗
          </a>
        )}
      </div>

      <Card>
        <CardHeader className="pb-2">
          <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
            style={{ fontFamily: "var(--font-display)" }}
          >
            Revocation Details
          </CardTitle>
        </CardHeader>
        <CardContent className="text-sm space-y-4">
          <div className="space-y-1">
            <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">Certificate</p>
            {revocation.certificateId ? (
              <Link to={`/certificates/${revocation.certificateId}`} className="text-primary hover:underline font-mono text-xs">
                {revocation.certificateId}
              </Link>
            ) : (
              <p className="text-sm font-medium">—</p>
            )}
          </div>
          <div className="space-y-1">
            <p className="text-xs text-muted-foreground uppercase tracking-[0.05em]">Patient</p>
            {revocation.personId ? (
              <Link to={`/patients/${revocation.personId}`} className="text-primary hover:underline font-mono text-xs">
                {revocation.personId}
              </Link>
            ) : (
              <p className="text-sm font-medium">—</p>
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
