import type { ReactNode } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"

type RibbonColor = "primary" | "secondary" | "tertiary" | "destructive"

const ribbonColors: Record<RibbonColor, string> = {
  primary: "var(--primary)",
  secondary: "var(--secondary)",
  tertiary: "var(--tertiary)",
  destructive: "var(--destructive)",
}

interface Props {
  title: string
  isLoading: boolean
  isError: boolean
  isEmpty?: boolean
  emptyMessage?: string
  ribbonColor?: RibbonColor
  children: ReactNode
}

export function RelatedResourceCard({
  title,
  isLoading,
  isError,
  isEmpty,
  emptyMessage = "None",
  ribbonColor = "tertiary",
  children,
}: Props) {
  return (
    <Card
      className="pl-0 overflow-hidden"
      style={{ borderLeft: `4px solid ${ribbonColors[ribbonColor]}` }}
    >
      <CardHeader className="pb-2">
        <CardTitle className="text-xs font-semibold text-muted-foreground uppercase tracking-[0.05em]"
          style={{ fontFamily: "var(--font-display)" }}
        >
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
