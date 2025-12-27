import type { CleanRequest, CleanResponse, ErrorResponse, NoteHistoryItem } from '../types';

const API_BASE_URL = 'http://localhost:8080/api/notes';

export class ApiError extends Error {
  statusCode: number;
  details: ErrorResponse | null;

  constructor(statusCode: number, details: ErrorResponse | null, message?: string) {
    super(message || details?.error || 'An error occurred');
    this.name = 'ApiError';
    this.statusCode = statusCode;
    this.details = details;
  }
}

export async function cleanNote(request: CleanRequest): Promise<CleanResponse> {
  const response = await fetch(`${API_BASE_URL}/clean`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    let errorData: ErrorResponse | null = null;
    try {
      errorData = await response.json();
    } catch {
      // Response wasn't JSON
    }
    throw new ApiError(response.status, errorData);
  }

  return response.json();
}

export async function getHistory(): Promise<NoteHistoryItem[]> {
  const response = await fetch(`${API_BASE_URL}/history`, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
    },
  });

  if (!response.ok) {
    throw new ApiError(response.status, null, `Failed to fetch history with status ${response.status}`);
  }

  return response.json();
}