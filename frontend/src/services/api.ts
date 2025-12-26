import type { CleanRequest, CleanResponse, ErrorResponse } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

export class ApiError extends Error {
  statusCode: number;
  details: ErrorResponse;

  constructor(statusCode: number, details: ErrorResponse) {
    super(details.error);
    this.name = 'ApiError';
    this.statusCode = statusCode;
    this.details = details;
  }
}

export async function cleanNote(request: CleanRequest): Promise<CleanResponse> {
  const response = await fetch(`${API_BASE_URL}/notes/clean`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData: ErrorResponse = await response.json();
    throw new ApiError(response.status, errorData);
  }

  return response.json();
}
