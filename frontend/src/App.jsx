import React, { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import api from './services/api'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import RequestBoardPage from './pages/RequestBoardPage'
import PostBountyPage from './pages/PostBountyPage'
import SessionDetailPage from './pages/SessionDetailPage'
import WalletPage from './pages/WalletPage'
import DisputesPage from './pages/DisputesPage'
import Navbar from './components/Navbar'
import './App.css'

function App() {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [token, setToken] = useState(localStorage.getItem('token'))

  useEffect(() => {
    if (token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    }
    setLoading(false)
  }, [token])

  const handleLogin = (userData, authToken) => {
    setUser(userData)
    setToken(authToken)
    localStorage.setItem('token', authToken)
    api.defaults.headers.common['Authorization'] = `Bearer ${authToken}`
  }

  const handleLogout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('token')
    delete api.defaults.headers.common['Authorization']
  }

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>
  }

  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        {token && <Navbar user={user} onLogout={handleLogout} />}
        <main className="container mx-auto">
          <Routes>
            <Route path="/login" element={!token ? <LoginPage onLogin={handleLogin} /> : <Navigate to="/dashboard" />} />
            <Route path="/register" element={!token ? <RegisterPage /> : <Navigate to="/dashboard" />} />
            <Route path="/dashboard" element={token ? <DashboardPage user={user} /> : <Navigate to="/login" />} />
            <Route path="/requests" element={token ? <RequestBoardPage /> : <Navigate to="/login" />} />
            <Route path="/post-bounty" element={token ? <PostBountyPage /> : <Navigate to="/login" />} />
            <Route path="/sessions/:sessionId" element={token ? <SessionDetailPage /> : <Navigate to="/login" />} />
            <Route path="/wallet" element={token ? <WalletPage user={user} /> : <Navigate to="/login" />} />
            <Route path="/disputes" element={token ? <DisputesPage /> : <Navigate to="/login" />} />
            <Route path="/" element={token ? <Navigate to="/dashboard" /> : <Navigate to="/login" />} />
          </Routes>
        </main>
      </div>
    </Router>
  )
}

export default App
