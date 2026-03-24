import { useParams } from "react-router-dom"
import { useEntityDetail } from "@/hooks/useEntityDetail"
import { useQuery } from "@tanstack/react-query"
import { fetchResource } from "@/lib/api"
import { CertificateTable } from "@/components/CertificateTable"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import type { PatientResponse, CollectionModel, CertificateResponse } from "@/types/api"

export function PatientDetailPage() {
  const { personId } = useParams<{ personId: string }>()
  const url = personId ? `/api/navigate/patients/${personId}` : null

  const { data: patient, links, isLoading, isError } = useEntityDetail<PatientResponse>(url)

  const certsQuery = useQuery<CollectionModel<CertificateResponse>>({
    queryKey: ["patient-certs", personId],
    queryFn: () => fetchResource<CollectionModel<CertificateResponse>>(links!["certificates"].href),
    enabled: !!links?.["certificates"],
  })

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-32 w-full" />
      </div>
    )
  }

  if (isError || !patient) {
    return <p className="text-destructive">Patient not found.</p>
  }

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm text-muted-foreground mb-1">Patient</p>
        <h2 className="text-xl font-semibold">
          {[patient.firstName, patient.lastName].filter(Boolean).join(" ") || patient.personId}
        </h2>
        <p className="font-mono text-sm text-muted-foreground">{patient.personId}</p>
      </div>

      <Card className="max-w-sm">
        <CardHeader className="pb-2">
          <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
            Address
          </CardTitle>
        </CardHeader>
        <CardContent className="text-sm space-y-0.5">
          {patient.streetAddress ? (
            <>
              <p>{patient.streetAddress}</p>
              <p>
                {[patient.postalCode, patient.city].filter(Boolean).join(" ")}
              </p>
            </>
          ) : (
            <p className="text-muted-foreground">—</p>
          )}
        </CardContent>
      </Card>

      <div>
        <h3 className="text-base font-medium mb-3">Certificates</h3>
        <CertificateTable data={certsQuery.data} isLoading={certsQuery.isLoading} />
      </div>
    </div>
  )
}
