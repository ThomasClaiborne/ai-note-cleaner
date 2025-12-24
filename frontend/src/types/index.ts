export type OutputFormat = 'bullets' | 'paragraphs' | 'numbered';

export interface CleanRequest {
  content: string;
  outputFormat: OutputFormat;
}

export interface CleanResponse {
  original: string;
  cleaned: string;
  outputFormat: OutputFormat;
  timestamp: string;
}

export interface FieldError {
  field: string;
  message: string;
}

export interface ErrorResponse {
  error: string;
  details: FieldError[] | null;
  timestamp: string;
}
