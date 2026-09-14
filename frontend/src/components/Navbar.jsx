import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Menu, X, LogOut, Home, BookOpen, DollarSign, AlertCircle } from 'lucide-react'

export default function Navbar({ user, onLogout }) {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const navigate = useNavigate()

  const handleLogout = () => {
    onLogout()
    navigate('/login')
  }

  return (
    <nav className="bg-white shadow-md sticky top-0 z-50">
      <div className="container mx-auto px-4 py-3 flex justify-between items-center">
        {/* Logo */}
        <Link to="/dashboard" className="text-2xl font-bold text-blue-600">
          TutorMarket
        </Link>

        {/* Desktop Navigation */}
        <div className="hidden md:flex items-center gap-6">
          <Link to="/dashboard" className="flex items-center gap-2 text-gray-700 hover:text-blue-600">
            <Home size={20} />
            Dashboard
          </Link>
          <Link to="/requests" className="flex items-center gap-2 text-gray-700 hover:text-blue-600">
            <BookOpen size={20} />
            Requests
          </Link>
          <Link to="/post-bounty" className="flex items-center gap-2 text-gray-700 hover:text-blue-600">
            <DollarSign size={20} />
            Post Bounty
          </Link>
          <Link to="/wallet" className="flex items-center gap-2 text-gray-700 hover:text-blue-600">
            <DollarSign size={20} />
            Wallet
          </Link>
          <Link to="/disputes" className="flex items-center gap-2 text-gray-700 hover:text-blue-600">
            <AlertCircle size={20} />
            Disputes
          </Link>
        </div>

        {/* User Info and Logout */}
        <div className="hidden md:flex items-center gap-4">
          <div className="text-sm text-gray-600">
            Welcome, <span className="font-semibold">{user?.username}</span>
          </div>
          <button
            onClick={handleLogout}
            className="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors"
          >
            <LogOut size={18} />
            Logout
          </button>
        </div>

        {/* Mobile Menu Button */}
        <button
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          className="md:hidden text-gray-700 hover:text-blue-600"
        >
          {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Mobile Navigation */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-gray-100 p-4 space-y-2">
          <Link
            to="/dashboard"
            className="block py-2 px-4 rounded hover:bg-gray-200"
            onClick={() => setMobileMenuOpen(false)}
          >
            Dashboard
          </Link>
          <Link
            to="/requests"
            className="block py-2 px-4 rounded hover:bg-gray-200"
            onClick={() => setMobileMenuOpen(false)}
          >
            Requests
          </Link>
          <Link
            to="/post-bounty"
            className="block py-2 px-4 rounded hover:bg-gray-200"
            onClick={() => setMobileMenuOpen(false)}
          >
            Post Bounty
          </Link>
          <Link
            to="/wallet"
            className="block py-2 px-4 rounded hover:bg-gray-200"
            onClick={() => setMobileMenuOpen(false)}
          >
            Wallet
          </Link>
          <Link
            to="/disputes"
            className="block py-2 px-4 rounded hover:bg-gray-200"
            onClick={() => setMobileMenuOpen(false)}
          >
            Disputes
          </Link>
          <button
            onClick={() => {
              handleLogout()
              setMobileMenuOpen(false)
            }}
            className="w-full text-left py-2 px-4 rounded bg-red-600 text-white hover:bg-red-700"
          >
            Logout
          </button>
        </div>
      )}
    </nav>
  )
}
