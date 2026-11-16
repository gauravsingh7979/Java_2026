import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Sparkles, CheckCircle2, AlertTriangle, ArrowRight, X, ChevronDown, ChevronUp, MapPin, Calendar } from 'lucide-react';
import { matchService } from '../services/matchService';

export const MatchScoreCard = ({ match, onDismiss, onClaim }) => {
  const [expanded, setExpanded] = useState(false);
  const [dismissing, setDismissing] = useState(false);

  const foundItem = match.foundItem;
  const scorePercent = Math.round((match.matchScore || 0) * 100);
  const breakdown = match.scoreBreakdown;

  const getClassificationBadge = (cls) => {
    switch (cls) {
      case 'STRONG':
        return 'bg-emerald-100 text-emerald-800 border-emerald-300';
      case 'POSSIBLE':
        return 'bg-amber-100 text-amber-800 border-amber-300';
      case 'WEAK':
      default:
        return 'bg-slate-100 text-slate-800 border-slate-300';
    }
  };

  const getScoreColor = (score) => {
    if (score >= 80) return 'text-emerald-600';
    if (score >= 50) return 'text-amber-600';
    return 'text-slate-500';
  };

  const handleDismiss = async () => {
    setDismissing(true);
    try {
      await matchService.dismissMatch(match.id);
      if (onDismiss) onDismiss(match.id);
    } catch (err) {
      console.error('Failed to dismiss match:', err);
    } finally {
      setDismissing(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm hover:shadow-md transition-all overflow-hidden">
      {/* Header bar */}
      <div className="p-4 bg-gradient-to-r from-indigo-50/60 via-white to-slate-50 flex items-center justify-between border-b border-slate-100">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-indigo-600 text-white flex items-center justify-center shadow-sm">
            <Sparkles className="w-4 h-4" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className={`text-base font-extrabold ${getScoreColor(scorePercent)}`}>
                {scorePercent}% Match
              </span>
              <span
                className={`text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full border ${getClassificationBadge(
                  match.classification
                )}`}
              >
                {match.classification}
              </span>
            </div>
            <p className="text-[11px] text-slate-500">Heuristic Engine Confidence</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setExpanded(!expanded)}
            className="text-xs text-indigo-600 hover:text-indigo-800 font-semibold flex items-center gap-1 px-2.5 py-1 rounded-lg hover:bg-indigo-50 transition-colors"
          >
            <span>{expanded ? 'Hide Details' : 'Score Breakdown'}</span>
            {expanded ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
          </button>
          <button
            onClick={handleDismiss}
            disabled={dismissing}
            title="Dismiss this match recommendation"
            className="p-1.5 text-slate-400 hover:text-rose-600 rounded-lg hover:bg-rose-50 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Expanded Breakdown */}
      {expanded && breakdown && (
        <div className="px-5 py-4 bg-slate-50 border-b border-slate-100 text-xs">
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-3">
            <div className="p-2 bg-white rounded-lg border border-slate-200">
              <span className="text-slate-500 block text-[10px]">Category Match</span>
              <span className="font-bold text-slate-900">{Math.round((breakdown.categoryScore || 0) * 100)}%</span>
            </div>
            <div className="p-2 bg-white rounded-lg border border-slate-200">
              <span className="text-slate-500 block text-[10px]">Brand Match</span>
              <span className="font-bold text-slate-900">{Math.round((breakdown.brandScore || 0) * 100)}%</span>
            </div>
            <div className="p-2 bg-white rounded-lg border border-slate-200">
              <span className="text-slate-500 block text-[10px]">Color Match</span>
              <span className="font-bold text-slate-900">{Math.round((breakdown.colorScore || 0) * 100)}%</span>
            </div>
            <div className="p-2 bg-white rounded-lg border border-slate-200">
              <span className="text-slate-500 block text-[10px]">Date Proximity</span>
              <span className="font-bold text-slate-900">{Math.round((breakdown.dateScore || 0) * 100)}%</span>
            </div>
            <div className="p-2 bg-white rounded-lg border border-slate-200">
              <span className="text-slate-500 block text-[10px]">Location Proximity</span>
              <span className="font-bold text-slate-900">{Math.round((breakdown.locationScore || 0) * 100)}%</span>
            </div>
            <div className="p-2 bg-white rounded-lg border border-slate-200">
              <span className="text-slate-500 block text-[10px]">Text Semantics</span>
              <span className="font-bold text-slate-900">{Math.round((breakdown.semanticScore || 0) * 100)}%</span>
            </div>
          </div>
          {match.matchReasons && (
            <div className="text-[11px] text-slate-600 bg-white p-2.5 rounded-lg border border-slate-200">
              <strong>Match Signals:</strong> {match.matchReasons}
            </div>
          )}
        </div>
      )}

      {/* Candidate Found Item Body */}
      {foundItem ? (
        <div className="p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="space-y-1 max-w-lg">
            <div className="flex items-center gap-2">
              <span className="px-2 py-0.5 rounded text-[10px] font-bold uppercase bg-emerald-100 text-emerald-800">
                Found Candidate
              </span>
              <span className="text-[11px] font-mono text-slate-500">{foundItem.trackingNumber}</span>
            </div>
            <h4 className="text-sm font-bold text-slate-900">{foundItem.title}</h4>
            <p className="text-xs text-slate-600 line-clamp-2">{foundItem.publicDescription}</p>

            <div className="flex items-center gap-4 text-[11px] text-slate-500 pt-1">
              <span className="flex items-center gap-1">
                <MapPin className="w-3 h-3 text-slate-400" />
                {foundItem.locationFound || 'Campus'}
              </span>
              <span className="flex items-center gap-1">
                <Calendar className="w-3 h-3 text-slate-400" />
                {foundItem.dateFound ? new Date(foundItem.dateFound).toLocaleDateString() : 'Unknown date'}
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2 w-full sm:w-auto justify-end pt-2 sm:pt-0">
            <Link
              to={`/found-items/${foundItem.id}`}
              className="px-3 py-2 text-xs font-semibold text-slate-700 hover:text-indigo-600 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors"
            >
              Inspect Item
            </Link>
            <button
              onClick={() => onClaim && onClaim(foundItem)}
              className="px-4 py-2 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl shadow-sm hover:shadow-indigo-500/20 transition-all flex items-center gap-1.5"
            >
              <span>Claim Ownership</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      ) : (
        <div className="p-4 text-xs text-slate-500 italic">Found item details not available</div>
      )}
    </div>
  );
};
