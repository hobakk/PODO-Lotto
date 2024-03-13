import React, { useEffect, useState } from 'react'
import { CommonStyle, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query'
import { UnifiedResponse } from '../../shared/TypeMenu'
import { PageBoardsRes, getAllBoardsByStatus } from '../../api/boardApi';
import SelectStatus from '../../components/SelectStatus';

function GetBoard() {
    const [selectedStatus, setSelectedStatus] = useState<string>("");
    const [value, setValue] = useState<PageBoardsRes>();

    const getAllBoardsMutation = useMutation(getAllBoardsByStatus, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) setValue(res.data);
        },
        onError: (err)=>{
            console.log(err);
        }
    })

    useEffect(()=>{
        if (selectedStatus !== "") getAllBoardsMutation.mutate(selectedStatus);
    }, [selectedStatus])

    return (
        <div style={CommonStyle}>
            <h1 style={ TitleStyle }>모든 문의 확인</h1>
            <SelectStatus onChange={(e)=>{setSelectedStatus(e)}} />
        </div>
    )
}

export default GetBoard