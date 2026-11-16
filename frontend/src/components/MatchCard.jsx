import React from 'react';
import { Link } from 'react-router-dom';
import { Sparkles, ArrowRight, ShieldCheck, CheckCircle2, ChevronRight } from 'lucide-react';
import StatusBadge from './StatusBadge';

export default function MatchCard({ match, userRole = 'USER' }) {
  const { score, classification, matchReasons, lostItem, foundItem } = match;

  const scoreColor =
    score >= 80
      ? 'text-emerald-600 bg-emerald-50 border-emerald-200'
      : score >= 60
      ? 'text-amber-600 bg-amber-50 border-amber-200'
      : 'text-slate-600 bg-slate-50 border-slate-200';

  const progressBg =
    score >= 80 ? 'bg-emerald-500' : score >= 60 ? 'bg-amber-500' : 'bg-slate-400';

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm hover:shadow-md transition-shadow">
      {/* Header with Score */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div className="flex items-center gap-3">
          <div className={`w-14 h-14 rounded-2xl border flex flex-col items-center justify-center font-bold ${scoreColor}`}>
            <span className="text-xl leading-none">{Math.round(score)}</span>
            <span className="text-[10px] uppercase font-semibold">Score</span>
          </div>
          <div>
            <div className="flex items-center gap-2 mb-1">
              <StatusBadge status={classification} />
              <span className="text-xs text-slate-400">Match ID #{match.id}</span>
            </div>
            <p className="text-sm font-medium text-slate-700">
              Smart Heuristic Analysis
            </p>
          </div>
        </div>

        {/* Score Progress Bar */}
        <div className="w-full sm:w-48">
          <div className="flex justify-between text-xs text-slate-500 mb-1">
            <span>Confidence</span>
            <span className="font-semibold">{Math.round(score)}%</span>
          </div>
          <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
            <div
              className={`h-full rounded-full transition-all duration-500 ${progressBg}`}
              style={{ width: `${Math.min(100, Math.max(5, score))}%` }}
            />
          </div>
        </div>
      </div>

      {/* Comparison Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 my-4">
        {/* Lost Item */}
        <div className="p-4 rounded-xl bg-slate-50 border border-slate-100">
          <span className="text-xs font-bold uppercase tracking-wider text-rose-600">
            Lost Item
          </span>
          <h4 className="font-bold text-slate-900 mt-1 truncate">{lostItem.title}</h4>
          <p className="text-xs text-slate-500 mt-0.5">
            {lostItem.category} • Lost on {lostItem.dateLost}
          </p>
          <p className="text-xs text-slate-600 mt-1 truncate">
            📍 {lostItem.locationLost}
          </p>
          <Link
            to={`/lost-items/${lostItem.id}`}
            className="inline-flex items-center gap-1 text-xs text-brand-600 hover:text-brand-700 font-semibold mt-3"
          >
            View Lost Item Report <ChevronRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {/* Found Item */}
        <div className="p-4 rounded-xl bg-slate-50 border border-slate-100">
          <span className="text-xs font-bold uppercase tracking-wider text-emerald-600">
            Found Item
          </span>
          <h4 className="font-bold text-slate-900 mt-1 truncate">{foundItem.title}</h4>
          <p className="text-xs text-slate-500 mt-0.5">
            {foundItem.category} • Found on {foundItem.dateFound}
          </p>
          <p className="text-xs text-slate-600 mt-1 truncate">
            📍 {foundItem.locationFound}
          </p>
          <Link
            to={`/found-items/${foundItem.id}`}
            className="inline-flex items-center gap-1 text-xs text-brand-600 hover:text-brand-700 font-semibold mt-3"
          >
            View Found Item Report <ChevronRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      </div>

      {/* Match Reasons */}
      {matchReasons && matchReasons.length > 0 && (
        <div className="mt-4 pt-4 border-t border-slate-100">
          <h5 className="text-xs font-bold text-slate-700 uppercase tracking-wide mb-2">
            Why this matched
          </h5>
          <ul className="space-y-1.5">
            {matchReasons.map((reason, index) => (
              <li key={index} className="flex items-start gap-2 text-xs text-slate-600">
                <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0 mt-0.5" />
                <span>{reason}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Claim Button CTA */}
      <div className="mt-5 flex justify-end gap-3 pt-3 border-t border-slate-100">
        <Link
          to={`/found-items/${foundItem.id}`}
          className="inline-flex items-center gap-2 px-4 py-2 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-sm font-semibold shadow-sm transition-colors"
        >
          <ShieldCheck className="w-4 h-4" />
          <span>Claim Ownership for Found Item</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>
    </div>
  );
}