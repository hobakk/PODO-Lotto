import { useState } from 'react';

function useTextareaWithShiftEnter() {
    const [value, setValue] = useState<string>('');

    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter' && e.shiftKey) {
            e.preventDefault(); // 기본 동작 방지
            const textarea = e.target as HTMLTextAreaElement;
            const start = textarea.selectionStart || 0;
            const end = textarea.selectionEnd || 0;
            const currentValue = textarea.value;
            const newValue = currentValue.substring(0, start) + '\n' + currentValue.substring(end);
            setValue(newValue);
            textarea.selectionStart = textarea.selectionEnd = start + 1;
        }
    };

    return {
        value,
        handleKeyDown
    };
}

export default useTextareaWithShiftEnter;
