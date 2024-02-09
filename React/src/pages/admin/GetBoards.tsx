import React, { useEffect, useState } from 'react'
import { CommonStyle, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query'
import { UnifiedResponse } from '../../shared/TypeMenu'

function GetBoard() {
    const [value, setValue] = useState<string | undefined>();

    return (
        <div style={CommonStyle}>
            <h1 style={ TitleStyle }>모든 문의 확인</h1>
        </div>
    )
}

export default GetBoard