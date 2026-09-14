import React, { useState, useEffect } from 'react'
import { walletAPI, handleError } from '../services/api'
import { DollarSign, TrendingUp, TrendingDown, Clock } from 'lucide-react'

export default function WalletPage({ user }) {
  const [wallet, setWallet] = useState(null)
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadWalletData()
  }, [user])

  const loadWalletData = async () => {
    try {
      const [walletRes, txRes] = await Promise.all([
        walletAPI.getMyWallet(),
        walletAPI.getMyTransactions()
      ])

      setWallet(walletRes.data)
      setTransactions(txRes.data)
    } catch (err) {
      setError(handleError(err))
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="text-center py-10">Loading wallet...</div>
  }

  return (
    <div className="py-8">
      <h1 className="text-3xl font-bold text-gray-800 mb-8 flex items-center gap-3">
        <DollarSign size={32} className="text-blue-600" />
        My Wallet
      </h1>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {/* Wallet Summary */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div className="card">
          <p className="text-gray-600 text-sm mb-2">Available Points</p>
          <p className="text-3xl font-bold text-blue-600">
            {wallet?.availableBalance || 0}
          </p>
        </div>

        <div className="card">
          <p className="text-gray-600 text-sm mb-2">Locked Points</p>
          <p className="text-3xl font-bold text-orange-600">
            {wallet?.lockedBalance || 0}
          </p>
        </div>

        <div className="card">
          <p className="text-gray-600 text-sm mb-2">Total Earned</p>
          <p className="text-3xl font-bold text-green-600">
            {wallet?.totalEarned || 0}
          </p>
        </div>

        <div className="card">
          <p className="text-gray-600 text-sm mb-2">Total Spent</p>
          <p className="text-3xl font-bold text-red-600">
            {wallet?.totalSpent || 0}
          </p>
        </div>
      </div>

      {/* Transaction History */}
      <div className="card">
        <h2 className="text-xl font-bold text-gray-800 mb-6">Transaction History</h2>
        
        {transactions.length > 0 ? (
          <div className="space-y-3">
            {transactions.map((tx, idx) => (
              <div key={idx} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100">
                <div className="flex items-center gap-3 flex-1">
                  <div className={`p-2 rounded-full ${
                    ['EARNING', 'DEPOSIT'].includes(tx.transactionType)
                      ? 'bg-green-100'
                      : 'bg-red-100'
                  }`}>
                    {['EARNING', 'DEPOSIT'].includes(tx.transactionType)
                      ? <TrendingUp size={20} className="text-green-600" />
                      : <TrendingDown size={20} className="text-red-600" />
                    }
                  </div>
                  <div className="flex-1">
                    <p className="font-semibold text-gray-800">{tx.description}</p>
                    <div className="flex items-center gap-2 text-sm text-gray-600">
                      <Clock size={14} />
                      {new Date(tx.createdAt).toLocaleString()}
                    </div>
                  </div>
                </div>
                <div className="text-right">
                  <p className={`font-semibold text-lg ${
                    ['EARNING', 'DEPOSIT'].includes(tx.transactionType)
                      ? 'text-green-600'
                      : 'text-red-600'
                  }`}>
                    {['EARNING', 'DEPOSIT'].includes(tx.transactionType) ? '+' : '-'}
                    {tx.amount}
                  </p>
                  <p className="text-xs text-gray-600">
                    Balance: {tx.balanceAfter}
                  </p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-10">
            <DollarSign size={48} className="mx-auto text-gray-400 mb-3" />
            <p className="text-gray-600">No transactions yet</p>
          </div>
        )}
      </div>
    </div>
  )
}
