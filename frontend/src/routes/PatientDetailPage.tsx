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
      <div className="space-y-6">
        <Skeleton className="h-10 w-48" />
        <Skeleton className="h-32 w-full" />
      </div>
    )
  }

  if (isError || !patient) {
    return <p className="text-destructive">Patient not found.</p>
  }

  return (
    <div className="space-y-10">
      <div>
        <p className="text-xs text-muted-foreground uppercase tracking-[0.05em] mb-2">Patient</p>
        <h1 className="text-3xl font-bold text-foreground">
          {[patient.firstName, patient.lastName].filter(Boolean).join(" ") || patient.personId}
        </h1>
        <p className="font-mono text-sm text-muted-foreground mt-1">{patient.personId}</p>
      </div>

      <Card className="max-w-sm">
        <CardHeader className="pb-2">
          <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
            style={{ fontFamily: "var(--font-display)" }}
          >
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
        <h2 className="text-lg font-semibold mb-6">Certificates</h2>
        <CertificateTable data={certsQuery.data} isLoading={certsQuery.isLoading} />
      </div>
    </div>
  )
}
