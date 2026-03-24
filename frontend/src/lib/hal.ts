import type { HalCollection, HalLinks, HalResource } from "@/types/hal"

export function links(resource: HalResource): HalLinks {
  return resource._links
}

export function href(resource: HalResource, rel: string): string {
  const link = resource._links?.[rel]
  if (!link) throw new Error(`Missing HAL link rel: "${rel}"`)
  return link.href
}

export function hrefOptional(resource: HalResource, rel: string): string | undefined {
  return resource._links?.[rel]?.href
}

export function embedded<T>(collection: HalCollection<T>, key: string): T[] {
  return collection._embedded?.[key] ?? []
}

export async function followLink<T>(resource: HalResource, rel: string): Promise<T> {
  const url = href(resource, rel)
  const res = await fetch(url, { headers: { Accept: "application/hal+json" } })
  if (!res.ok) throw new Error(`HTTP ${res.status} fetching ${url}`)
  return res.json() as Promise<T>
}
