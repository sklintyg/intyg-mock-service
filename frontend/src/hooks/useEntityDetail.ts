import { useQuery } from "@tanstack/react-query"
import { fetchResource } from "@/lib/api"
import { links as getLinks } from "@/lib/hal"
import type { HalLinks, HalResource } from "@/types/hal"

export function useEntityDetail<T extends HalResource>(url: string | null | undefined) {
  const query = useQuery<T>({
    queryKey: ["entity", url],
    queryFn: () => fetchResource<T>(url!),
    enabled: !!url,
  })

  const links: HalLinks | null = query.data ? getLinks(query.data) : null

  return {
    data: query.data ?? null,
    links,
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
  }
}
