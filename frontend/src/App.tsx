import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { Layout } from "@/components/Layout"
import { CertificateListPage } from "@/routes/CertificateListPage"
import { CertificateDetailPage } from "@/routes/CertificateDetailPage"
import { PatientDetailPage } from "@/routes/PatientDetailPage"
import { UnitDetailPage } from "@/routes/UnitDetailPage"
import { StaffDetailPage } from "@/routes/StaffDetailPage"
import { MessageDetailPage } from "@/routes/MessageDetailPage"
import { LogEntryDetailPage } from "@/routes/LogEntryDetailPage"
import { RevocationDetailPage } from "@/routes/RevocationDetailPage"
import { StatusUpdatesDetailPage } from "@/routes/StatusUpdatesDetailPage"
import { NotFoundPage } from "@/routes/NotFoundPage"
import { RootLinksProvider } from "@/context/RootLinksContext"

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 1,
    },
  },
})

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RootLinksProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<Navigate to="/certificates" replace />} />
              <Route path="certificates" element={<CertificateListPage />} />
              <Route path="certificates/:id" element={<CertificateDetailPage />} />
              <Route path="patients/:personId" element={<PatientDetailPage />} />
              <Route path="units/:unitId" element={<UnitDetailPage />} />
              <Route path="staff/:staffId" element={<StaffDetailPage />} />
              <Route path="messages/:messageId" element={<MessageDetailPage />} />
              <Route path="log-entries/:logId" element={<LogEntryDetailPage />} />
              <Route path="certificates/:certId/status-updates" element={<StatusUpdatesDetailPage />} />
              <Route path="certificates/:certId/revocation" element={<RevocationDetailPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </RootLinksProvider>
    </QueryClientProvider>
  )
}

export default App
