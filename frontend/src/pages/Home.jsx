import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Search,
  PlusCircle,
  Sparkles,
  ShieldCheck,
  ArrowRight,
  MapPin,
  Clock,
  Layers,
  CheckCircle,
} from 'lucide-react';
import { lostItemService } from '../services/lostItemService';
import { foundItemService } from '../services/foundItemService';
import ItemCard from '../components/ItemCard';

const categories = [
  'ELECTRONICS',
  'DOCUMENTS',
  'WALLETS',
  'BAGS',
  'KEYS',
  'CLOTHING',
  'BOOKS',
  'OTHERS',
];

export default function Home() {
  const [keyword, setKeyword] = useState('');
  const [recentLost, setRecentLost] = useState([]);
  const [recentFound, setRecentFound] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRecent = async () => {
      try {
        const [lostRes, foundRes] = await Promise.all([
          lostItemService.getAll({ page: 0, size: 3 }),
          foundItemService.getAll({ page: 0, size: 3 }),
        ]);
        setRecentLost(lostRes.content || []);
        setRecentFound(foundRes.content || []);
      } catch (err) {
        console.error('Failed to load recent items:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchRecent();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (keyword.trim()) {
      navigate(`/lost-items?search=${encodeURIComponent(keyword.trim())}`);
    }
  };

  return (
    <div className="space-y-16 pb-16">
      {/* HERO SECTION */}
      <section className="relative overflow-hidden bg-gradient-to-b from-brand-900 via-navy-900 to-slate-900 text-white pt-20 pb-28 px-4 sm:px-6 lg:px-8">
        {/* Glow circles */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-brand-500/20 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute top-1/3 right-10 w-72 h-72 bg-indigo-500/15 rounded-full blur-2xl pointer-events-none" />

        <div className="relative max-w-5xl mx-auto text-center space-y-8">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-brand-500/10 border border-brand-400/20 text-brand-300 text-xs font-semibold uppercase tracking-wider backdrop-blur">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Intelligent Heuristic Matching Engine</span>
          </div>

          <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-white leading-tight">
            Lost something? <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-400 via-sky-300 to-indigo-300">
              Find it faster with FindIt.
            </span>
          </h1>

          <p className="max-w-2xl mx-auto text-slate-300 text-base sm:text-lg">
            A centralized platform for campuses, offices, and communities. Report lost or found items, let our weighted scoring match them automatically, and verify ownership securely.
          </p>

          {/* Search Box */}
          <form
            onSubmit={handleSearch}
            className="max-w-2xl mx-auto flex flex-col sm:flex-row items-center gap-2 p-2 bg-white/10 backdrop-blur-md rounded-2xl border border-white/20 shadow-2xl"
          >
            <div className="flex items-center gap-2 flex-1 w-full px-3 text-white">
              <Search className="w-5 h-5 text-slate-400" />
              <input
                type="text"
                placeholder="Search by item name, brand, or location..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                className="w-full bg-transparent border-none outline-none text-sm placeholder-slate-400 text-white"
              />
            </div>
            <button
              type="submit"
              className="w-full sm:w-auto px-6 py-3 bg-brand-600 hover:bg-brand-500 text-white font-semibold text-sm rounded-xl transition-colors shadow-md"
            >
              Search
            </button>
          </form>

          {/* Quick Categories */}
          <div className="flex flex-wrap items-center justify-center gap-2 pt-2">
            <span className="text-xs text-slate-400 mr-2">Popular:</span>
            {categories.slice(0, 5).map((cat) => (
              <Link
                key={cat}
                to={`/lost-items?category=${cat}`}
                className="text-xs px-3 py-1 rounded-full bg-white/5 hover:bg-white/10 border border-white/10 text-slate-300 hover:text-white transition-colors"
              >
                {cat.replace(/_/g, ' ')}
              </Link>
            ))}
          </div>

          {/* Action CTAs */}
          <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link
              to="/report-lost"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 bg-rose-600 hover:bg-rose-500 text-white font-bold text-sm rounded-xl shadow-lg shadow-rose-600/30 transition-all hover:-translate-y-0.5"
            >
              <PlusCircle className="w-4 h-4" />
              <span>I Lost Something</span>
            </Link>
            <Link
              to="/report-found"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-sm rounded-xl shadow-lg shadow-emerald-600/30 transition-all hover:-translate-y-0.5"
            >
              <PlusCircle className="w-4 h-4" />
              <span>I Found Something</span>
            </Link>
          </div>
        </div>
      </section>

      {/* THREE PILLARS / HOW IT WORKS */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-2xl mx-auto mb-12">
          <span className="text-xs font-bold uppercase tracking-wider text-brand-600">
            Engineered For Trust
          </span>
          <h2 className="text-3xl font-extrabold text-slate-900 mt-2">
            How FindIt Works
          </h2>
          <p className="text-slate-600 text-sm mt-2">
            From lost item report to secure physical handover in four seamless steps.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm relative group hover:border-brand-500 transition-colors">
            <div className="w-12 h-12 rounded-xl bg-brand-50 text-brand-600 flex items-center justify-center font-bold text-xl mb-4 group-hover:scale-110 transition-transform">
              1
            </div>
            <h3 className="font-bold text-slate-900 text-lg mb-2">
              Report With Details
            </h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              Describe your lost item or report what you found. Found item reports keep sensitive identifying details (serial numbers, wallet cash) hidden from the public.
            </p>
          </div>

          <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm relative group hover:border-purple-500 transition-colors">
            <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center font-bold text-xl mb-4 group-hover:scale-110 transition-transform">
              2
            </div>
            <h3 className="font-bold text-slate-900 text-lg mb-2">
              Weighted Smart Matching
            </h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              Our 100-point algorithm scores category, name, brand, color, location proximity, and date similarity, immediately notifying you when a strong match is discovered.
            </p>
          </div>

          <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm relative group hover:border-emerald-500 transition-colors">
            <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center font-bold text-xl mb-4 group-hover:scale-110 transition-transform">
              3
            </div>
            <h3 className="font-bold text-slate-900 text-lg mb-2">
              Verified Handover Code
            </h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              Submit an ownership claim with private proof. Once approved by admins, coordinate a meeting and complete handover using a secret recovery verification code.
            </p>
          </div>
        </div>
      </section>

      {/* RECENT LOST ITEMS */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Recently Lost Items</h2>
            <p className="text-slate-500 text-xs">Help fellow members recover their belongings</p>
          </div>
          <Link
            to="/lost-items"
            className="inline-flex items-center gap-1 text-sm font-semibold text-brand-600 hover:text-brand-700"
          >
            <span>View All</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[1, 2, 3].map((n) => (
              <div key={n} className="h-72 bg-slate-100 rounded-2xl animate-pulse"></div>
            ))}
          </div>
        ) : recentLost.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {recentLost.map((item) => (
              <ItemCard key={item.id} item={item} type="lost" />
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-2xl border border-slate-200 p-8 text-center text-slate-500">
            No lost items reported recently.
          </div>
        )}
      </section>

      {/* RECENT FOUND ITEMS */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Recently Found Items</h2>
            <p className="text-slate-500 text-xs">Browse items awaiting their rightful owners</p>
          </div>
          <Link
            to="/found-items"
            className="inline-flex items-center gap-1 text-sm font-semibold text-brand-600 hover:text-brand-700"
          >
            <span>View All</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[1, 2, 3].map((n) => (
              <div key={n} className="h-72 bg-slate-100 rounded-2xl animate-pulse"></div>
            ))}
          </div>
        ) : recentFound.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {recentFound.map((item) => (
              <ItemCard key={item.id} item={item} type="found" />
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-2xl border border-slate-200 p-8 text-center text-slate-500">
            No found items reported recently.
          </div>
        )}
      </section>
    </div>
  );
}