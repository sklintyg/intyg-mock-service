import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import { fetchRoot } from "@/lib/api"
import type { HalLinks } from "@/types/hal"

interface RootLinksContextValue {
  links: HalLinks | null
  loading: boolean
  error: Error | null
}

const RootLinksContext = createContext<RootLinksContextValue>({
  links: null,
  loading: true,
  error: null,
})

export function RootLinksProvider({ children }: { children: ReactNode }) {
  const [links, setLinks] = useState<HalLinks | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    fetchRoot()
      .then((root) => setLinks(root._links))
      .catch((err: unknown) => setError(err instanceof Error ? err : new Error(String(err))))
      .finally(() => setLoading(false))
  }, [])

  return (
    <RootLinksContext.Provider value={{ links, loading, error }}>
      {children}
    </RootLinksContext.Provider>
  )
}

export function useRootLinks() {
  return useContext(RootLinksContext)
}
