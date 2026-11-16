import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { Search, Filter, PlusCircle, ShieldCheck, X } from 'lucide-react';
import { foundItemService } from '../services/foundItemService';
import ItemCard from '../components/ItemCard';

const categories = [
  'ALL',
  'ELECTRONICS',
  'DOCUMENTS',
  'WALLETS',
  'BAGS',
  'KEYS',
  'CLOTHING',
  'ACCESSORIES',
  'BOOKS',
  'OTHERS',
];

export default function FoundItems() {
  const [searchParams] = useSearchParams();
  const [items, setItems] = useState([]);
  const [pageData, setPageData] = useState({ page: 0, totalPages: 1, totalElements: 0 });
  const [loading, setLoading] = useState(true);

  // Filter state
  const [search, setSearch] = useState(searchParams.get('search') || '');
  const [category, setCategory] = useState(searchParams.get('category') || 'ALL');
  const [status, setStatus] = useState(searchParams.get('status') || 'ALL');
  const [location, setLocation] = useState(searchParams.get('location') || '');
  const [sortBy, setSortBy] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [currentPage, setCurrentPage] = useState(0);

  const fetchItems = async () => {
    setLoading(true);
    try {
      const params = {
        page: currentPage,
        size: 9,
        sortBy,
        sortDir,
      };
      if (search) params.search = search;
      if (category && category !== 'ALL') params.category = category;
      if (status && status !== 'ALL') params.status = status;
      if (location) params.location = location;

      const res = await foundItemService.getAll(params);
      setItems(res.content || []);
      setPageData({
        page: res.number || 0,
        totalPages: res.totalPages || 1,
        totalElements: res.totalElements || 0,
      });
    } catch (err) {
      console.error('Failed to load found items:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItems();
  }, [category, status, sortBy, sortDir, currentPage]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setCurrentPage(0);
    fetchItems();
  };

  const handleResetFilters = () => {
    setSearch('');
    setCategory('ALL');
    setStatus('ALL');
    setLocation('');
    setCurrentPage(0);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <div className="flex items-center gap-2">
            <span className="w-3 h-3 rounded-full bg-emerald-500 animate-pulse"></span>
            <span className="text-xs font-bold uppercase tracking-wider text-emerald-600">Directory</span>
          </div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Found Items</h1>
          <p className="text-slate-500 text-xs mt-1">
            Browse discovered items waiting for their verified owners. Sensitive identifying details are protected.
          </p>
        </div>

        <Link
          to="/report-found"
          className="inline-flex items-center gap-1.5 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold shadow-md transition-colors self-start md:self-auto"
        >
          <PlusCircle className="w-4 h-4" />
          <span>Report a Found Item</span>
        </Link>
      </div>

      {/* Security notice */}
      <div className="bg-emerald-50/70 border border-emerald-200/80 rounded-2xl p-4 flex items-center gap-3 text-xs text-emerald-900">
        <ShieldCheck className="w-5 h-5 text-emerald-600 shrink-0" />
        <p>
          <strong>Privacy Safeguard:</strong> Identifying features (such as serial numbers, exact card contents, and private engravings) are kept strictly hidden. Claimants must prove these details during verification.
        </p>
      </div>

      {/* Filter Bar */}
      <div className="bg-white rounded-2xl border border-slate-200 p-4 shadow-sm space-y-4">
        <form onSubmit={handleSearchSubmit} className="flex flex-col md:flex-row gap-3">
          <div className="flex items-center gap-2 flex-1 px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl focus-within:ring-2 focus-within:ring-brand-500 focus-within:border-brand-500">
            <Search className="w-4 h-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search by keywords, title, or brand..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full bg-transparent border-none outline-none text-xs text-slate-800"
            />
          </div>

          <div className="flex items-center gap-2 flex-1 md:max-w-xs px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl focus-within:ring-2 focus-within:ring-brand-500 focus-within:border-brand-500">
            <Filter className="w-4 h-4 text-slate-400" />
            <input
              type="text"
              placeholder="Filter by location (e.g. Cafeteria)..."
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              className="w-full bg-transparent border-none outline-none text-xs text-slate-800"
            />
          </div>

          <button
            type="submit"
            className="px-5 py-2 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold transition-colors shadow-sm"
          >
            Apply Filters
          </button>

          {(search || category !== 'ALL' || status !== 'ALL' || location) && (
            <button
              type="button"
              onClick={handleResetFilters}
              className="px-3 py-2 text-slate-500 hover:text-slate-800 rounded-xl text-xs font-semibold flex items-center gap-1 border border-slate-200 hover:bg-slate-50"
            >
              <X className="w-3.5 h-3.5" />
              <span>Reset</span>
            </button>
          )}
        </form>

        {/* Categories Pills */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 text-xs">
          <span className="font-semibold text-slate-400 shrink-0">Category:</span>
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => {
                setCategory(cat);
                setCurrentPage(0);
              }}
              className={`px-3 py-1 rounded-full font-medium whitespace-nowrap transition-colors ${
                category === cat
                  ? 'bg-emerald-600 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {cat.replace(/_/g, ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Results Header */}
      <div className="flex items-center justify-between text-xs text-slate-500">
        <span>Showing {items.length} of {pageData.totalElements} found items</span>
        <div className="flex items-center gap-2">
          <span>Sort by:</span>
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="bg-transparent font-semibold text-slate-700 border-none outline-none cursor-pointer"
          >
            <option value="createdAt">Date Reported</option>
            <option value="dateFound">Date Found</option>
            <option value="title">Title</option>
          </select>
        </div>
      </div>

      {/* Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[1, 2, 3, 4, 5, 6].map((n) => (
            <div key={n} className="h-80 bg-slate-100 rounded-2xl animate-pulse"></div>
          ))}
        </div>
      ) : items.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {items.map((item) => (
            <ItemCard key={item.id} item={item} type="found" />
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-3xl border border-slate-200 p-12 text-center text-slate-500 space-y-3">
          <p className="text-base font-semibold text-slate-700">No found items matching your search</p>
          <p className="text-xs text-slate-500">Try adjusting your filters or check back later.</p>
        </div>
      )}

      {/* Pagination */}
      {pageData.totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 pt-6">
          <button
            onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
            disabled={currentPage === 0}
            className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 disabled:opacity-40"
          >
            Previous
          </button>
          <span className="text-xs text-slate-600 font-semibold px-2">
            Page {currentPage + 1} of {pageData.totalPages}
          </span>
          <button
            onClick={() => setCurrentPage((p) => Math.min(pageData.totalPages - 1, p + 1))}
            disabled={currentPage >= pageData.totalPages - 1}
            className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 disabled:opacity-40"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}