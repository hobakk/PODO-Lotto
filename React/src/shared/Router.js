import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Layout from './Layout'
import Home from '../pages/Home'
import GlobalStyle from './GlobalStyle'
import Lotto from '../pages/Lotto'
import Signin from '../pages/Signin'
import Signup from '../pages/Signup'
import MyPage from '../pages/MyPage'

const Router = () => {
  return (
    <BrowserRouter>
        <GlobalStyle />
        <Layout>
            <Routes>
                <Route path='/' element={<Home />} />
                <Route path='/lotto' element={<Lotto />} />
                <Route path='/signin' element={<Signin />} />
                <Route path='/signup' element={<Signup />} />
                <Route path='/my-page' element={<MyPage />} />
            </Routes>
        </Layout>
    </BrowserRouter>
  )
}

export default Router