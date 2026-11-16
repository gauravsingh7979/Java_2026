import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  PlusCircle,
  Sparkles,
  Layers,
  ShieldCheck,
  FileText,
  AlertCircle,
  Trash2,
  Edit,
  ExternalLink,
  ChevronRight,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { lostItemService } from '../services/lostItemService';
import { foundItemService } from '../services/foundItemService';
import StatusBadge from '../components/StatusBadge';
import MatchCard from '../components/MatchCard';

export default function Dashboard() {
  const { user } = useAuth();
  const [reports, setReports] = useState({ lostItems: [], foundItems: [] });
  const [activeTab, setActiveTab] = useState('lost');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchReports = async () => {
    try {
      setLoading(true);
      const data = await authService.getMyReports();
      setReports(data);
    } catch (err) {
      setError('Failed to load your reports.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, []);

  const handleDeleteLost = async (id) => {
    if (window.confirm('Are you sure you want to delete this lost item report?')) {
      try {
        await lostItemService.delete(id);
        fetchReports();
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete report.');
      }
    }
  };

  const handleDeleteFound = async (id) => {
    if (window.confirm('Are you sure you want to delete this found item report?')) {
      try {
        await foundItemService.delete(id);
        fetchReports();
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete report.');
      }
    }
  };

  const lostCount = reports.lostItems?.length || 0;
  const foundCount = reports.foundItems?.length || 0;
  const totalMatches =
    (reports.lostItems?.reduce((acc, i) => acc + (i.matchCount || 0), 0) || 0) +
    (reports.foundItems?.reduce((acc, i) => acc + (i.matchCount || 0), 0) || 0);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-brand-900 to-indigo-900 rounded-3xl p-6 sm:p-8 text-white flex flex-col md:flex-row items-start md:items-center justify-between gap-6 shadow-xl">
        <div className="space-y-2">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 text-brand-300 text-xs font-semibold backdrop-blur">
            <span>Logged in as {user?.role?.replace('ROLE_', '')}</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white">
            Hello, {user?.fullName || 'User'}!
          </h1>
          <p className="text-slate-300 text-sm max-w-xl">
            Welcome to your personal dashboard. Track your reported items, view algorithm-generated matches, and manage recovery handovers.
          </p>
        </div>

        <div className="flex flex-wrap gap-3">
          <Link
            to="/report-lost"
            className="inline-flex items-center gap-1.5 px-4 py-2.5 bg-rose-600 hover:bg-rose-500 text-white rounded-xl text-xs font-bold shadow-md transition-colors"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Report Lost</span>
          </Link>
          <Link
            to="/report-found"
            className="inline-flex items-center gap-1.5 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold shadow-md transition-colors"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Report Found</span>
          </Link>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-rose-50 text-rose-600 flex items-center justify-center">
            <FileText className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase">My Lost Reports</p>
            <p className="text-2xl font-black text-slate-900">{lostCount}</p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase">My Found Reports</p>
            <p className="text-2xl font-black text-slate-900">{foundCount}</p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center">
            <Sparkles className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase">Total Matches</p>
            <p className="text-2xl font-black text-slate-900">{totalMatches}</p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="border-b border-slate-200">
        <nav className="flex space-x-6">
          <button
            onClick={() => setActiveTab('lost')}
            className={`pb-4 text-sm font-bold border-b-2 transition-colors flex items-center gap-2 ${
              activeTab === 'lost'
                ? 'border-brand-600 text-brand-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <span>My Lost Items</span>
            <span className="px-2 py-0.5 rounded-full text-xs bg-slate-100 text-slate-700">
              {lostCount}
            </span>
          </button>

          <button
            onClick={() => setActiveTab('found')}
            className={`pb-4 text-sm font-bold border-b-2 transition-colors flex items-center gap-2 ${
              activeTab === 'found'
                ? 'border-brand-600 text-brand-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <span>My Found Items</span>
            <span className="px-2 py-0.5 rounded-full text-xs bg-slate-100 text-slate-700">
              {foundCount}
            </span>
          </button>
        </nav>
      </div>

      {/* Reports List */}
      {loading ? (
        <div className="py-12 text-center text-slate-400">Loading your reports...</div>
      ) : activeTab === 'lost' ? (
        <div className="space-y-4">
          {reports.lostItems && reports.lostItems.length > 0 ? (
            reports.lostItems.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4 hover:border-slate-300 transition-colors"
              >
                <div className="flex items-start gap-4">
                  <div className="w-16 h-16 rounded-xl bg-slate-100 shrink-0 overflow-hidden border border-slate-200">
                    {item.primaryImageUrl ? (
                      <img
                        src={item.primaryImageUrl}
                        alt={item.title}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-slate-400 text-xs">
                        No image
                      </div>
                    )}
                  </div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <StatusBadge status={item.status} />
                      <span className="text-xs font-semibold text-slate-500 uppercase">
                        {item.category}
                      </span>
                    </div>
                    <h3 className="font-bold text-slate-900 text-base">{item.title}</h3>
                    <p className="text-xs text-slate-500 mt-0.5">
                      Lost at {item.locationLost} on {item.dateLost}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2 flex-wrap md:flex-nowrap">
                  <Link
                    to={`/lost-items/${item.id}/matches`}
                    className="inline-flex items-center gap-1.5 px-3 py-2 bg-purple-50 text-purple-700 border border-purple-200 hover:bg-purple-100 rounded-xl text-xs font-bold transition-colors"
                  >
                    <Sparkles className="w-3.5 h-3.5" />
                    <span>View Matches ({item.matchCount || 0})</span>
                  </Link>

                  <Link
                    to={`/lost-items/${item.id}`}
                    className="inline-flex items-center gap-1 px-3 py-2 bg-slate-50 hover:bg-slate-100 text-slate-700 border border-slate-200 rounded-xl text-xs font-semibold transition-colors"
                  >
                    <ExternalLink className="w-3.5 h-3.5" />
                    <span>Details</span>
                  </Link>

                  <button
                    onClick={() => handleDeleteLost(item.id)}
                    className="p-2 text-rose-600 hover:bg-rose-50 rounded-xl transition-colors"
                    title="Delete report"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="bg-white rounded-2xl border border-slate-200 p-8 text-center text-slate-500 space-y-3">
              <p className="text-sm">You haven't reported any lost items yet.</p>
              <Link
                to="/report-lost"
                className="inline-flex items-center gap-1.5 px-4 py-2 bg-rose-600 text-white rounded-xl text-xs font-bold"
              >
                <PlusCircle className="w-4 h-4" />
                <span>Report Lost Item</span>
              </Link>
            </div>
          )}
        </div>
      ) : (
        <div className="space-y-4">
          {reports.foundItems && reports.foundItems.length > 0 ? (
            reports.foundItems.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4 hover:border-slate-300 transition-colors"
              >
                <div className="flex items-start gap-4">
                  <div className="w-16 h-16 rounded-xl bg-slate-100 shrink-0 overflow-hidden border border-slate-200">
                    {item.primaryImageUrl ? (
                      <img
                        src={item.primaryImageUrl}
                        alt={item.title}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-slate-400 text-xs">
                        No image
                      </div>
                    )}
                  </div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <StatusBadge status={item.status} />
                      <span className="text-xs font-semibold text-slate-500 uppercase">
                        {item.category}
                      </span>
                    </div>
                    <h3 className="font-bold text-slate-900 text-base">{item.title}</h3>
                    <p className="text-xs text-slate-500 mt-0.5">
                      Found at {item.locationFound} on {item.dateFound}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2 flex-wrap md:flex-nowrap">
                  <Link
                    to={`/found-items/${item.id}/matches`}
                    className="inline-flex items-center gap-1.5 px-3 py-2 bg-purple-50 text-purple-700 border border-purple-200 hover:bg-purple-100 rounded-xl text-xs font-bold transition-colors"
                  >
                    <Sparkles className="w-3.5 h-3.5" />
                    <span>View Matches ({item.matchCount || 0})</span>
                  </Link>

                  <Link
                    to={`/found-items/${item.id}`}
                    className="inline-flex items-center gap-1 px-3 py-2 bg-slate-50 hover:bg-slate-100 text-slate-700 border border-slate-200 rounded-xl text-xs font-semibold transition-colors"
                  >
                    <ExternalLink className="w-3.5 h-3.5" />
                    <span>Details</span>
                  </Link>

                  <button
                    onClick={() => handleDeleteFound(item.id)}
                    className="p-2 text-rose-600 hover:bg-rose-50 rounded-xl transition-colors"
                    title="Delete report"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="bg-white rounded-2xl border border-slate-200 p-8 text-center text-slate-500 space-y-3">
              <p className="text-sm">You haven't reported any found items yet.</p>
              <Link
                to="/report-found"
                className="inline-flex items-center gap-1.5 px-4 py-2 bg-emerald-600 text-white rounded-xl text-xs font-bold"
              >
                <PlusCircle className="w-4 h-4" />
                <span>Report Found Item</span>
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  );
}