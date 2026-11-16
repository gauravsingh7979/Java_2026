import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Bell,
  CheckCircle,
  Clock,
  Sparkles,
  ShieldCheck,
  CheckCheck,
  Trash2,
  AlertCircle,
} from 'lucide-react';
import { notificationService } from '../services/notificationService';
import { useNotifications } from '../context/NotificationContext';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { markAsRead, markAllAsRead, fetchUnreadCount } = useNotifications();

  const fetchList = async () => {
    setLoading(true);
    try {
      const data = await notificationService.getAll({ page: 0, size: 30 });
      setNotifications(data.content || []);
    } catch (err) {
      setError('Failed to load notifications.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchList();
  }, []);

  const handleMarkOne = async (id) => {
    await markAsRead(id);
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
    );
  };

  const handleMarkAll = async () => {
    await markAllAsRead();
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
  };

  const getIcon = (type) => {
    switch (type) {
      case 'MATCH_FOUND':
        return <Sparkles className="w-5 h-5 text-purple-600" />;
      case 'CLAIM_APPROVED':
        return <CheckCircle className="w-5 h-5 text-emerald-600" />;
      case 'CLAIM_REJECTED':
        return <AlertCircle className="w-5 h-5 text-rose-600" />;
      case 'RECOVERY_INITIATED':
      case 'HANDOVER_SCHEDULED':
      case 'ITEM_RECOVERED':
        return <ShieldCheck className="w-5 h-5 text-indigo-600" />;
      default:
        return <Bell className="w-5 h-5 text-brand-600" />;
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <div className="flex items-center gap-2">
            <Bell className="w-4 h-4 text-brand-600" />
            <span className="text-xs font-bold uppercase tracking-wider text-brand-600">
              Notification Center
            </span>
          </div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight mt-1">
            Notifications &amp; Activity
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Stay informed about smart matches, claim verifications, and handover updates.
          </p>
        </div>

        {notifications.some((n) => !n.isRead) && (
          <button
            onClick={handleMarkAll}
            className="inline-flex items-center gap-1.5 px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition-colors self-start sm:self-auto"
          >
            <CheckCheck className="w-4 h-4 text-brand-600" />
            <span>Mark all as read</span>
          </button>
        )}
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-2xl text-red-700 text-xs">
          {error}
        </div>
      )}

      {/* List */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-brand-600 mx-auto mb-2"></div>
          <p className="text-xs">Loading notifications...</p>
        </div>
      ) : notifications.length > 0 ? (
        <div className="space-y-3">
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`p-4 rounded-2xl border transition-all flex items-start gap-4 ${
                n.isRead
                  ? 'bg-white border-slate-200 opacity-80'
                  : 'bg-brand-50/40 border-brand-200 shadow-sm'
              }`}
            >
              <div className="p-2.5 rounded-xl bg-white border border-slate-200 shrink-0">
                {getIcon(n.type)}
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-2">
                  <h4 className="font-bold text-slate-900 text-sm truncate">{n.title}</h4>
                  <span className="text-[11px] text-slate-400 shrink-0">
                    {new Date(n.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-xs text-slate-600 mt-1 leading-relaxed">{n.message}</p>

                {/* Actions & Links */}
                <div className="mt-3 flex items-center gap-4 text-xs font-semibold">
                  {n.relatedEntityType === 'MATCH' && n.relatedEntityId && (
                    <Link
                      to={`/lost-items/${n.relatedEntityId}/matches`}
                      className="text-brand-600 hover:underline"
                    >
                      View Match Details →
                    </Link>
                  )}
                  {n.relatedEntityType === 'CLAIM' && (
                    <Link to="/claims" className="text-brand-600 hover:underline">
                      View My Claims →
                    </Link>
                  )}
                  {n.relatedEntityType === 'FOUND_ITEM' && n.relatedEntityId && (
                    <Link
                      to={`/found-items/${n.relatedEntityId}`}
                      className="text-brand-600 hover:underline"
                    >
                      View Found Item →
                    </Link>
                  )}

                  {!n.isRead && (
                    <button
                      onClick={() => handleMarkOne(n.id)}
                      className="text-slate-400 hover:text-slate-700 ml-auto"
                    >
                      Mark read
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-3xl border border-slate-200 p-12 text-center text-slate-500 space-y-2">
          <Bell className="w-12 h-12 text-slate-300 mx-auto mb-2" />
          <p className="text-base font-semibold text-slate-700">No notifications yet</p>
          <p className="text-xs text-slate-500">
            You'll receive alerts when items match your reports or your ownership claims are reviewed.
          </p>
        </div>
      )}
    </div>
  );
}