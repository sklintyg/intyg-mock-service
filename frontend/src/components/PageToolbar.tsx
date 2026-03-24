import { useNavigate, useLocation } from "react-router-dom"
import { useQueryClient } from "@tanstack/react-query"
import { ArrowLeft, RotateCw } from "lucide-react"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"
import { useBreadcrumbs } from "@/hooks/useBreadcrumbs"

export function PageToolbar() {
  const navigate = useNavigate()
  const location = useLocation()
  const queryClient = useQueryClient()
  const crumbs = useBreadcrumbs()

  const isRoot = location.pathname === "/certificates" || location.pathname === "/"
  const showBack = !isRoot

  function handleReload() {
    queryClient.invalidateQueries()
  }

  return (
    <div className="flex items-center justify-between mb-4">
      <Breadcrumb>
        <BreadcrumbList>
          {crumbs.map((crumb, i) => {
            const isLast = i === crumbs.length - 1
            return (
              <BreadcrumbItem key={i}>
                {isLast ? (
                  <BreadcrumbPage className="font-mono text-xs">{crumb.label}</BreadcrumbPage>
                ) : (
                  <BreadcrumbLink asChild>
                    <Link to={crumb.to!}>{crumb.label}</Link>
                  </BreadcrumbLink>
                )}
                {!isLast && <BreadcrumbSeparator />}
              </BreadcrumbItem>
            )
          })}
        </BreadcrumbList>
      </Breadcrumb>

      <div className="flex items-center gap-1">
        {showBack && (
          <Button
            variant="ghost"
            size="icon-sm"
            onClick={() => navigate(-1)}
            title="Go back"
          >
            <ArrowLeft />
          </Button>
        )}
        <Button
          variant="ghost"
          size="icon-sm"
          onClick={handleReload}
          title="Reload"
        >
          <RotateCw />
        </Button>
      </div>
    </div>
  )
}
