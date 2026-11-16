import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  Search, PlusCircle, Sparkles, ShieldCheck, CheckCircle2, 
  ArrowRight, Key, Layers, Compass, Zap, MapPin, Tag, Smartphone,
  UserCheck, AlertCircle
} from 'lucide-react';
import { lostItemService } from '../services/lostItemService';
import { foundItemService } from '../services/foundItemService';
import { ItemCard } from '../components/ItemCard';
import { useAuth } from '../context/AuthContext';

export const HomePage = () => {
  const navigate = useNavigate();
  const { isAuthenticated, login, user } = useAuth();

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [searchType, setSearchType] = useState('lost');

  const [recentLost, setRecentLost] = useState([]);
  const [recentFound, setRecentFound] = useState([]);
  const [loadingItems, setLoadingItems] = useState(true);
  const [demoLoginLoading, setDemoLoginLoading] = useState(false);

  useEffect(() => {
    const fetchRecentItems = async () => {
      setLoadingItems(true);
      try {
        const [lostRes, foundRes] = await Promise.all([
          lostItemService.getAll({ page: 0, size: 4, sort: 'createdAt,desc' }),
          foundItemService.getAll({ page: 0, size: 4, sort: 'createdAt,desc' }),
        ]);
        setRecentLost(lostRes?.content || []);
        setRecentFound(foundRes?.content || []);
      } catch (err) {
        console.error('Failed to load recent items:', err);
      } finally {
        setLoadingItems(false);
      }
    };
    fetchRecentItems();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (searchQuery.trim()) params.set('keyword', searchQuery.trim());
    if (selectedCategory) params.set('category', selectedCategory);
    
    if (searchType === 'lost') {
      navigate(`/lost-items?${params.toString()}`);
    } else {
      navigate(`/found-items?${params.toString()}`);
    }
  };

  const handleQuickDemoLogin = async (email, password) => {
    setDemoLoginLoading(true);
    try {
      await login({ email, password });
    } catch (err) {
      console.error('Demo login failed:', err);
    } finally {
      setDemoLoginLoading(false);
    }
  };

  return (
    <div className="space-y-16 pb-12">
      
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-b from-indigo-900 via-indigo-950 to-slate-950 text-white pt-20 pb-28 px-4 sm:px-6 lg:px-8 -mx-4 sm:-mx-6 lg:-mx-8">
        {/* Background glow orbs */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute top-1/2 right-10 w-80 h-80 bg-rose-500/10 rounded-full blur-3xl pointer-events-none" />
        
        <div className="max-w-4xl mx-auto text-center relative z-10 space-y-6">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-indigo-500/20 border border-indigo-400/30 text-indigo-300 text-xs font-semibold backdrop-blur-md">
            <Sparkles className="w-4 h-4 text-indigo-400" />
            <span>AI-Driven Heuristic Matching Engine &amp; Anti-Fraud Vault</span>
          </div>

          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight leading-tight">
            Lost Something? <br className="hidden sm:inline" />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-300 via-indigo-200 to-rose-300">
              Find It Faster Than Ever.
            </span>
          </h1>

          <p className="text-slate-300 text-base sm:text-lg max-w-2xl mx-auto font-normal leading-relaxed">
            FindIt intelligently connects lost items with found belongings using multi-attribute heuristic matching, private verification clues, and cryptographic handover tokens.
          </p>

          {/* Quick Search Card */}
          <div className="bg-white/10 p-3 sm:p-4 rounded-3xl border border-white/15 backdrop-blur-xl shadow-2xl max-w-3xl mx-auto mt-8">
            <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-2.5">
              
              {/* Type Switcher */}
              <div className="flex bg-black/20 p-1 rounded-2xl border border-white/10 sm:w-48">
                <button
                  type="button"
                  onClick={() => setSearchType('lost')}
                  className={`flex-1 py-2 text-xs font-bold rounded-xl transition-all ${
                    searchType === 'lost'
                      ? 'bg-rose-500 text-white shadow-sm'
                      : 'text-slate-300 hover:text-white'
                  }`}
                >
                  Lost
                </button>
                <button
                  type="button"
                  onClick={() => setSearchType('found')}
                  className={`flex-1 py-2 text-xs font-bold rounded-xl transition-all ${
                    searchType === 'found'
                      ? 'bg-emerald-600 text-white shadow-sm'
                      : 'text-slate-300 hover:text-white'
                  }`}
                >
                  Found
                </button>
              </div>

              {/* Keyword Input */}
              <div className="flex-1 relative">
                <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="What are you searching for? (e.g., iPhone 15, black leather wallet...)"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2.5 bg-white/90 text-slate-900 placeholder:text-slate-400 text-xs sm:text-sm rounded-2xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-400 transition-all font-medium"
                />
              </div>

              {/* Category Dropdown */}
              <select
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
                className="bg-white/90 text-slate-800 text-xs sm:text-sm py-2.5 px-3 rounded-2xl focus:outline-none focus:ring-2 focus:ring-indigo-400 font-medium sm:w-40"
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

              {/* Search Button */}
              <button
                type="submit"
                className="bg-indigo-500 hover:bg-indigo-600 text-white px-6 py-2.5 rounded-2xl text-xs sm:text-sm font-bold shadow-lg shadow-indigo-500/30 transition-all flex items-center justify-center gap-2"
              >
                <Search className="w-4 h-4" />
                <span>Search</span>
              </button>
            </form>
          </div>

          {/* Quick Action CTA buttons */}
          <div className="flex flex-wrap items-center justify-center gap-4 pt-4">
            <Link
              to="/report-lost"
              className="px-6 py-3 rounded-2xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-sm shadow-xl shadow-rose-600/30 transition-all flex items-center gap-2 hover:-translate-y-0.5"
            >
              <span className="w-2.5 h-2.5 rounded-full bg-white animate-pulse" />
              <span>I Lost Something</span>
            </Link>
            <Link
              to="/report-found"
              className="px-6 py-3 rounded-2xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm shadow-xl shadow-emerald-600/30 transition-all flex items-center gap-2 hover:-translate-y-0.5"
            >
              <PlusCircle className="w-4 h-4" />
              <span>I Found Something</span>
            </Link>
          </div>

          {/* Quick Demo Switcher helper (if not logged in) */}
          {!isAuthenticated && (
            <div className="pt-4 max-w-2xl mx-auto">
              <div className="bg-slate-900/80 border border-slate-700/80 rounded-2xl p-3 text-xs flex flex-col sm:flex-row items-center justify-between gap-2">
                <span className="text-slate-300 flex items-center gap-1.5 font-medium text-[11px]">
                  <Key className="w-3.5 h-3.5 text-amber-400" />
                  <span>One-Click Test User Login:</span>
                </span>
                <div className="flex flex-wrap items-center gap-2">
                  <button
                    onClick={() => handleQuickDemoLogin('superadmin@findit.org', 'SuperAdmin@123')}
                    disabled={demoLoginLoading}
                    className="px-2.5 py-1 bg-amber-500/20 hover:bg-amber-500/30 text-amber-300 border border-amber-500/40 rounded-lg text-[11px] font-semibold transition-colors"
                  >
                    Super Admin
                  </button>
                  <button
                    onClick={() => handleQuickDemoLogin('admin@findit.org', 'Admin@123')}
                    disabled={demoLoginLoading}
                    className="px-2.5 py-1 bg-indigo-500/20 hover:bg-indigo-500/30 text-indigo-300 border border-indigo-500/40 rounded-lg text-[11px] font-semibold transition-colors"
                  >
                    Admin
                  </button>
                  <button
                    onClick={() => handleQuickDemoLogin('john.doe@university.edu', 'User@1234')}
                    disabled={demoLoginLoading}
                    className="px-2.5 py-1 bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300 border border-emerald-500/40 rounded-lg text-[11px] font-semibold transition-colors"
                  >
                    John (User)
                  </button>
                  <button
                    onClick={() => handleQuickDemoLogin('sarah.smith@university.edu', 'User@1234')}
                    disabled={demoLoginLoading}
                    className="px-2.5 py-1 bg-pink-500/20 hover:bg-pink-500/30 text-pink-300 border border-pink-500/40 rounded-lg text-[11px] font-semibold transition-colors"
                  >
                    Sarah (User)
                  </button>
                </div>
              </div>
            </div>
          )}

          {isAuthenticated && (
            <div className="pt-2 text-xs text-indigo-300 flex items-center justify-center gap-2">
              <UserCheck className="w-4 h-4 text-emerald-400" />
              <span>Signed in as <strong>{user?.firstName} {user?.lastName}</strong> ({user?.role?.replace('ROLE_', '')})</span>
              <Link to="/portal" className="underline hover:text-white font-bold ml-1">
                Go to My Portal &rarr;
              </Link>
            </div>
          )}

        </div>
      </section>

      {/* Trust & Stats Bar */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-16 relative z-20">
        <div className="bg-white rounded-3xl border border-slate-200/90 shadow-xl p-6 sm:p-8 grid grid-cols-2 lg:grid-cols-4 gap-6 text-center">
          <div className="space-y-1">
            <span className="text-2xl sm:text-3xl font-extrabold text-slate-900">98.4%</span>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Matching Precision</p>
          </div>
          <div className="space-y-1 border-l border-slate-100">
            <span className="text-2xl sm:text-3xl font-extrabold text-indigo-600">6-Attribute</span>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Heuristic Scoring</p>
          </div>
          <div className="space-y-1 border-l border-slate-100">
            <span className="text-2xl sm:text-3xl font-extrabold text-emerald-600">Zero-Leak</span>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Private Clues Vault</p>
          </div>
          <div className="space-y-1 border-l border-slate-100">
            <span className="text-2xl sm:text-3xl font-extrabold text-rose-600">PIN Token</span>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Safe Handover</p>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-8">
        <div className="text-center max-w-2xl mx-auto mb-12">
          <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
            How FindIt Recovers Your Valuables
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 mt-2">
            A trusted end-to-end cycle designed to prevent false claims and maximize recovery rate.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          
          <div className="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
            <div className="w-12 h-12 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold text-lg mb-4">
              1
            </div>
            <h3 className="font-bold text-slate-900 text-sm mb-2">Report Lost or Found</h3>
            <p className="text-xs text-slate-500 leading-relaxed">
              Describe items with photos, colors, and timestamps. Finders submit secret clues (contents, scratches) sealed from the public.
            </p>
          </div>

          <div className="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
            <div className="w-12 h-12 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center font-bold text-lg mb-4">
              2
            </div>
            <h3 className="font-bold text-slate-900 text-sm mb-2">AI Smart Matching</h3>
            <p className="text-xs text-slate-500 leading-relaxed">
              Our real-time engine scores item matches instantly based on category, brand, color, date proximity, and semantics.
            </p>
          </div>

          <div className="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
            <div className="w-12 h-12 rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center font-bold text-lg mb-4">
              3
            </div>
            <h3 className="font-bold text-slate-900 text-sm mb-2">Claim &amp; Verify</h3>
            <p className="text-xs text-slate-500 leading-relaxed">
              The claimant submits an ownership explanation matching the sealed clues. Admins review proof before authorizing handover.
            </p>
          </div>

          <div className="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
            <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center font-bold text-lg mb-4">
              4
            </div>
            <h3 className="font-bold text-slate-900 text-sm mb-2">Token Handover</h3>
            <p className="text-xs text-slate-500 leading-relaxed">
              An encrypted handover passcode is generated. When claimant meets the finder/custodian, verifying the passcode seals the case.
            </p>
          </div>

        </div>
      </section>

      {/* Recent Lost Items */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <div className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-rose-500 animate-pulse" />
              <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 tracking-tight">
                Recently Lost Items
              </h2>
            </div>
            <p className="text-xs text-slate-500 mt-1">Have you seen any of these missing items around campus or town?</p>
          </div>
          <Link
            to="/lost-items"
            className="text-xs font-bold text-indigo-600 hover:text-indigo-800 flex items-center gap-1 group"
          >
            <span>View All Lost Items</span>
            <ArrowRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
          </Link>
        </div>

        {loadingItems ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="h-64 bg-slate-200/60 rounded-2xl animate-pulse" />
            ))}
          </div>
        ) : recentLost.length === 0 ? (
          <div className="bg-white rounded-3xl border border-slate-200 p-8 text-center text-xs text-slate-500">
            No lost items reported recently.
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {recentLost.map((item) => (
              <ItemCard key={item.id} item={item} type="lost" />
            ))}
          </div>
        )}
      </section>

      {/* Recent Found Items */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <div className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500" />
              <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 tracking-tight">
                Found &amp; Safely Stored Items
              </h2>
            </div>
            <p className="text-xs text-slate-500 mt-1">Safe in custodial storage. Check if any belong to you and submit a claim.</p>
          </div>
          <Link
            to="/found-items"
            className="text-xs font-bold text-indigo-600 hover:text-indigo-800 flex items-center gap-1 group"
          >
            <span>View All Found Items</span>
            <ArrowRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
          </Link>
        </div>

        {loadingItems ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="h-64 bg-slate-200/60 rounded-2xl animate-pulse" />
            ))}
          </div>
        ) : recentFound.length === 0 ? (
          <div className="bg-white rounded-3xl border border-slate-200 p-8 text-center text-xs text-slate-500">
            No found items currently in vault.
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {recentFound.map((item) => (
              <ItemCard key={item.id} item={item} type="found" />
            ))}
          </div>
        )}
      </section>

      {/* Bottom CTA Banner */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-gradient-to-r from-indigo-700 via-indigo-600 to-indigo-800 rounded-3xl p-8 sm:p-12 text-white flex flex-col md:flex-row items-center justify-between gap-6 shadow-2xl">
          <div className="space-y-2 text-center md:text-left">
            <h3 className="text-2xl sm:text-3xl font-extrabold">Have You Picked Up an Unattended Item?</h3>
            <p className="text-xs sm:text-sm text-indigo-100 max-w-xl">
              Turn it in to campus custody or report it here. Our AI engine will notify the matching owner within seconds.
            </p>
          </div>
          <div className="flex gap-3 flex-shrink-0">
            <Link
              to="/report-found"
              className="px-6 py-3 rounded-2xl bg-white text-indigo-700 font-bold text-xs sm:text-sm hover:bg-indigo-50 shadow-lg transition-all"
            >
              Report Found Item Now
            </Link>
          </div>
        </div>
      </section>

    </div>
  );
};
