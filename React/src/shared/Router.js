import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Layout from './Layout'
import Home from "../pages/users/Home"
import GlobalStyle from './GlobalStyle'
import Signin from '../pages/users/Signin'
import Signup from '../pages/users/Signup'
import MyPage from '../pages/users/MyPage'
import InformationUpdate from '../pages/users/InformationUpdate'
import SetCharging from '../pages/users/SetCharging'
import GetCharging from '../pages/users/GetCharging'
import Premium from '../pages/users/Premium'
import Statement from '../pages/users/Statement'
import BuyNumber from '../pages/sixnum/BuyNumber'
import StatisticalNumber from '../pages/sixnum/StatisticalNumber'
import StatsMain from '../pages/lotto/StatsMain'
import GetUsers from '../pages/admin/GetUsers'
import RecentNumber from '../pages/sixnum/RecentNumber'
import StatsMonth from '../pages/lotto/StatsMonth'
import GetAllCharges from '../pages/admin/GetAllCharges'
import SearchCharges from '../pages/admin/SearchCharges'
import CreateMainLotto from '../pages/admin/CreateMainLotto'
import SetWinNumber from '../pages/admin/SetWinNumber'


const Router = () => {
  return (
    <BrowserRouter>
        <GlobalStyle />
        <Layout>
            <Routes>
                <Route path='/' element={<Home />} />
                <Route path='/signin' element={<Signin />} />
                <Route path='/signup' element={<Signup />} />
                <Route path='/my-page' element={<MyPage />} />
                <Route path='/my-page/update' element={<InformationUpdate />} />
                <Route path='/set-charging' element={<SetCharging />} />
                <Route path='/get-charging' element={<GetCharging />} />
                <Route path='/premium' element={<Premium />} />
                <Route path='/statement' element={<Statement />} />
                <Route path='/buynum' element={<BuyNumber />} />
                <Route path='/stats/num' element={<StatisticalNumber />} />
                <Route path='/stats/main' element={<StatsMain />} />
                <Route path='/stats/month' element={<StatsMonth />} />
                <Route path='/admin/users' element={<GetUsers />} />
                <Route path='/recent/num' element={<RecentNumber />} />
                <Route path='/admin/charges' element={<GetAllCharges />} />
                <Route path='/admin/search' element={<SearchCharges />} />
                <Route path='/admin/lotto' element={<CreateMainLotto />} />
                <Route path='/admin/winnumber' element={<SetWinNumber />} />
            </Routes>
        </Layout>
    </BrowserRouter>
  )
}

export default Router