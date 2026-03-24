import { useState, useCallback } from "react"
import { useQuery } from "@tanstack/react-query"
import { fetchResource } from "@/lib/api"
import { hrefOptional } from "@/lib/hal"
import type { PagedModel } from "@/types/api"

interface UsePaginatedCollectionOptions {
  baseUrl: string | null | undefined
  pageSize?: number
}

export function usePaginatedCollection<T>(options: UsePaginatedCollectionOptions) {
  const { baseUrl, pageSize = 20 } = options
  const [page, setPage] = useState(0)

  const cleanBase = baseUrl ? baseUrl.split("?")[0] : null
  const url = cleanBase ? `${cleanBase}?page=${page}&size=${pageSize}` : null

  const query = useQuery<PagedModel<T>>({
    queryKey: ["collection", baseUrl, page, pageSize],
    queryFn: () => fetchResource<PagedModel<T>>(url!),
    enabled: !!url,
    placeholderData: (prev) => prev,
  })

  const totalPages = query.data?.page?.totalPages ?? 0
  const hasNext = page + 1 < totalPages
  const hasPrev = page > 0

  const goToPage = useCallback(
    (p: number) => {
      if (p >= 0 && (totalPages === 0 || p < totalPages)) setPage(p)
    },
    [totalPages],
  )

  // Also support following HAL next/prev links directly
  const nextUrl = query.data ? hrefOptional(query.data, "next") : undefined
  const prevUrl = query.data ? hrefOptional(query.data, "prev") : undefined

  return {
    data: query.data,
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
    page,
    totalPages,
    hasNext,
    hasPrev,
    nextUrl,
    prevUrl,
    goToPage,
    nextPage: () => hasNext && goToPage(page + 1),
    prevPage: () => hasPrev && goToPage(page - 1),
  }
}
