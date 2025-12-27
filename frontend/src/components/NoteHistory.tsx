import { useState, useEffect } from 'react';
import { getHistory, ApiError } from '../services/api';
import type { NoteHistoryItem } from '../types';
import HistoryItem from './HistoryItem';
import LoadingSpinner from './LoadingSpinner';

interface NoteHistoryProps {
  refreshTrigger: number;
}

/**
 * Displays the list of past note transformations.
 */
export default function NoteHistory({ refreshTrigger }: NoteHistoryProps) {
  const [history, setHistory] = useState<NoteHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchHistory = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const data = await getHistory();
        setHistory(data);
      } catch (err) {
        if (err instanceof ApiError) {
          setError(err.message);
        } else {
          setError('Failed to load history');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchHistory();
  }, [refreshTrigger]);

  if (isLoading) {
    return (
      <div className="mt-8">
        <h2 className="text-lg font-semibold text-gray-700 mb-4">📝 Note History</h2>
        <div className="flex justify-center py-8">
          <LoadingSpinner />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mt-8">
        <h2 className="text-lg font-semibold text-gray-700 mb-4">📝 Note History</h2>
        <p className="text-red-600 text-sm">{error}</p>
      </div>
    );
  }

  if (history.length === 0) {
    return (
      <div className="mt-8">
        <h2 className="text-lg font-semibold text-gray-700 mb-4">📝 Note History</h2>
        <p className="text-gray-500 text-sm">No notes cleaned yet. Try cleaning some notes above!</p>
      </div>
    );
  }

  return (
    <div className="mt-8">
      <h2 className="text-lg font-semibold text-gray-700 mb-4">
        📝 Note History ({history.length})
      </h2>
      <div className="space-y-2">
        {history.map((item) => (
          <HistoryItem key={item.id} item={item} />
        ))}
      </div>
    </div>
  );
}