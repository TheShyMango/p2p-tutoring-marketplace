import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authAPI, handleError } from '../services/api'
import { Mail, Lock } from 'lucide-react'

export default function LoginPage({ onLogin }) {
  const [formData, setFormData] = useState({
    usernameOrEmail: '',
    password: ''
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
    setLoading(true)

    try {
      const response = await authAPI.login(formData)
      onLogin(response.data.user, response.data.token)
      navigate('/dashboard')
    } catch (err) {
      setError(handleError(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-center text-gray-800 mb-8">
          TutorMarket
        </h1>

        <h2 className="text-xl font-semibold text-center text-gray-700 mb-6">
          Student Login
        </h2>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Username/Email */}
          <div>
            <label className="label">
              <Mail size={18} className="inline mr-2" />
              Username or Email
            </label>
            <input
              type="text"
              name="usernameOrEmail"
              value={formData.usernameOrEmail}
              onChange={handleChange}
              className="input"
              placeholder="Enter username or email"
              required
            />
          </div>

          {/* Password */}
          <div>
            <label className="label">
              <Lock size={18} className="inline mr-2" />
              Password
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="input"
              placeholder="Enter password"
              required
              minLength="8"
            />
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            disabled={loading}
            className="button-primary w-full mt-6 disabled:opacity-50"
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        {/* Register Link */}
        <div className="mt-6 text-center">
          <p className="text-gray-600">
            Don't have an account?{' '}
            <Link to="/register" className="text-blue-600 font-semibold hover:underline">
              Register here
            </Link>
          </p>
        </div>

        {/* Features */}
        <div className="mt-8 pt-6 border-t border-gray-200">
          <h3 className="text-sm font-semibold text-gray-700 mb-3">Why TutorMarket?</h3>
          <ul className="text-sm text-gray-600 space-y-2">
            <li>✓ P2P tutoring marketplace</li>
            <li>✓ Internal point economy</li>
            <li>✓ Secure payment system</li>
            <li>✓ Skill-based matching</li>
          </ul>
        </div>
      </div>
    </div>
  )
}
