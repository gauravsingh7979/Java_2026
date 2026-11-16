import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate, Link } from 'react-router-dom';
import { Sparkles, ArrowLeft, RefreshCw, AlertCircle, ShieldCheck } from 'lucide-react';
import { matchService } from '../services/matchService';
import { lostItemService } from '../services/lostItemService';
import { foundItemService } from '../services/foundItemService';
import MatchCard from '../components/MatchCard';
import { useAuth } from '../context/AuthContext';

export default function Matches() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const isLost = location.pathname.includes('/lost-items');
  const [matches, setMatches] = useState([]);
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchMatches = async () => {
    setLoading(true);
    try {
      if (isLost) {
        const [itemData, matchesData] = await Promise.all([
          lostItemService.getById(id),
          matchService.getLostItemMatches(id),
        ]);
        setItem(itemData);
        setMatches(matchesData || []);
      } else {
        const [itemData, matchesData] = await Promise.all([
          foundItemService.getById(id),
          matchService.getFoundItemMatches(id),
        ]);
        setItem(itemData);
        setMatches(matchesData || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Could not load matches.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMatches();
  }, [id, isLost]);

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Top Header */}
      <div>
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 mb-3"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Back to Item</span>
        </button>

        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
          <div>
            <div className="flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-purple-600" />
              <span className="text-xs font-bold uppercase tracking-wider text-purple-600">
                Heuristic Analysis
              </span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight mt-1">
              Matches for "{item?.title || 'Report'}"
            </h1>
            <p className="text-xs text-slate-500 mt-0.5">
              Target {isLost ? 'Lost Item' : 'Found Item'} #{id} • {item?.category}
            </p>
          </div>

          <button
            onClick={fetchMatches}
            className="inline-flex items-center gap-1.5 px-3 py-2 border border-slate-200 hover:bg-slate-50 rounded-xl text-xs font-semibold text-slate-700 transition-colors self-start sm:self-auto"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Refresh Matches</span>
          </button>
        </div>
      </div>

      {/* Algorithm Scoring Explanation Banner */}
      <div className="bg-gradient-to-r from-purple-50 to-indigo-50 border border-purple-200 rounded-2xl p-5 text-xs text-purple-950 space-y-2">
        <h3 className="font-bold text-sm text-purple-900 flex items-center gap-2">
          <Sparkles className="w-4 h-4 text-purple-600" />
          <span>How FindIt Calculates Scores (100-Point Max)</span>
        </h3>
        <p className="text-purple-900/80 leading-relaxed">
          Our rule-based engine compares attributes using weighted heuristics:
          Category compatibility (25 pts), Title token overlap (20 pts), Brand matching (15 pts), Color similarity (10 pts), Location proximity (15 pts), Date closeness (10 pts), and Description keywords (5 pts).
        </p>
        <div className="flex flex-wrap gap-2 pt-1 font-semibold text-[11px]">
          <span className="bg-emerald-100 text-emerald-800 px-2.5 py-0.5 rounded-full border border-emerald-300">
            Strong Match: 80 - 100 pts
          </span>
          <span className="bg-amber-100 text-amber-800 px-2.5 py-0.5 rounded-full border border-amber-300">
            Possible Match: 60 - 79 pts
          </span>
          <span className="bg-slate-100 text-slate-700 px-2.5 py-0.5 rounded-full border border-slate-300">
            Weak Match: &lt; 60 pts
          </span>
        </div>
      </div>

      {/* Matches List */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600 mx-auto mb-2"></div>
          <p className="text-xs">Analyzing and evaluating potential matches...</p>
        </div>
      ) : error ? (
        <div className="p-6 bg-red-50 border border-red-200 rounded-2xl text-red-700 text-xs flex items-center gap-3">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{error}</span>
        </div>
      ) : matches.length > 0 ? (
        <div className="space-y-4">
          {matches.map((match) => (
            <MatchCard key={match.id} match={match} userRole={user?.role} />
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-3xl border border-slate-200 p-12 text-center text-slate-500 space-y-3">
          <p className="text-base font-semibold text-slate-700">No matches found yet</p>
          <p className="text-xs text-slate-500 max-w-md mx-auto">
            When another community member reports a compatible item that matches your category and characteristics, it will automatically appear here.
          </p>
        </div>
      )}
    </div>
  );
}