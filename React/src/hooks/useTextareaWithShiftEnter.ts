import { useState } from 'react';

function useTextareaWithShiftEnter() {
    const [textValue, setTextValue] = useState<string>('');

    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter' && e.shiftKey) {
            e.preventDefault();
            const textarea = e.target as HTMLTextAreaElement;
            const start = textarea.selectionStart || 0;
            const end = textarea.selectionEnd || 0;
            const currentValue = textarea.value;
            const newValue = currentValue.substring(0, start) + '\n' + currentValue.substring(end);
            setTextValue(newValue);
            textarea.selectionStart = textarea.selectionEnd = start + 1;
        }
    };

    return {
        textValue,
        setTextValue,
        handleKeyDown
    };
}

export default useTextareaWithShiftEnter;
