interface NoteInputProps {
  value: string;
  onChange: (content: string) => void;
  disabled?: boolean;
  maxLength?: number;
}

export default function NoteInput({
  value,
  onChange,
  disabled,
  maxLength = 10000,
}: NoteInputProps) {
  const characterCount = value.length;
  const isNearLimit = characterCount >= maxLength * 0.9;
  const isAtLimit = characterCount >= maxLength;

  return (
    <div className="space-y-2">
      <label htmlFor="noteInput" className="block text-sm font-medium text-gray-700">
        Your Notes
      </label>
      <textarea
        id="noteInput"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        maxLength={maxLength}
        placeholder="Paste your messy notes here..."
        rows={8}
        className={`w-full p-3 border rounded-lg resize-y focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed ${
          isAtLimit
            ? 'border-red-500'
            : isNearLimit
              ? 'border-yellow-500'
              : 'border-gray-300'
        }`}
      />
      <div
        className={`text-sm text-right ${
          isAtLimit
            ? 'text-red-600 font-medium'
            : isNearLimit
              ? 'text-yellow-600'
              : 'text-gray-500'
        }`}
      >
        {characterCount.toLocaleString()} / {maxLength.toLocaleString()} characters
      </div>
    </div>
  );
}
