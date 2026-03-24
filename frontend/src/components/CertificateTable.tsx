import { Link } from "react-router-dom"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Skeleton } from "@/components/ui/skeleton"
import { embedded } from "@/lib/hal"
import type { CertificateResponse, CollectionModel, PagedModel } from "@/types/api"

interface Props {
  data: CollectionModel<CertificateResponse> | PagedModel<CertificateResponse> | undefined
  isLoading: boolean
}

function formatDateTime(ts: string | null): string {
  if (!ts) return "—"
  try {
    return new Date(ts).toLocaleString("sv-SE")
  } catch {
    return ts
  }
}

export function CertificateTable({ data, isLoading }: Props) {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array.from({ length: 5 }).map((_, i) => (
          <Skeleton key={i} className="h-12 w-full" />
        ))}
      </div>
    )
  }

  const items = data ? embedded<CertificateResponse>(data as CollectionModel<CertificateResponse>, "certificateResponseList") : []

  if (items.length === 0) {
    return (
      <p className="text-muted-foreground text-sm py-12 text-center">
        No certificates received yet.
      </p>
    )
  }

  return (
    <div className="rounded-xl overflow-hidden bg-[var(--surface-container)]">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Certificate ID</TableHead>
            <TableHead>Type</TableHead>
            <TableHead>Patient</TableHead>
            <TableHead>Signing Time</TableHead>
            <TableHead>Unit</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {items.map((cert) => (
            <TableRow key={cert.certificateId}>
              <TableCell>
                <Link
                  to={`/certificates/${cert.certificateId}`}
                  className="font-mono text-xs text-primary hover:underline font-medium"
                >
                  {cert.certificateId}
                </Link>
              </TableCell>
              <TableCell className="text-sm">{cert.certificateTypeDisplayName ?? cert.certificateType}</TableCell>
              <TableCell className="text-sm">
                {cert.patient ? (
                  <Link
                    to={`/patients/${cert.patient.personId}`}
                    className="hover:underline text-primary"
                    onClick={(e) => e.stopPropagation()}
                  >
                    {[cert.patient.firstName, cert.patient.lastName].filter(Boolean).join(" ") ||
                      cert.patient.personId}
                  </Link>
                ) : (
                  "—"
                )}
              </TableCell>
              <TableCell className="text-sm tabular-nums">
                {formatDateTime(cert.signingTimestamp)}
              </TableCell>
              <TableCell className="text-sm">
                {cert.issuedBy?.unit ? (
                  <Link
                    to={`/units/${cert.issuedBy.unit.unitId}`}
                    className="hover:underline text-primary"
                    onClick={(e) => e.stopPropagation()}
                  >
                    {cert.issuedBy.unit.unitName ?? cert.issuedBy.unit.unitId}
                  </Link>
                ) : (
                  "—"
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
