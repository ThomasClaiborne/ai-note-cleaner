import { useState } from 'react';
import type { NoteHistoryItem } from '../types';

interface HistoryItemProps {
  item: NoteHistoryItem;
}

/**
 * Single history item with expand/collapse functionality.
 */
export default function HistoryItem({ item }: HistoryItemProps) {
  const [isExpanded, setIsExpanded] = useState(false);
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(item.cleanedContent);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const formatLabel = (format: string): string => {
    const labels: Record<string, string> = {
      bullets: 'Bullets',
      paragraphs: 'Paragraphs',
      numbered: 'Numbered',
    };
    return labels[format] || format;
  };

  const truncate = (text: string, maxLength: number = 50): string => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  };

  return (
    <div className="border border-gray-200 rounded-lg overflow-hidden">
      {/* Collapsed header - always visible */}
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="w-full px-4 py-3 bg-gray-50 hover:bg-gray-100 transition-colors text-left"
      >
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-gray-400 text-sm">
              {isExpanded ? '▼' : '▶'}
            </span>
            <span className="text-sm text-gray-600">
              {formatDate(item.createdAt)}
            </span>
            <span className="text-xs px-2 py-0.5 bg-blue-100 text-blue-700 rounded">
              {formatLabel(item.outputFormat)}
            </span>
          </div>
        </div>
        <p className="mt-1 text-sm text-gray-500 truncate pl-5">
          "{truncate(item.originalContent)}"
        </p>
      </button>

      {/* Expanded content */}
      {isExpanded && (
        <div className="px-4 py-3 border-t border-gray-200 bg-white">
          {/* Original */}
          <div className="mb-3">
            <h4 className="text-xs font-semibold text-gray-500 uppercase mb-1">
              Original
            </h4>
            <p className="text-sm text-gray-700 whitespace-pre-wrap bg-gray-50 p-2 rounded">
              {item.originalContent}
            </p>
          </div>

          {/* Cleaned */}
          <div className="mb-3">
            <h4 className="text-xs font-semibold text-gray-500 uppercase mb-1">
              Cleaned
            </h4>
            <p className="text-sm text-gray-700 whitespace-pre-wrap bg-green-50 p-2 rounded">
              {item.cleanedContent}
            </p>
          </div>

          {/* Copy button */}
          <button
            onClick={handleCopy}
            className="text-sm px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors"
          >
            {copied ? '✓ Copied!' : 'Copy Cleaned'}
          </button>
        </div>
      )}
    </div>
  );
}