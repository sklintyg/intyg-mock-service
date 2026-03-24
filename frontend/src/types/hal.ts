export interface HalLink {
  href: string
  templated?: boolean
}

export interface HalLinks {
  [rel: string]: HalLink
}

export interface HalResource {
  _links: HalLinks
}

export interface HalCollection<T> extends HalResource {
  _embedded?: { [key: string]: T[] }
}

export interface HalPage<T> extends HalCollection<T> {
  page: {
    size: number
    totalElements: number
    totalPages: number
    number: number
  }
}
