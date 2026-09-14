import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { sessionAPI, handleError } from '../services/api'
import { ArrowLeft, CheckCircle, AlertCircle } from 'lucide-react'

export default function SessionDetailPage() {
  const { sessionId } = useParams()
  const [session, setSession] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionLoading, setActionLoading] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    loadSession()
  }, [sessionId])

  const loadSession = async () => {
    try {
      // Fetch single session - assuming there's a GET endpoint
      const response = await sessionAPI.getOpenRequests()
      const found = response.data.find(s => s.id === parseInt(sessionId))
      if (found) {
        setSession(found)
      }
    } catch (err) {
      setError(handleError(err))
    } finally {
      setLoading(false)
    }
  }

  const handleAccept = async () => {
    setActionLoading(true)
    try {
      await sessionAPI.acceptRequest(sessionId)
      loadSession()
    } catch (err) {
      setError(handleError(err))
    } finally {
      setActionLoading(false)
    }
  }

  const handleComplete = async () => {
    setActionLoading(true)
    try {
      await sessionAPI.completeSession(sessionId, 60)
      loadSession()
    } catch (err) {
      setError(handleError(err))
    } finally {
      setActionLoading(false)
    }
  }

  const handleDispute = async () => {
    setActionLoading(true)
    try {
      await sessionAPI.raiseDispute(sessionId)
      loadSession()
    } catch (err) {
      setError(handleError(err))
    } finally {
      setActionLoading(false)
    }
  }

  if (loading) {
    return <div className="text-center py-10">Loading session...</div>
  }

  if (!session) {
    return (
      <div className="py-8">
        <button onClick={() => navigate('/requests')} className="button-secondary mb-4 flex items-center gap-2">
          <ArrowLeft size={18} />
          Back
        </button>
        <div className="card text-center py-10">
          <p className="text-gray-600">Session not found</p>
        </div>
      </div>
    )
  }

  return (
    <div className="py-8">
      <button onClick={() => navigate('/requests')} className="button-secondary mb-6 flex items-center gap-2">
        <ArrowLeft size={18} />
        Back
      </button>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      <div className="max-w-2xl">
        <div className="card mb-6">
          <div className="flex justify-between items-start mb-4">
            <h1 className="text-2xl font-bold text-gray-800">{session.title}</h1>
            <span className={`badge ${
              session.state === 'OPEN' ? 'badge-primary' :
              session.state === 'IN_PROGRESS' ? 'badge-warning' :
              session.state === 'IN_DISPUTE' ? 'badge-danger' :
              'badge-success'
            }`}>
              {session.state}
            </span>
          </div>

          <p className="text-gray-600 mb-4">{session.description}</p>

          <div className="grid grid-cols-3 gap-4 mb-6 py-4 border-y border-gray-200">
            <div>
              <p className="text-gray-600 text-sm">Skill</p>
              <p className="font-semibold text-gray-800">{session.skillTopic}</p>
            </div>
            <div>
              <p className="text-gray-600 text-sm">Bounty</p>
              <p className="font-semibold text-blue-600 text-lg">{session.bountyPoints} points</p>
            </div>
            <div>
              <p className="text-gray-600 text-sm">Posted</p>
              <p className="font-semibold text-gray-800">
                {new Date(session.creationTime).toLocaleDateString()}
              </p>
            </div>
          </div>

          {/* Student Info */}
          {session.student && (
            <div className="bg-gray-50 p-4 rounded-lg mb-6">
              <p className="text-sm text-gray-600 mb-2">Posted by:</p>
              <p className="font-semibold text-gray-800">{session.student.fullName}</p>
              <p className="text-sm text-gray-600">{session.student.email}</p>
            </div>
          )}

          {/* Action Buttons */}
          {session.state === 'OPEN' && (
            <button
              onClick={handleAccept}
              disabled={actionLoading}
              className="button-primary w-full flex items-center justify-center gap-2 disabled:opacity-50"
            >
              <CheckCircle size={18} />
              {actionLoading ? 'Accepting...' : 'Accept Request'}
            </button>
          )}

          {session.state === 'IN_PROGRESS' && (
            <div className="space-y-2">
              <button
                onClick={handleComplete}
                disabled={actionLoading}
                className="button-primary w-full flex items-center justify-center gap-2 disabled:opacity-50"
              >
                <CheckCircle size={18} />
                {actionLoading ? 'Completing...' : 'Mark as Complete'}
              </button>
              <button
                onClick={handleDispute}
                disabled={actionLoading}
                className="button-danger w-full flex items-center justify-center gap-2 disabled:opacity-50"
              >
                <AlertCircle size={18} />
                Raise Dispute
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
