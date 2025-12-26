import type { OutputFormat } from '../types';

interface FormatSelectorProps {
  value: OutputFormat;
  onChange: (format: OutputFormat) => void;
  disabled?: boolean;
}

const FORMAT_OPTIONS: { value: OutputFormat; label: string }[] = [
  { value: 'bullets', label: 'Bullet Points' },
  { value: 'paragraphs', label: 'Paragraphs' },
  { value: 'numbered', label: 'Numbered List' },
];

export default function FormatSelector({ value, onChange, disabled }: FormatSelectorProps) {
  return (
    <fieldset className="space-y-2">
      <legend className="text-sm font-medium text-gray-700">Output Format</legend>
      <div className="flex flex-wrap gap-4">
        {FORMAT_OPTIONS.map((option) => (
          <label
            key={option.value}
            className={`flex items-center gap-2 cursor-pointer ${
              disabled ? 'opacity-50 cursor-not-allowed' : ''
            }`}
          >
            <input
              type="radio"
              name="outputFormat"
              value={option.value}
              checked={value === option.value}
              onChange={() => onChange(option.value)}
              disabled={disabled}
              className="w-4 h-4 text-blue-600 focus:ring-2 focus:ring-blue-500"
            />
            <span className="text-gray-700">{option.label}</span>
          </label>
        ))}
      </div>
    </fieldset>
  );
}
