// Output format options
export type OutputFormat = 'bullets' | 'paragraphs' | 'numbered';

// Request to clean notes
export interface CleanRequest {
  content: string;
  outputFormat: OutputFormat;
}

// Response from clean endpoint
export interface CleanResponse {
  original: string;
  cleaned: string;
  outputFormat: OutputFormat;
  timestamp: string;
}

// Field-level validation error
export interface FieldError {
  field: string;
  message: string;
}

// Error response from API
export interface ErrorResponse {
  error: string;
  details: FieldError[] | null;
  timestamp: string;
}

// History item from database
export interface NoteHistoryItem {
  id: number;
  originalContent: string;
  cleanedContent: string;
  outputFormat: string;
  createdAt: string;
}