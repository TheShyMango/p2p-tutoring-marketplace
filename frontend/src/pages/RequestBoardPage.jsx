import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { sessionAPI, handleError } from '../services/api'
import { BookOpen, DollarSign, Clock, CheckCircle } from 'lucide-react'

export default function RequestBoardPage() {
  const [requests, setRequests] = useState([])
  const [filteredRequests, setFilteredRequests] = useState([])
  const [skillFilter, setSkillFilter] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadRequests()
  }, [])

  const loadRequests = async () => {
    try {
      const response = await sessionAPI.getOpenRequests()
      setRequests(response.data)
      setFilteredRequests(response.data)
    } catch (err) {
      setError(handleError(err))
    } finally {
      setLoading(false)
    }
  }

  const handleSkillFilter = async (skill) => {
    setSkillFilter(skill)
    if (!skill) {
      setFilteredRequests(requests)
      return
    }

    try {
      const response = await sessionAPI.getRequestsBySkill(skill)
      setFilteredRequests(response.data)
    } catch (err) {
      setError(handleError(err))
    }
  }

  if (loading) {
    return <div className="text-center py-10">Loading requests...</div>
  }

  return (
    <div className="py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800">
          <BookOpen className="inline mr-2" />
          Tutoring Requests
        </h1>
        <button
          onClick={loadRequests}
          className="button-secondary"
        >
          Refresh
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {/* Skill Filter */}
      <div className="card mb-6">
        <h2 className="font-semibold text-gray-800 mb-3">Filter by Skill</h2>
        <input
          type="text"
          value={skillFilter}
          onChange={(e) => handleSkillFilter(e.target.value)}
          placeholder="Enter skill name (Math, Physics, etc.)"
          className="input"
        />
      </div>

      {/* Requests Grid */}
      {filteredRequests.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredRequests.map(request => (
            <div key={request.id} className="card hover:shadow-lg transition-shadow">
              <div className="flex justify-between items-start mb-3">
                <h3 className="text-lg font-bold text-gray-800 flex-1">
                  {request.title}
                </h3>
                <span className="badge-primary">
                  {request.state}
                </span>
              </div>

              <p className="text-gray-600 text-sm mb-3 line-clamp-2">
                {request.description}
              </p>

              {/* Skill Tag */}
              <div className="mb-4">
                <span className="inline-block bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
                  {request.skillTopic}
                </span>
              </div>

              {/* Points and Info */}
              <div className="space-y-2 mb-4">
                <div className="flex items-center text-gray-700">
                  <DollarSign size={18} className="mr-2 text-yellow-600" />
                  <span>{request.bountyPoints} points</span>
                </div>
                <div className="flex items-center text-gray-700">
                  <Clock size={18} className="mr-2 text-blue-600" />
                  <span>Posted: {new Date(request.creationTime).toLocaleDateString()}</span>
                </div>
              </div>

              {/* Accept Button */}
              <Link
                to={`/sessions/${request.id}`}
                className="button-primary w-full text-center flex items-center justify-center gap-2"
              >
                <CheckCircle size={18} />
                View & Accept
              </Link>
            </div>
          ))}
        </div>
      ) : (
        <div className="card text-center py-10">
          <BookOpen size={48} className="mx-auto text-gray-400 mb-3" />
          <p className="text-gray-600 text-lg">No requests available</p>
          <p className="text-gray-500 text-sm">Check back later or adjust filters</p>
        </div>
      )}
    </div>
  )
}
