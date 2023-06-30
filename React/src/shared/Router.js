import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Layout from './Layout'
import Home from '../pages/Home'
import GlobalStyle from './GlobalStyle'
import Lotto from '../pages/Lotto'

const Router = () => {
  return (
    <BrowserRouter>
        <GlobalStyle />
        <Layout>
            <Routes>
                <Route path='/' element={<Home />} />
                <Route path='/lotto' element={<Lotto />} />
            </Routes>
        </Layout>
    </BrowserRouter>
  )
}

export default Router