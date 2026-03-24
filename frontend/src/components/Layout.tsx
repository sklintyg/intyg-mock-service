import { Outlet, NavLink } from "react-router-dom"
import { PageToolbar } from "@/components/PageToolbar"

export function Layout() {
  return (
    <div className="min-h-screen flex flex-col bg-background">
      <header
        className="bg-[var(--surface-container-low)] sticky top-0 z-10"
        style={{ boxShadow: "var(--shadow-ambient)" }}
      >
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center gap-8">
          <span
            className="text-base font-bold text-foreground"
            style={{ fontFamily: "var(--font-display)" }}
          >
            Intyg Mock Service
          </span>
          <nav className="flex items-center gap-6">
            <NavLink
              to="/certificates"
              className={({ isActive }) =>
                `text-sm transition-colors hover:text-foreground ${
                  isActive ? "text-foreground font-semibold" : "text-muted-foreground"
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
      <main className="flex-1 max-w-7xl mx-auto w-full px-6 py-10">
        <PageToolbar />
        <Outlet />
      </main>
    </div>
  )
}
