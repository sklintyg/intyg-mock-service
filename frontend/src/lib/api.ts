import type { HalResource } from "@/types/hal"

const ROOT_URL = "/api/navigate"

export async function fetchRoot(): Promise<HalResource> {
  const res = await fetch(ROOT_URL, { headers: { Accept: "application/hal+json" } })
  if (!res.ok) throw new Error(`HTTP ${res.status} fetching root`)
  return res.json() as Promise<HalResource>
}

export async function fetchResource<T>(url: string): Promise<T> {
  const res = await fetch(url, { headers: { Accept: "application/hal+json" } })
  if (!res.ok) {
    const err = new Error(`HTTP ${res.status} fetching ${url}`)
    ;(err as Error & { status: number }).status = res.status
    throw err
  }
  return res.json() as Promise<T>
}
