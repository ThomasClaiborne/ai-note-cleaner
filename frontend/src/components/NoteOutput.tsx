import { useState } from 'react';

interface NoteOutputProps {
  content: string;
  format: string;
}

export default function NoteOutput({ content, format }: NoteOutputProps) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(content);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  return (
    <div className="space-y-2">
      <div className="flex justify-between items-center">
        <h2 className="text-sm font-medium text-gray-700">
          Cleaned Notes ({format})
        </h2>
        <button
          onClick={handleCopy}
          className="px-3 py-1 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-md transition-colors"
        >
          {copied ? '✓ Copied!' : 'Copy'}
        </button>
      </div>
      <div className="bg-green-50 border border-green-300 rounded-lg p-4">
        <pre className="whitespace-pre-wrap font-sans text-gray-800">{content}</pre>
      </div>
    </div>
  );
}
