import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { sessionAPI, handleError } from '../services/api'
import { DollarSign, BookOpen } from 'lucide-react'

export default function PostBountyPage() {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    skillTopic: '',
    bountyPoints: ''
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    // Validation
    if (!formData.title.trim()) {
      setError('Title is required')
      return
    }

    if (!formData.description.trim()) {
      setError('Description is required')
      return
    }

    if (!formData.skillTopic.trim()) {
      setError('Skill topic is required')
      return
    }

    const bountyPoints = parseInt(formData.bountyPoints)
    if (isNaN(bountyPoints) || bountyPoints <= 0) {
      setError('Bounty points must be greater than 0')
      return
    }

    setLoading(true)

    try {
      const response = await sessionAPI.postRequest({
        title: formData.title,
        description: formData.description,
        skillTopic: formData.skillTopic,
        bountyPoints: bountyPoints
      })

      // Redirect to session detail
      navigate('/dashboard')
    } catch (err) {
      setError(handleError(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="py-8 max-w-2xl mx-auto">
      <div className="flex items-center gap-3 mb-8">
        <DollarSign size={32} className="text-blue-600" />
        <h1 className="text-3xl font-bold text-gray-800">
          Post Tutoring Request
        </h1>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      <div className="card">
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Title */}
          <div>
            <label className="label">
              <BookOpen size={18} className="inline mr-2" />
              Request Title
            </label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              className="input"
              placeholder="e.g., Need help with Calculus"
              maxLength="100"
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              {formData.title.length}/100 characters
            </p>
          </div>

          {/* Description */}
          <div>
            <label className="label">
              Description
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              className="input h-32"
              placeholder="Describe what you need help with, topics to cover, your level, etc."
              maxLength="500"
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              {formData.description.length}/500 characters
            </p>
          </div>

          {/* Skill Topic */}
          <div>
            <label className="label">
              Skill/Subject
            </label>
            <input
              type="text"
              name="skillTopic"
              value={formData.skillTopic}
              onChange={handleChange}
              className="input"
              placeholder="e.g., Mathematics, Physics, Chemistry, Programming"
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              Choose a main skill/subject
            </p>
          </div>

          {/* Bounty Points */}
          <div>
            <label className="label">
              <DollarSign size={18} className="inline mr-2" />
              Bounty Points
            </label>
            <input
              type="number"
              name="bountyPoints"
              value={formData.bountyPoints}
              onChange={handleChange}
              className="input"
              placeholder="e.g., 100"
              min="1"
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              Points will be locked until session completion
            </p>
          </div>

          {/* Info Box */}
          <div className="bg-blue-50 border border-blue-200 p-4 rounded-lg">
            <h3 className="font-semibold text-blue-900 mb-2">How it works:</h3>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>✓ Points are locked from your account when posted</li>
              <li>✓ Tutor accepts your request</li>
              <li>✓ After session completion, points transfer to tutor</li>
              <li>✓ Can raise dispute if not satisfied</li>
            </ul>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            disabled={loading}
            className="button-primary w-full disabled:opacity-50"
          >
            {loading ? 'Posting...' : 'Post Request'}
          </button>
        </form>
      </div>
    </div>
  )
}
