import { useLocation, useParams } from "react-router-dom"

export interface Crumb {
  label: string
  to?: string
}

export function useBreadcrumbs(): Crumb[] {
  const location = useLocation()
  const { pathname } = location
  const params = useParams()

  // /certificates
  if (pathname === "/certificates") {
    return [{ label: "Certificates" }]
  }

  // /certificates/:certId/status-updates
  if (params.certId && pathname.endsWith("/status-updates")) {
    return [
      { label: "Certificates", to: "/certificates" },
      { label: params.certId, to: `/certificates/${params.certId}` },
      { label: "Status Updates" },
    ]
  }

  // /certificates/:certId/revocation
  if (params.certId && pathname.endsWith("/revocation")) {
    return [
      { label: "Certificates", to: "/certificates" },
      { label: params.certId, to: `/certificates/${params.certId}` },
      { label: "Revocation" },
    ]
  }

  // /certificates/:id
  if (params.id) {
    return [
      { label: "Certificates", to: "/certificates" },
      { label: params.id },
    ]
  }

  // /patients/:personId
  if (params.personId) {
    return [
      { label: "Certificates", to: "/certificates" },
      { label: params.personId },
    ]
  }

  // /units/:unitId
  if (params.unitId) {
    return [
      { label: "Certificates", to: "/certificates" },
      { label: params.unitId },
    ]
  }

  // /staff/:staffId
  if (params.staffId) {
    return [
      { label: "Certificates", to: "/certificates" },
      { label: params.staffId },
    ]
  }

  // /messages/:messageId
  if (params.messageId) {
    const certId = (location.state as { certId?: string } | null)?.certId
    return [
      { label: "Certificates", to: "/certificates" },
      ...(certId ? [{ label: certId, to: `/certificates/${certId}` }] : []),
      { label: params.messageId },
    ]
  }

  // /log-entries/:logId
  if (params.logId) {
    const certId = (location.state as { certId?: string } | null)?.certId
    return [
      { label: "Certificates", to: "/certificates" },
      ...(certId ? [{ label: certId, to: `/certificates/${certId}` }] : []),
      { label: params.logId },
    ]
  }

  return [{ label: "Certificates", to: "/certificates" }]
}
