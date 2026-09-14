import React, { useState, useEffect } from 'react'
import { disputeAPI, handleError } from '../services/api'
import { AlertCircle } from 'lucide-react'

export default function DisputesPage() {
  const [disputes, setDisputes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadDisputes()
  }, [])

  const loadDisputes = async () => {
    try {
      const response = await disputeAPI.getDisputes()
      setDisputes(response.data)
    } catch (err) {
      setError(handleError(err))
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="text-center py-10">Loading disputes...</div>
  }

  return (
    <div className="py-8">
      <h1 className="text-3xl font-bold text-gray-800 mb-8 flex items-center gap-3">
        <AlertCircle size={32} className="text-red-600" />
        Disputes
      </h1>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {disputes.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {disputes.map(dispute => (
            <div key={dispute.id} className="card border-l-4 border-red-600">
              <div className="flex justify-between items-start mb-3">
                <h3 className="font-bold text-gray-800">
                  Dispute #{dispute.id}
                </h3>
                <span className={`badge ${
                  dispute.status === 'OPEN' ? 'badge-warning' :
                  dispute.status === 'AWARDED_TO_TUTOR' ? 'badge-success' :
                  'badge-primary'
                }`}>
                  {dispute.status}
                </span>
              </div>

              <p className="text-sm text-gray-600 mb-2">
                <strong>Reason:</strong> {dispute.reason}
              </p>

              <p className="text-sm text-gray-600 mb-4">
                <strong>Description:</strong> {dispute.description}
              </p>

              <div className="bg-gray-50 p-3 rounded mb-4">
                <p className="text-xs text-gray-600 mb-1">Session Info:</p>
                <p className="text-sm font-semibold text-gray-800">
                  {dispute.tutoringSession?.title}
                </p>
                <p className="text-xs text-gray-600">
                  Raised by: {dispute.raisedBy?.fullName}
                </p>
              </div>

              {dispute.status === 'OPEN' && (
                <div className="space-y-2">
                  <button className="button-success w-full text-sm">
                    Award to Tutor
                  </button>
                  <button className="button-secondary w-full text-sm">
                    Refund to Student
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="card text-center py-10">
          <AlertCircle size={48} className="mx-auto text-gray-400 mb-3" />
          <p className="text-gray-600 text-lg">No disputes</p>
          <p className="text-gray-500 text-sm">All sessions are running smoothly</p>
        </div>
      )}
    </div>
  )
}
