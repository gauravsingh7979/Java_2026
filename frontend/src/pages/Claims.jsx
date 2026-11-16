import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, ArrowRight, AlertCircle } from 'lucide-react';
import { claimService } from '../services/claimService';
import ClaimCard from '../components/ClaimCard';

export default function Claims() {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchClaims = async () => {
    setLoading(true);
    try {
      const data = await claimService.getMyClaims();
      setClaims(data || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load your claims.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClaims();
  }, []);

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-emerald-600" />
            <span className="text-xs font-bold uppercase tracking-wider text-emerald-600">
              Ownership Claims
            </span>
          </div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight mt-1">
            My Claims &amp; Handover
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Track verification progress, review admin decisions, and retrieve your recovery verification code once approved.
          </p>
        </div>

        <Link
          to="/found-items"
          className="inline-flex items-center gap-1.5 px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-xl text-xs font-bold transition-colors self-start sm:self-auto"
        >
          <span>Browse Found Directory</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-2xl text-red-700 text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Claims List */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600 mx-auto mb-2"></div>
          <p className="text-xs">Loading your submitted claims...</p>
        </div>
      ) : claims.length > 0 ? (
        <div className="space-y-4">
          {claims.map((claim) => (
            <ClaimCard key={claim.id} claim={claim} onUpdate={fetchClaims} />
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-3xl border border-slate-200 p-12 text-center text-slate-500 space-y-3">
          <ShieldCheck className="w-12 h-12 text-slate-300 mx-auto mb-2" />
          <p className="text-base font-semibold text-slate-700">You haven't submitted any claims yet</p>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            Browse the Found Items directory to spot belongings that match yours, then submit a claim with your ownership clues.
          </p>
          <Link
            to="/found-items"
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold shadow-md"
          >
            Browse Found Items
          </Link>
        </div>
      )}
    </div>
  );
}