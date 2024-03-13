import React, { useEffect, useState } from 'react'
import { BoardStyle, CommonStyle, TitleStyle, } from '../../shared/Styles'
import { useMutation } from 'react-query'
import { Err, UnifiedResponse } from '../../shared/TypeMenu'
import { BoardsResponse, getBoardsByStatus } from '../../api/boardApi';
import GetBoard from '../../components/GetBoard';
import SelectStatus from '../../components/SelectStatus';

function GetBoardsByStatus() {
    const [values, setValues] = useState<BoardsResponse[]>([]);
    const [boardId, setBoardId] = useState<number>(-1);
    const [selectedStatus, setSelectedStatus] = useState<string>("");

    const getBoardsByStatusMutation = useMutation<UnifiedResponse<BoardsResponse[]>, Err, string>(getBoardsByStatus, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) setValues(res.data);
        },
        onError: (err)=>{
            setValues([]);
        }
    });

    useEffect(()=>{
        if (selectedStatus !== "") getBoardsByStatusMutation.mutate(selectedStatus);
    }, [selectedStatus])

    const onClickHandler = (id: number) => {
        if (id >= 0) setBoardId(id);
    }

    return (
        boardId !== -1 ? (
            <GetBoard boardId={boardId} />
        ):(
            <div style={CommonStyle}>
                <h1 style={ TitleStyle }>내 문의 확인</h1>
                <SelectStatus onChange={(e)=>{setSelectedStatus(e)}} />
                <div style={{ marginTop:"1cm"}}>
                    {values.length === 0 ? (
                        <div>문의가 존재하지 않습니다</div>
                    ):(
                        values.map(item=>{
                            return (
                                <div 
                                    key={item.boardId}
                                    style={{...BoardStyle, cursor:"pointer"}}
                                    typeof='button'
                                    onClick={()=>onClickHandler(item.boardId)}
                                >
                                    <div style={{ display:"flex" }}>
                                        <span>주제:</span>
                                        <span style={{ marginLeft:"20px"}} >{item.subject}</span>
                                    </div>
                                    <div style={{ display:"flex", marginTop:"20px"}}>
                                        <span>내용:</span>
                                        <span style={{ marginLeft:"20px"}} >{item.contents}</span>
                                    </div>
                                </div>
                            )
                        })
                    )}
                </div>
            </div>
        )
    )
}

export default GetBoardsByStatus