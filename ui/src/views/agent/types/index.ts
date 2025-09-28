export interface Agent {
  _id?: string
  name?: string
  description?: string
  agentTemplate?: string
  config?: AgentConfig
  members?: string[]
  tags?: string[]
}

export interface AgentConfig {

}

export interface AgentTemplate {
  _id?: string
  key: string
  name: string
  description?: string
}

export interface AgentFormData {
  name: string
  description?: string
  agentTemplate?: string
  config?: AgentConfig
  members?: string[]
}

export interface AgentSearchParams {
  page?: number
  size?: number
  name?: string
}

export interface AgentSearchResponse {
  content: Agent[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  numberOfElements: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface AgentTemplateSearchResponse {
  list: AgentTemplate[]
  total: number
  page: number
  size: number
}
