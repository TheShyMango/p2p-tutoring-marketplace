import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { sessionAPI, walletAPI, handleError } from '../services/api'
import { DollarSign, BookOpen, TrendingUp, Clock } from 'lucide-react'

export default function DashboardPage({ user }) {
  const [stats, setStats] = useState({
    wallet: null,
    sessions: []
  })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (user?.id) {
      loadDashboardData()
    } else {
      setLoading(false)
    }
  }, [user])

  const loadDashboardData = async () => {
    try {
      const [walletRes, sessionsRes] = await Promise.all([
        walletAPI.getMyWallet(),
        sessionAPI.getStudentSessions(user?.id)
      ])

      setStats({
        wallet: walletRes.data,
        sessions: sessionsRes.data
      })
    } catch (err) {
      setError(handleError(err))
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="text-center py-10">Loading dashboard...</div>
  }

  if (!user) {
    return (
      <div className="text-center py-10">
        <p className="text-gray-600 mb-4">Please log in to view your dashboard.</p>
        <Link to="/login" className="btn-primary">Log In</Link>
      </div>
    )
  }

  return (
    <div className="py-8">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">
        Welcome back, {user?.fullName || user?.username}!
      </h1>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        {/* Wallet Balance */}
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Available Points</p>
              <p className="text-2xl font-bold text-blue-600">
                {stats.wallet?.availableBalance || 0}
              </p>
            </div>
            <DollarSign size={32} className="text-blue-600 opacity-50" />
          </div>
        </div>

        {/* Locked Points */}
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Locked Points</p>
              <p className="text-2xl font-bold text-orange-600">
                {stats.wallet?.lockedBalance || 0}
              </p>
            </div>
            <Clock size={32} className="text-orange-600 opacity-50" />
          </div>
        </div>

        {/* Total Earned */}
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Total Earned</p>
              <p className="text-2xl font-bold text-green-600">
                {stats.wallet?.totalEarned || 0}
              </p>
            </div>
            <TrendingUp size={32} className="text-green-600 opacity-50" />
          </div>
        </div>

        {/* Active Sessions */}
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Active Sessions</p>
              <p className="text-2xl font-bold text-purple-600">
                {stats.sessions?.filter(s => ['OPEN', 'IN_PROGRESS'].includes(s.state))?.length || 0}
              </p>
            </div>
            <BookOpen size={32} className="text-purple-600 opacity-50" />
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <Link
          to="/requests"
          className="card hover:shadow-lg text-center p-4 bg-blue-50 border-2 border-blue-200"
        >
          <BookOpen className="mx-auto mb-2 text-blue-600" size={24} />
          <h3 className="font-semibold text-gray-800">Browse Requests</h3>
          <p className="text-sm text-gray-600">Find tutoring opportunities</p>
        </Link>

        <Link
          to="/post-bounty"
          className="card hover:shadow-lg text-center p-4 bg-green-50 border-2 border-green-200"
        >
          <DollarSign className="mx-auto mb-2 text-green-600" size={24} />
          <h3 className="font-semibold text-gray-800">Post Bounty</h3>
          <p className="text-sm text-gray-600">Request tutoring help</p>
        </Link>

        <Link
          to="/wallet"
          className="card hover:shadow-lg text-center p-4 bg-purple-50 border-2 border-purple-200"
        >
          <DollarSign className="mx-auto mb-2 text-purple-600" size={24} />
          <h3 className="font-semibold text-gray-800">View Wallet</h3>
          <p className="text-sm text-gray-600">Track points & history</p>
        </Link>
      </div>

      {/* Recent Sessions */}
      <div className="card">
        <h2 className="text-xl font-bold text-gray-800 mb-4">Recent Sessions</h2>
        {stats.sessions?.length > 0 ? (
          <div className="space-y-3">
            {stats.sessions.slice(0, 5).map(session => (
              <Link
                key={session.id}
                to={`/sessions/${session.id}`}
                className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100"
              >
                <div>
                  <p className="font-semibold text-gray-800">{session.title}</p>
                  <p className="text-sm text-gray-600">{session.skillTopic}</p>
                </div>
                <div className="text-right">
                  <span className={`badge ${
                    session.state === 'OPEN' ? 'badge-primary' :
                    session.state === 'IN_PROGRESS' ? 'badge-warning' :
                    'badge-success'
                  }`}>
                    {session.state}
                  </span>
                  <p className="text-sm font-semibold text-blue-600 mt-1">
                    {session.bountyPoints} points
                  </p>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <p className="text-gray-600 text-center py-6">No sessions yet</p>
        )}
      </div>
    </div>
  )
}
