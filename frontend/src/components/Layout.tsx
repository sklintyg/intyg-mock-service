import { Outlet, NavLink } from "react-router-dom"
import { Separator } from "@/components/ui/separator"

export function Layout() {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b bg-white sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center gap-6">
          <span className="font-semibold text-sm text-foreground">Intyg Mock Service</span>
          <Separator orientation="vertical" className="h-5" />
          <nav className="flex items-center gap-4">
            <NavLink
              to="/certificates"
              className={({ isActive }) =>
                `text-sm transition-colors hover:text-foreground ${
                  isActive ? "text-foreground font-medium" : "text-muted-foreground"
                }`
              }
            >
              Certificates
            </NavLink>
            <a
              href="/swagger-ui"
              target="_blank"
              rel="noreferrer"
              className="text-sm text-muted-foreground hover:text-foreground transition-colors"
            >
              Swagger UI ↗
            </a>
          </nav>
        </div>
      </header>
      <main className="flex-1 max-w-7xl mx-auto w-full px-4 py-6">
        <Outlet />
      </main>
    </div>
  )
}
