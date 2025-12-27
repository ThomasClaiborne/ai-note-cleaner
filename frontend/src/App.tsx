import { useState } from 'react';
import type { OutputFormat, CleanResponse, FieldError } from './types';
import { cleanNote, ApiError } from './services/api';
import LoadingSpinner from './components/LoadingSpinner';
import ErrorMessage from './components/ErrorMessage';
import FormatSelector from './components/FormatSelector';
import NoteInput from './components/NoteInput';
import NoteOutput from './components/NoteOutput';
import NoteHistory from './components/NoteHistory';

interface AppError {
  message: string;
  details?: FieldError[];
}

function App() {
  const [content, setContent] = useState('');
  const [outputFormat, setOutputFormat] = useState<OutputFormat>('bullets');
  const [result, setResult] = useState<CleanResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<AppError | null>(null);
  const [historyRefreshTrigger, setHistoryRefreshTrigger] = useState(0);

  const handleSubmit = async () => {
    setIsLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await cleanNote({ content, outputFormat });
      setResult(response);
      // Trigger history refresh after successful clean
      setHistoryRefreshTrigger(prev => prev + 1);
    } catch (err) {
      if (err instanceof ApiError) {
        setError({
          message: err.message,
          details: err.details?.details ?? undefined,
        });
      } else {
        setError({ message: 'An unexpected error occurred' });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const isSubmitDisabled = content.trim() === '' || isLoading;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-3xl mx-auto p-6 space-y-6">
        <header className="text-center">
          <h1 className="text-3xl font-bold text-gray-900">AI Note Cleaner</h1>
          <p className="mt-2 text-gray-600">
            Transform your messy notes into clean, organized text
          </p>
        </header>

        <NoteInput
          value={content}
          onChange={setContent}
          disabled={isLoading}
        />

        <FormatSelector
          value={outputFormat}
          onChange={setOutputFormat}
          disabled={isLoading}
        />

        <button
          onClick={handleSubmit}
          disabled={isSubmitDisabled}
          className="w-full bg-blue-600 hover:bg-blue-700 text-white px-4 py-3 rounded-lg font-medium disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
        >
          {isLoading ? 'Cleaning...' : 'Clean My Notes'}
        </button>

        {isLoading && <LoadingSpinner />}

        {error && (
          <ErrorMessage
            message={error.message}
            details={error.details}
            onDismiss={() => setError(null)}
          />
        )}

        {result && (
          <NoteOutput
            content={result.cleaned}
            format={result.outputFormat}
          />
        )}

        <NoteHistory refreshTrigger={historyRefreshTrigger} />
      </div>
    </div>
  );
}

export default App;