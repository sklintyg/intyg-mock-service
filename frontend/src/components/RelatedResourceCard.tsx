import type { ReactNode } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"

interface Props {
  title: string
  isLoading: boolean
  isError: boolean
  isEmpty?: boolean
  emptyMessage?: string
  children: ReactNode
}

export function RelatedResourceCard({
  title,
  isLoading,
  isError,
  isEmpty,
  emptyMessage = "None",
  children,
}: Props) {
  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wide">
          {title}
        </CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-2">
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-3/4" />
          </div>
        ) : isError ? (
          <p className="text-destructive text-sm">Failed to load.</p>
        ) : isEmpty ? (
          <p className="text-muted-foreground text-sm">{emptyMessage}</p>
        ) : (
          children
        )}
      </CardContent>
    </Card>
  )
}
