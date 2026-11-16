import React, { useState, useEffect } from 'react';
import {
  Shield,
  Users,
  FileText,
  ShieldCheck,
  Sparkles,
  AlertCircle,
  CheckCircle,
  XCircle,
  RotateCw,
  Search,
  Lock,
  Layers,
  History,
  Check,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { claimService } from '../../services/claimService';
import { matchService } from '../../services/matchService';
import StatusBadge from '../../components/StatusBadge';
import { useAuth } from '../../context/AuthContext';

export default function AdminDashboard() {
  const { user, isSuperAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  const [stats, setStats] = useState(null);
  const [claims, setClaims] = useState([]);
  const [users, setUsers] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState({ text: '', type: '' });

  // Claim review modal state
  const [selectedClaim, setSelectedClaim] = useState(null);
  const [reviewNotes, setReviewNotes] = useState('');
  const [processingClaim, setProcessingClaim] = useState(false);

  // Recalculating state
  const [recalculating, setRecalculating] = useState(false);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const [statsData, claimsData, usersData, logsData] = await Promise.all([
        adminService.getDashboardStats(),
        adminService.getAllClaims({ page: 0, size: 20 }),
        adminService.getAllUsers({ page: 0, size: 20 }),
        adminService.getAuditLogs({ page: 0, size: 30 }),
      ]);
      setStats(statsData);
      setClaims(claimsData.content || []);
      setUsers(usersData.content || []);
      setAuditLogs(logsData.content || []);
    } catch (err) {
      setActionMsg({ text: 'Failed to load admin data.', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleApproveClaim = async (claimId) => {
    setProcessingClaim(true);
    try {
      await claimService.approveClaim(claimId, reviewNotes || 'Claim verified by administration.');
      setActionMsg({ text: `Claim #${claimId} approved! Secure recovery code generated.`, type: 'success' });
      setSelectedClaim(null);
      setReviewNotes('');
      fetchDashboardData();
    } catch (err) {
      setActionMsg({ text: err.response?.data?.message || 'Failed to approve claim.', type: 'error' });
    } finally {
      setProcessingClaim(false);
    }
  };

  const handleRejectClaim = async (claimId) => {
    if (!reviewNotes) {
      alert('Please provide a reason for rejecting this claim.');
      return;
    }
    setProcessingClaim(true);
    try {
      await claimService.rejectClaim(claimId, reviewNotes);
      setActionMsg({ text: `Claim #${claimId} rejected.`, type: 'success' });
      setSelectedClaim(null);
      setReviewNotes('');
      fetchDashboardData();
    } catch (err) {
      setActionMsg({ text: err.response?.data?.message || 'Failed to reject claim.', type: 'error' });
    } finally {
      setProcessingClaim(false);
    }
  };

  const handleToggleUser = async (userId) => {
    try {
      await adminService.toggleUserStatus(userId);
      setActionMsg({ text: 'User status updated successfully.', type: 'success' });
      fetchDashboardData();
    } catch (err) {
      setActionMsg({ text: 'Failed to update user status.', type: 'error' });
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      await adminService.updateUserRole(userId, newRole);
      setActionMsg({ text: `User role updated to ${newRole}.`, type: 'success' });
      fetchDashboardData();
    } catch (err) {
      setActionMsg({ text: 'Failed to update user role.', type: 'error' });
    }
  };

  const handleRecalculateMatches = async () => {
    setRecalculating(true);
    try {
      const res = await matchService.recalculateMatches();
      setActionMsg({ text: res.message || 'All matches recalculated successfully!', type: 'success' });
      fetchDashboardData();
    } catch (err) {
      setActionMsg({ text: 'Failed to recalculate matches.', type: 'error' });
    } finally {
      setRecalculating(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Top Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <div className="flex items-center gap-2">
            <Shield className="w-5 h-5 text-indigo-600" />
            <span className="text-xs font-bold uppercase tracking-wider text-indigo-600">
              Administration Portal
            </span>
          </div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight mt-1">
            Platform Management &amp; Moderation
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Signed in as {user?.fullName} ({user?.role?.replace('ROLE_', '')})
          </p>
        </div>

        <button
          onClick={handleRecalculateMatches}
          disabled={recalculating}
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold shadow-md transition-colors self-start sm:self-auto disabled:opacity-50"
        >
          <RotateCw className={`w-3.5 h-3.5 ${recalculating ? 'animate-spin' : ''}`} />
          <span>{recalculating ? 'Recalculating Engine...' : 'Run Global Match Re-Scoring'}</span>
        </button>
      </div>

      {actionMsg.text && (
        <div
          className={`p-4 rounded-2xl text-xs flex items-center justify-between gap-2 ${
            actionMsg.type === 'success'
              ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
              : 'bg-red-50 text-red-800 border border-red-200'
          }`}
        >
          <span>{actionMsg.text}</span>
          <button onClick={() => setActionMsg({ text: '', type: '' })} className="font-bold">
            ✕
          </button>
        </div>
      )}

      {/* KPI Cards */}
      {stats && (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
            <span className="text-[11px] font-bold text-slate-500 uppercase">Users</span>
            <p className="text-2xl font-black text-slate-900 mt-1">{stats.totalUsers ?? 0}</p>
          </div>
          <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
            <span className="text-[11px] font-bold text-rose-500 uppercase">Lost Items</span>
            <p className="text-2xl font-black text-slate-900 mt-1">{stats.totalLostReports ?? stats.totalLostItems ?? 0}</p>
          </div>
          <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
            <span className="text-[11px] font-bold text-emerald-500 uppercase">Found Items</span>
            <p className="text-2xl font-black text-slate-900 mt-1">{stats.totalFoundReports ?? stats.totalFoundItems ?? 0}</p>
          </div>
          <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
            <span className="text-[11px] font-bold text-indigo-500 uppercase">Recovered</span>
            <p className="text-2xl font-black text-slate-900 mt-1">{stats.completedRecoveries ?? stats.totalRecoveries ?? 0}</p>
          </div>
          <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
            <span className="text-[11px] font-bold text-amber-500 uppercase">Pending Claims</span>
            <p className="text-2xl font-black text-amber-600 mt-1">{stats.pendingClaims ?? 0}</p>
          </div>
          <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
            <span className="text-[11px] font-bold text-purple-500 uppercase">Total Matches</span>
            <p className="text-2xl font-black text-slate-900 mt-1">{stats.totalMatches ?? 0}</p>
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="border-b border-slate-200">
        <nav className="flex space-x-6">
          <button
            onClick={() => setActiveTab('overview')}
            className={`pb-4 text-sm font-bold border-b-2 transition-colors ${
              activeTab === 'overview'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            Overview &amp; Categories
          </button>
          <button
            onClick={() => setActiveTab('claims')}
            className={`pb-4 text-sm font-bold border-b-2 transition-colors flex items-center gap-1.5 ${
              activeTab === 'claims'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <span>Claims Moderation</span>
            {stats?.pendingClaims > 0 && (
              <span className="px-2 py-0.5 rounded-full text-xs bg-amber-100 text-amber-800 font-bold">
                {stats.pendingClaims}
              </span>
            )}
          </button>
          <button
            onClick={() => setActiveTab('users')}
            className={`pb-4 text-sm font-bold border-b-2 transition-colors ${
              activeTab === 'users'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            User Management
          </button>
          <button
            onClick={() => setActiveTab('audit')}
            className={`pb-4 text-sm font-bold border-b-2 transition-colors ${
              activeTab === 'audit'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            System Audit Trail
          </button>
        </nav>
      </div>

      {/* TAB CONTENT */}

      {/* 1. Overview */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Category Breakdown */}
            <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm col-span-1 md:col-span-2">
              <h3 className="font-bold text-slate-900 text-base mb-4">
                Total Reports by Category
              </h3>
              {(stats?.reportsByCategory || stats?.lostItemsByCategory) ? (
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                  {Object.entries(stats.reportsByCategory || stats.lostItemsByCategory).map(([cat, count]) => (
                    <div key={cat} className="p-3 bg-slate-50 border border-slate-200 rounded-xl flex items-center justify-between text-xs">
                      <span className="font-semibold text-slate-700">{cat.replace(/_/g, ' ')}</span>
                      <span className="px-2 py-0.5 rounded-full bg-brand-100 text-brand-800 font-bold">
                        {count}
                      </span>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-xs text-slate-400">No category statistics available.</p>
              )}
            </div>
          </div>
        </div>
      )}

      {/* 2. Claims Moderation */}
      {activeTab === 'claims' && (
        <div className="space-y-4">
          <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
            <div className="p-4 bg-slate-50 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-sm">
                Ownership Claims Queue
              </h3>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-100 text-slate-600 uppercase font-semibold">
                  <tr>
                    <th className="p-3.5">ID</th>
                    <th className="p-3.5">Found Item</th>
                    <th className="p-3.5">Claimant</th>
                    <th className="p-3.5">Status</th>
                    <th className="p-3.5">Submitted At</th>
                    <th className="p-3.5 text-right">Review Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 text-slate-700">
                  {claims.length > 0 ? (
                    claims.map((c) => (
                      <tr key={c.id} className="hover:bg-slate-50/70">
                        <td className="p-3.5 font-bold">#{c.id}</td>
                        <td className="p-3.5 font-medium">{c.foundItem?.title}</td>
                        <td className="p-3.5">{c.claimantName}</td>
                        <td className="p-3.5">
                          <StatusBadge status={c.status} />
                        </td>
                        <td className="p-3.5">{new Date(c.createdAt).toLocaleDateString()}</td>
                        <td className="p-3.5 text-right">
                          <button
                            onClick={() => setSelectedClaim(c)}
                            className="px-3 py-1.5 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 font-bold rounded-lg border border-indigo-200"
                          >
                            Inspect &amp; Decide
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="6" className="p-8 text-center text-slate-400">
                        No claims in queue.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* 3. User Management */}
      {activeTab === 'users' && (
        <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
          <div className="p-4 bg-slate-50 border-b border-slate-200">
            <h3 className="font-bold text-slate-800 text-sm">
              Community Members &amp; Roles
            </h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-100 text-slate-600 uppercase font-semibold">
                <tr>
                  <th className="p-3.5">Name</th>
                  <th className="p-3.5">Email</th>
                  <th className="p-3.5">Role</th>
                  <th className="p-3.5">Status</th>
                  <th className="p-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {users.map((u) => (
                  <tr key={u.id} className="hover:bg-slate-50/70">
                    <td className="p-3.5 font-bold">{u.fullName}</td>
                    <td className="p-3.5 text-slate-500">{u.email}</td>
                    <td className="p-3.5">
                      {isSuperAdmin && u.id !== user.id ? (
                        <select
                          value={u.role}
                          onChange={(e) => handleRoleChange(u.id, e.target.value)}
                          className="bg-slate-50 border border-slate-300 rounded p-1 font-semibold text-xs"
                        >
                          <option value="ROLE_USER">USER</option>
                          <option value="ROLE_ADMIN">ADMIN</option>
                          <option value="ROLE_SUPER_ADMIN">SUPER_ADMIN</option>
                        </select>
                      ) : (
                        <span className="font-semibold text-slate-700">{u.role}</span>
                      )}
                    </td>
                    <td className="p-3.5">
                      <span
                        className={`px-2 py-0.5 rounded-full font-bold ${
                          u.enabled
                            ? 'bg-emerald-100 text-emerald-800'
                            : 'bg-rose-100 text-rose-800'
                        }`}
                      >
                        {u.enabled ? 'Active' : 'Suspended'}
                      </span>
                    </td>
                    <td className="p-3.5 text-right">
                      {u.id !== user.id && (
                        <button
                          onClick={() => handleToggleUser(u.id)}
                          className={`px-3 py-1.5 rounded-lg font-bold border ${
                            u.enabled
                              ? 'text-rose-600 border-rose-200 hover:bg-rose-50'
                              : 'text-emerald-600 border-emerald-200 hover:bg-emerald-50'
                          }`}
                        >
                          {u.enabled ? 'Suspend' : 'Activate'}
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* 4. System Audit Trail */}
      {activeTab === 'audit' && (
        <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
          <div className="p-4 bg-slate-50 border-b border-slate-200 flex items-center gap-2">
            <History className="w-4 h-4 text-slate-500" />
            <h3 className="font-bold text-slate-800 text-sm">
              Security &amp; Operational Audit Trail
            </h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-100 text-slate-600 uppercase font-semibold">
                <tr>
                  <th className="p-3.5">Timestamp</th>
                  <th className="p-3.5">Actor</th>
                  <th className="p-3.5">Action</th>
                  <th className="p-3.5">Target</th>
                  <th className="p-3.5">Details</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {auditLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50/70">
                    <td className="p-3.5 font-mono text-slate-500">
                      {new Date(log.timestamp).toLocaleString()}
                    </td>
                    <td className="p-3.5 font-bold text-slate-800">{log.actorEmail}</td>
                    <td className="p-3.5">
                      <span className="font-semibold px-2 py-0.5 rounded bg-slate-100 text-slate-700">
                        {log.action}
                      </span>
                    </td>
                    <td className="p-3.5 font-mono text-[11px] text-slate-500">
                      {log.entityType} #{log.entityId}
                    </td>
                    <td className="p-3.5 text-slate-600 max-w-xs truncate">{log.details}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* CLAIM DECISION MODAL */}
      {selectedClaim && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
          <div className="bg-white rounded-3xl max-w-2xl w-full p-6 sm:p-8 space-y-6 shadow-2xl">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-indigo-600" />
                <h3 className="font-extrabold text-slate-900 text-lg">
                  Claim Moderation: #{selectedClaim.id}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setSelectedClaim(null)}
                className="text-slate-400 hover:text-slate-600 text-lg font-bold"
              >
                ✕
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Claimant statement */}
              <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 space-y-2">
                <span className="text-xs font-bold uppercase text-brand-600 block">
                  Claimant Statement ({selectedClaim.claimantName})
                </span>
                <p className="text-xs text-slate-700"><strong>Explanation:</strong> {selectedClaim.explanation}</p>
                <p className="text-xs text-slate-700"><strong>Distinctive Characteristics:</strong> {selectedClaim.distinctiveCharacteristics}</p>
                {selectedClaim.supportingEvidence && (
                  <p className="text-xs text-slate-700"><strong>Evidence:</strong> {selectedClaim.supportingEvidence}</p>
                )}
              </div>

              {/* Found Item Confidential Details */}
              <div className="p-4 bg-amber-50 rounded-2xl border border-amber-200 space-y-2">
                <div className="flex items-center gap-1.5 text-amber-900 font-bold text-xs uppercase">
                  <Lock className="w-3.5 h-3.5 text-amber-600" />
                  <span>Confidential Found Item Data</span>
                </div>
                <p className="text-xs text-amber-900"><strong>Title:</strong> {selectedClaim.foundItem?.title}</p>
                <p className="text-xs text-amber-900"><strong>Category:</strong> {selectedClaim.foundItem?.category}</p>
                <p className="text-xs text-amber-900"><strong>Location:</strong> {selectedClaim.foundItem?.locationFound}</p>
                <p className="text-xs text-amber-900 font-medium bg-amber-100/70 p-2 rounded-lg">
                  <strong>Hidden Clues:</strong> {selectedClaim.foundItem?.privateDetails || 'None recorded'}
                </p>
              </div>
            </div>

            {/* Admin Decision Notes */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
                Admin Decision Notes / Reason *
              </label>
              <textarea
                rows="2"
                value={reviewNotes}
                onChange={(e) => setReviewNotes(e.target.value)}
                placeholder="Enter feedback or verification reason visible to claimant..."
                className="w-full text-xs p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500"
              ></textarea>
            </div>

            {/* Actions */}
            <div className="pt-2 border-t border-slate-100 flex items-center justify-between gap-3">
              <button
                type="button"
                onClick={() => setSelectedClaim(null)}
                className="px-4 py-2 border border-slate-200 text-slate-600 text-xs font-bold rounded-xl"
              >
                Close
              </button>
              <div className="flex gap-2">
                <button
                  type="button"
                  disabled={processingClaim}
                  onClick={() => handleRejectClaim(selectedClaim.id)}
                  className="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-xl shadow-sm disabled:opacity-50"
                >
                  Reject Claim
                </button>
                <button
                  type="button"
                  disabled={processingClaim}
                  onClick={() => handleApproveClaim(selectedClaim.id)}
                  className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-md disabled:opacity-50"
                >
                  Approve Claim &amp; Authorize Recovery
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}