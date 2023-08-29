import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { SixNumberResponse, getBuySixNumberList } from '../../api/userApi'
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { CommonStyle } from '../../components/Styles';
import { ResultContainer } from '../../components/Manufacturing';

function GetBuySixNumberList() {
  const [date, setDate] = useState<string[]>([]);
  const [value, setValue] = useState<SixNumberResponse[]>([]);
  const [show, setShow] = useState<boolean>(false);
  const [result, setResult] = useState<string[]>([]);

  const getBuySixNumberMutation = useMutation<UnifiedResponse<SixNumberResponse[]>, Err>(getBuySixNumberList, {
    onSuccess: (res)=>{
      if (res.code === 200 && res.data) {
        setDate(res.data.map(six => six.date));
        setValue(res.data);
      }
    },
    onError: (err)=>{
      if (err.code === 400) {
        alert(err.msg);
      }
    }
  });

  const getFilteredValues = (selectedDate: string): string[] => {
    if (value.length !== 0) {
      let filteredValues: string[] = [];
      value.filter(six => six.date === selectedDate).map(six => {
        filteredValues = six.numberList;
      });
      
      if (filteredValues.length !== 0) {
        setShow(true);
      }
      
      return filteredValues;
    }

    return [];
  }

  const onClickHandler = (selectedDate: string) => {
    setResult(getFilteredValues(selectedDate));
  }

  useEffect(()=>{ getBuySixNumberMutation.mutate(); }, [])

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "60px" }}>최근 번호 조회</h1>
        {!show ? (
          <div>
            {date.length === 0 ? (
              <div> 값이 존재하지 않습니다 </div>
            ):(
              <div style={{ width:"27cm" }}>
                {date.map((date, index) => (
                  <button
                    key={`date${index}`}
                    style={{ fontSize:"20px", marginBottom:"15px", marginRight:"15px" }}
                    onClick={()=>onClickHandler(date)}
                  >
                    {date}
                  </button>
                ))}
              </div>
            )}
          </div>
        ):(
          <div>
            {result.length !== 0 &&(
              <>
                <button 
                  style={{ fontSize:"20px", marginBottom:"5px" }}
                  onClick={()=>setShow(false)}
                >
                  이전으로 돌아가기
                </button>
                <ResultContainer numSentenceList={result} />
              </>
            )}
          </div>
        )}
    </div>
  )
}

export default GetBuySixNumberList