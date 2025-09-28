export interface Model {
  _id?: string
  name: string
  description?: string
  apiBaseUrl?: string
  apiKey?: string
  modelName?: string
  maxTokens?: number
  temperature?: number

}

export interface ModelFormData {
  name: string
  description?: string
  apiBaseUrl?: string
  apiKey?: string
  modelName?: string
  maxTokens?: number
  temperature?: number
}

export interface ModelSearchParams {
  page?: number
  size?: number
  name?: string
}

export interface ModelSearchResponse {
  content: Model[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  numberOfElements: number
  first: boolean
  last: boolean
  empty: boolean
}
