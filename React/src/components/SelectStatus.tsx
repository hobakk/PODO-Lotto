import React, { useEffect, useState } from 'react'

interface SelectStatusProps {
    onChange: (status: string) => void;
}

function SelectStatus({ onChange }: SelectStatusProps) {
    const handleStatusChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        onChange(e.target.value);
    };

    return (
        <select 
            onChange={handleStatusChange} 
            style={{ width:"3cm", height:"0.65cm",textAlign:"center" }}
        >
            <option value="">상태 선택</option>
            <option value="PROCESSING">처리중</option>
            <option value="UNPROCESSED">미처리</option>
            <option value="COMPLETE">처리완료</option>
        </select>
    )
}

export default SelectStatus