import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { 
  Search, Filter, PlusCircle, RotateCcw, ChevronLeft, ChevronRight, 
  MapPin, ShieldCheck, AlertCircle, Lock
} from 'lucide-react';
import { foundItemService } from '../services/foundItemService';
import { ItemCard } from '../components/ItemCard';

export const FoundItemsPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [query, setQuery] = useState(searchParams.get('keyword') || searchParams.get('query') || '');
  const [category, setCategory] = useState(searchParams.get('category') || '');
  const [location, setLocation] = useState(searchParams.get('location') || '');
  const [status, setStatus] = useState(searchParams.get('status') || '');
  const [page, setPage] = useState(0);

  const [items, setItems] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchFoundItems = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page,
        size: 12,
        sortBy: 'createdAt',
        sortDirection: 'DESC',
      };
      if (query.trim()) params.query = query.trim();
      if (category) params.category = category;
      if (location.trim()) params.location = location.trim();
      if (status) params.status = status;

      const res = await foundItemService.getAll(params);
      setItems(res?.content || []);
      setTotalPages(res?.totalPages || 0);
      setTotalElements(res?.totalElements || 0);
    } catch (err) {
      console.error('Failed to load found items:', err);
      setError('Could not retrieve found items. Please check connection.');
    } finally {
      setLoading(false);
    }
  }, [query, category, location, status, page]);

  useEffect(() => {
    fetchFoundItems();
  }, [fetchFoundItems]);

  const handleApplyFilter = (e) => {
    e.preventDefault();
    setPage(0);
    const newParams = new URLSearchParams();
    if (query.trim()) newParams.set('query', query.trim());
    if (category) newParams.set('category', category);
    if (location.trim()) newParams.set('location', location.trim());
    if (status) newParams.set('status', status);
    setSearchParams(newParams);
    fetchFoundItems();
  };

  const handleResetFilters = () => {
    setQuery('');
    setCategory('');
    setLocation('');
    setStatus('');
    setPage(0);
    setSearchParams(new URLSearchParams());
  };

  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-gradient-to-r from-emerald-900 via-emerald-950 to-slate-900 p-8 rounded-3xl text-white shadow-xl relative overflow-hidden">
        <div className="absolute right-0 top-0 w-80 h-80 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="space-y-2 relative z-10">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-bold border border-emerald-400/30">
            <ShieldCheck className="w-4 h-4 text-emerald-400" />
            <span>Secure Custodial Vault</span>
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight">Browse Found Items</h1>
          <p className="text-xs sm:text-sm text-slate-300 max-w-xl">
            Items turned in by good samaritans and campus security. Private secret details are masked until you verify ownership.
          </p>
        </div>

        <Link
          to="/report-found"
          className="relative z-10 px-5 py-3 rounded-2xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs sm:text-sm shadow-lg shadow-emerald-600/30 transition-all flex items-center gap-2 self-start sm:self-center flex-shrink-0"
        >
          <PlusCircle className="w-4 h-4" />
          <span>Report Found Item</span>
        </Link>
      </div>

      {/* Security notice pill */}
      <div className="p-3.5 bg-emerald-50/70 border border-emerald-200/70 rounded-2xl flex items-center gap-3 text-xs text-emerald-900">
        <Lock className="w-4 h-4 text-emerald-600 flex-shrink-0" />
        <span>
          <strong>Anti-Fraud Protection:</strong> Private characteristics (serial numbers, card names, internal markings) are concealed to ensure only genuine owners can claim items.
        </span>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white p-5 rounded-3xl border border-slate-200/90 shadow-sm space-y-4">
        <form onSubmit={handleApplyFilter} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          
          {/* Query */}
          <div className="relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Title, brand, notes..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="w-full pl-9 pr-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
            />
          </div>

          {/* Category */}
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none bg-white text-slate-700"
          >
            <option value="">All Categories</option>
            <option value="ELECTRONICS">Electronics</option>
            <option value="WALLETS">Wallets</option>
            <option value="DOCUMENTS">Documents</option>
            <option value="BAGS">Bags</option>
            <option value="CLOTHING">Clothing</option>
            <option value="KEYS">Keys</option>
            <option value="ACCESSORIES">Accessories</option>
            <option value="BOOKS">Books</option>
            <option value="OTHERS">Others</option>
          </select>

          {/* Location */}
          <div className="relative">
            <MapPin className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Location found..."
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              className="w-full pl-9 pr-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
            />
          </div>

          {/* Status */}
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none bg-white text-slate-700"
          >
            <option value="">All Statuses</option>
            <option value="ACTIVE">Active (In Vault)</option>
            <option value="PENDING_CLAIM">Claim Pending Review</option>
            <option value="CLAIMED">Claim Approved</option>
            <option value="RECOVERED">Recovered &amp; Closed</option>
          </select>

          {/* Action buttons */}
          <div className="flex gap-2">
            <button
              type="submit"
              className="flex-1 py-2 px-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1.5 shadow-sm"
            >
              <Filter className="w-3.5 h-3.5" />
              <span>Filter</span>
            </button>
            <button
              type="button"
              onClick={handleResetFilters}
              title="Reset Filters"
              className="p-2 border border-slate-300 hover:bg-slate-100 rounded-xl text-slate-600 transition-colors"
            >
              <RotateCcw className="w-3.5 h-3.5" />
            </button>
          </div>

        </form>

        <div className="flex items-center justify-between text-xs text-slate-500 pt-2 border-t border-slate-100">
          <span>Displaying {totalElements} found items in platform records</span>
          {(query || category || location || status) && (
            <button
              onClick={handleResetFilters}
              className="text-emerald-600 hover:underline font-semibold"
            >
              Clear all active filters
            </button>
          )}
        </div>
      </div>

      {/* Item Grid */}
      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-xs text-rose-700 flex items-center gap-2">
          <AlertCircle className="w-4 h-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {[1, 2, 3, 4, 5, 6, 7, 8].map((i) => (
            <div key={i} className="h-64 bg-slate-200/60 rounded-3xl animate-pulse" />
          ))}
        </div>
      ) : items.length === 0 ? (
        <div className="bg-white rounded-3xl border border-slate-200 p-12 text-center space-y-3">
          <div className="w-12 h-12 rounded-full bg-emerald-50 text-emerald-500 flex items-center justify-center mx-auto">
            <Search className="w-6 h-6" />
          </div>
          <h3 className="font-bold text-slate-900 text-base">No Found Items Match Your Criteria</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            Check back later as new found items are cataloged regularly, or submit a lost report to receive notifications when matched.
          </p>
          <div className="pt-2">
            <Link
              to="/report-lost"
              className="inline-flex items-center gap-1.5 px-4 py-2 bg-indigo-600 text-white rounded-xl text-xs font-bold"
            >
              <PlusCircle className="w-4 h-4" />
              <span>Report Lost Item</span>
            </Link>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {items.map((item) => (
            <ItemCard key={item.id} item={item} type="found" />
          ))}
        </div>
      )}

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 pt-6">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="p-2 border border-slate-300 rounded-xl text-slate-600 disabled:opacity-40 hover:bg-slate-100 transition-colors"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          
          <span className="text-xs font-semibold text-slate-700 px-3">
            Page {page + 1} of {totalPages}
          </span>

          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            className="p-2 border border-slate-300 rounded-xl text-slate-600 disabled:opacity-40 hover:bg-slate-100 transition-colors"
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      )}

    </div>
  );
};
