import { useParams } from "react-router-dom"
import { useEntityDetail } from "@/hooks/useEntityDetail"
import { useQuery } from "@tanstack/react-query"
import { fetchResource } from "@/lib/api"
import { CertificateTable } from "@/components/CertificateTable"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import type { StaffResponse, CollectionModel, CertificateResponse } from "@/types/api"

export function StaffDetailPage() {
  const { staffId } = useParams<{ staffId: string }>()
  const url = staffId ? `/api/navigate/staff/${staffId}` : null

  const { data: staff, links, isLoading, isError } = useEntityDetail<StaffResponse>(url)

  const certsQuery = useQuery<CollectionModel<CertificateResponse>>({
    queryKey: ["staff-certs", staffId],
    queryFn: () => fetchResource<CollectionModel<CertificateResponse>>(links!["certificates"].href),
    enabled: !!links?.["certificates"],
  })

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-48" />
        <Skeleton className="h-32 w-full" />
      </div>
    )
  }

  if (isError || !staff) {
    return <p className="text-destructive">Staff member not found.</p>
  }

  return (
    <div className="space-y-10">
      <div>
        <p className="text-xs text-muted-foreground uppercase tracking-[0.05em] mb-2">Staff</p>
        <h1 className="text-3xl font-bold text-foreground">{staff.fullName ?? staff.staffId}</h1>
        <p className="font-mono text-sm text-muted-foreground mt-1">{staff.staffId}</p>
      </div>

      {staff.prescriptionCode && (
        <Card className="max-w-sm">
          <CardHeader className="pb-2">
            <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
              style={{ fontFamily: "var(--font-display)" }}
            >
              Details
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm">
            <p>
              <span className="text-muted-foreground">Prescription code: </span>
              {staff.prescriptionCode}
            </p>
          </CardContent>
        </Card>
      )}

      <div>
        <h2 className="text-lg font-semibold mb-6">Certificates</h2>
        <CertificateTable data={certsQuery.data} isLoading={certsQuery.isLoading} />
      </div>
    </div>
  )
}
