import React from 'react';
import { Link } from 'react-router-dom';
import { MapPin, Calendar, Tag, ShieldCheck, Sparkles } from 'lucide-react';
import StatusBadge from './StatusBadge';

export default function ItemCard({ item, type = 'lost' }) {
  const isLost = type === 'lost';
  const detailUrl = isLost ? `/lost-items/${item.id}` : `/found-items/${item.id}`;
  const dateValue = isLost ? item.dateLost : item.dateFound;
  const locationValue = isLost ? item.locationLost : item.locationFound;

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-all duration-200 overflow-hidden flex flex-col group">
      {/* Image Preview */}
      <div className="relative aspect-video w-full bg-slate-100 overflow-hidden">
        {item.primaryImageUrl ? (
          <img
            src={item.primaryImageUrl}
            alt={item.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
        ) : (
          <div className="w-full h-full flex flex-col items-center justify-center text-slate-400 bg-gradient-to-br from-slate-100 to-slate-200">
            <Tag className="w-10 h-10 mb-1 opacity-40" />
            <span className="text-xs font-medium">No photo provided</span>
          </div>
        )}

        {/* Type tag pill */}
        <div className="absolute top-3 left-3 flex gap-2">
          <span
            className={`px-2.5 py-1 rounded-md text-xs font-bold tracking-wide uppercase shadow-sm ${
              isLost
                ? 'bg-rose-600 text-white'
                : 'bg-emerald-600 text-white'
            }`}
          >
            {isLost ? 'Lost Item' : 'Found Item'}
          </span>
        </div>

        {/* Status Badge */}
        <div className="absolute top-3 right-3">
          <StatusBadge status={item.status} />
        </div>

        {item.matchCount > 0 && (
          <div className="absolute bottom-3 right-3 bg-purple-600/95 backdrop-blur text-white px-2.5 py-1 rounded-full text-xs font-semibold flex items-center gap-1 shadow-sm">
            <Sparkles className="w-3.5 h-3.5" />
            <span>{item.matchCount} {item.matchCount === 1 ? 'match' : 'matches'}</span>
          </div>
        )}
      </div>

      {/* Content */}
      <div className="p-5 flex-1 flex flex-col">
        <div className="flex items-center gap-2 text-xs font-medium text-brand-600 mb-1">
          <span className="bg-brand-50 px-2 py-0.5 rounded text-brand-700 font-semibold">
            {item.category?.replace(/_/g, ' ')}
          </span>
          {item.brand && (
            <span className="text-slate-500">• {item.brand}</span>
          )}
          {item.color && (
            <span className="text-slate-500">• {item.color}</span>
          )}
        </div>

        <h3 className="font-bold text-slate-900 text-lg group-hover:text-brand-600 transition-colors line-clamp-1 mb-2">
          {item.title}
        </h3>

        <p className="text-slate-600 text-sm line-clamp-2 mb-4 flex-1">
          {item.description}
        </p>

        {/* Metadata Footer */}
        <div className="pt-3 border-t border-slate-100 text-xs text-slate-500 space-y-1.5 mb-4">
          <div className="flex items-center gap-1.5">
            <MapPin className="w-4 h-4 text-slate-400 shrink-0" />
            <span className="truncate">{locationValue}</span>
          </div>
          <div className="flex items-center gap-1.5">
            <Calendar className="w-4 h-4 text-slate-400 shrink-0" />
            <span>{isLost ? 'Lost on' : 'Found on'} {dateValue}</span>
          </div>
        </div>

        <Link
          to={detailUrl}
          className="w-full text-center py-2 px-4 rounded-xl font-medium text-sm border border-slate-200 hover:border-brand-500 hover:bg-brand-50 hover:text-brand-700 text-slate-700 transition-colors"
        >
          View Full Details
        </Link>
      </div>
    </div>
  );
}