import type { FieldError } from '../types';

interface ErrorMessageProps {
  message: string;
  details?: FieldError[];
  onDismiss?: () => void;
}

export default function ErrorMessage({ message, details, onDismiss }: ErrorMessageProps) {
  return (
    <div className="bg-red-50 border border-red-500 text-red-700 p-4 rounded-lg" role="alert">
      <div className="flex justify-between items-start">
        <div className="flex-1">
          <p className="font-medium">{message}</p>
          {details && details.length > 0 && (
            <ul className="mt-2 list-disc list-inside text-sm">
              {details.map((error, index) => (
                <li key={index}>
                  <span className="font-medium">{error.field}:</span> {error.message}
                </li>
              ))}
            </ul>
          )}
        </div>
        {onDismiss && (
          <button
            onClick={onDismiss}
            className="ml-4 text-red-500 hover:text-red-700 font-bold text-xl leading-none"
            aria-label="Dismiss error"
          >
            ×
          </button>
        )}
      </div>
    </div>
  );
}
