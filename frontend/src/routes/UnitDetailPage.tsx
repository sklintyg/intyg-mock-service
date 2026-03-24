import { useParams } from "react-router-dom"
import { useEntityDetail } from "@/hooks/useEntityDetail"
import { useQuery } from "@tanstack/react-query"
import { fetchResource } from "@/lib/api"
import { CertificateTable } from "@/components/CertificateTable"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import type { UnitResponse, CollectionModel, CertificateResponse } from "@/types/api"

export function UnitDetailPage() {
  const { unitId } = useParams<{ unitId: string }>()
  const url = unitId ? `/api/navigate/units/${unitId}` : null

  const { data: unit, links, isLoading, isError } = useEntityDetail<UnitResponse>(url)

  const certsQuery = useQuery<CollectionModel<CertificateResponse>>({
    queryKey: ["unit-certs", unitId],
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

  if (isError || !unit) {
    return <p className="text-destructive">Unit not found.</p>
  }

  return (
    <div className="space-y-10">
      <div>
        <p className="text-xs text-muted-foreground uppercase tracking-[0.05em] mb-2">Unit</p>
        <h1 className="text-3xl font-bold text-foreground">{unit.unitName}</h1>
        <p className="font-mono text-sm text-muted-foreground mt-1">{unit.unitId}</p>
        {unit.careProviderName && (
          <p className="text-sm text-muted-foreground mt-0.5">{unit.careProviderName}</p>
        )}
      </div>

      <Card className="max-w-sm">
        <CardHeader className="pb-2">
          <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
            style={{ fontFamily: "var(--font-display)" }}
          >
            Contact
          </CardTitle>
        </CardHeader>
        <CardContent className="text-sm space-y-0.5">
          {unit.streetAddress && <p>{unit.streetAddress}</p>}
          {(unit.postalCode || unit.city) && (
            <p>{[unit.postalCode, unit.city].filter(Boolean).join(" ")}</p>
          )}
          {unit.phone && <p>{unit.phone}</p>}
          {unit.email && <p>{unit.email}</p>}
          {!unit.streetAddress && !unit.phone && !unit.email && (
            <p className="text-muted-foreground">—</p>
          )}
        </CardContent>
      </Card>

      <div>
        <h2 className="text-lg font-semibold mb-6">Certificates</h2>
        <CertificateTable data={certsQuery.data} isLoading={certsQuery.isLoading} />
      </div>
    </div>
  )
}
