import React from 'react'
import { useNavigate } from 'react-router-dom'

function Home() {
    const navigate = useNavigate();

  return (
    <div style={{ fontSize: "50px"}}>
      Home
    </div>
  )
}

export default Home