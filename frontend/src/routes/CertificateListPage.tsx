import { useRootLinks } from "@/context/RootLinksContext"
import { usePaginatedCollection } from "@/hooks/usePaginatedCollection"
import { CertificateTable } from "@/components/CertificateTable"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import type { CertificateResponse } from "@/types/api"

export function CertificateListPage() {
  const { links } = useRootLinks()
  const baseUrl = links?.certificates?.href

  const { data, isLoading, isError, page, totalPages, hasNext, hasPrev, goToPage } =
    usePaginatedCollection<CertificateResponse>({ baseUrl })

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Certificates</h2>
        {data?.page && (
          <span className="text-sm text-muted-foreground">
            {data.page.totalElements} total
          </span>
        )}
      </div>

      {isError && (
        <p className="text-destructive text-sm">Failed to load certificates.</p>
      )}

      <CertificateTable data={data} isLoading={isLoading} />

      {totalPages > 1 && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={() => hasPrev && goToPage(page - 1)}
                aria-disabled={!hasPrev}
                className={!hasPrev ? "pointer-events-none opacity-50" : "cursor-pointer"}
              />
            </PaginationItem>
            {Array.from({ length: Math.min(totalPages, 7) }).map((_, i) => {
              const p = totalPages <= 7 ? i : getPageNumber(i, page, totalPages)
              return (
                <PaginationItem key={p}>
                  <PaginationLink
                    isActive={p === page}
                    onClick={() => goToPage(p)}
                    className="cursor-pointer"
                  >
                    {p + 1}
                  </PaginationLink>
                </PaginationItem>
              )
            })}
            <PaginationItem>
              <PaginationNext
                onClick={() => hasNext && goToPage(page + 1)}
                aria-disabled={!hasNext}
                className={!hasNext ? "pointer-events-none opacity-50" : "cursor-pointer"}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      )}
    </div>
  )
}

function getPageNumber(index: number, current: number, total: number): number {
  if (total <= 7) return index
  if (current < 4) return index
  if (current > total - 5) return total - 7 + index
  return current - 3 + index
}
