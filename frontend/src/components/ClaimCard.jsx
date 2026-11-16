import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, Key, Calendar, Clock, MapPin, CheckCircle, AlertCircle } from 'lucide-react';
import StatusBadge from './StatusBadge';
import { recoveryService } from '../services/recoveryService';

export default function ClaimCard({ claim, onUpdate }) {
  const [scheduling, setScheduling] = useState(false);
  const [scheduleData, setScheduleData] = useState({
    handoverDate: '',
    handoverLocation: '',
    notes: '',
  });
  const [confirming, setConfirming] = useState(false);
  const [recoveryCodeInput, setRecoveryCodeInput] = useState('');
  const [handoverNotes, setHandoverNotes] = useState('');
  const [actionError, setActionError] = useState('');
  const [loading, setLoading] = useState(false);

  const recoveryId = claim.recovery?.id || claim.recoveryId;
  const recoveryCode = claim.recovery?.recoveryCode || claim.recoveryCode;
  const recoveryStatus = claim.recovery?.status || (claim.status === 'APPROVED' ? 'INITIATED' : claim.status);
  const recoveryDate = claim.recovery?.handoverScheduledAt || claim.recovery?.handoverDate;
  const recoveryNotes = claim.recovery?.handoverNotes || claim.recovery?.notes;

  const handleScheduleSubmit = async (e) => {
    e.preventDefault();
    setActionError('');
    setLoading(true);
    try {
      await recoveryService.scheduleHandover(recoveryId, {
        scheduledAt: scheduleData.handoverDate,
        handoverLocation: scheduleData.handoverLocation,
        handoverNotes: scheduleData.notes,
      });
      setScheduling(false);
      if (onUpdate) onUpdate();
    } catch (err) {
      setActionError(err.response?.data?.message || 'Failed to schedule handover.');
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmSubmit = async (e) => {
    e.preventDefault();
    setActionError('');
    setLoading(true);
    try {
      await recoveryService.confirmHandover(recoveryId, {
        recoveryCode: recoveryCodeInput.trim(),
        handoverNotes: handoverNotes.trim(),
      });
      setConfirming(false);
      if (onUpdate) onUpdate();
    } catch (err) {
      setActionError(err.response?.data?.message || 'Invalid recovery code or confirmation error.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wide">
              Claim ID #{claim.id}
            </span>
            <StatusBadge status={claim.status} />
          </div>
          <h3 className="font-bold text-slate-900 text-lg">
            Item: {claim.foundItem?.title}
          </h3>
          <p className="text-xs text-slate-500">
            Category: {claim.foundItem?.category} • Found at: {claim.foundItem?.locationFound}
          </p>
        </div>

        <Link
          to={`/found-items/${claim.foundItemId}`}
          className="text-sm font-semibold text-brand-600 hover:text-brand-700 hover:underline"
        >
          View Item Details →
        </Link>
      </div>

      {/* Claim details */}
      <div className="my-4 space-y-3">
        <div>
          <span className="text-xs font-semibold text-slate-500 block uppercase">
            Ownership Explanation:
          </span>
          <p className="text-sm text-slate-800 bg-slate-50 p-3 rounded-xl border border-slate-100 mt-1">
            {claim.explanation}
          </p>
        </div>

        {claim.distinctiveCharacteristics && (
          <div>
            <span className="text-xs font-semibold text-slate-500 block uppercase">
              Distinctive Characteristics:
            </span>
            <p className="text-sm text-slate-800 bg-slate-50 p-3 rounded-xl border border-slate-100 mt-1">
              {claim.distinctiveCharacteristics}
            </p>
          </div>
        )}

        {claim.adminNotes && (
          <div className="p-3 rounded-xl bg-blue-50 border border-blue-200 text-blue-900 text-xs">
            <span className="font-bold block mb-1">Admin Decision Feedback:</span>
            {claim.adminNotes}
          </div>
        )}
      </div>

      {/* RECOVERY SECTION IF APPROVED */}
      {claim.status === 'APPROVED' && (recovery || recoveryCode) && (
        <div className="mt-5 p-5 bg-gradient-to-r from-emerald-50 to-teal-50 border border-emerald-200 rounded-2xl">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              <h4 className="font-bold text-emerald-900 text-sm">
                Secure Handover &amp; Recovery
              </h4>
            </div>
            <StatusBadge status={recoveryStatus} />
          </div>

          <div className="bg-white p-4 rounded-xl border border-emerald-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4">
            <div>
              <span className="text-xs font-bold text-slate-500 uppercase tracking-wide block">
                Verification Reference Code
              </span>
              <p className="text-xs text-slate-600 mt-0.5">
                Present this secret verification code at the time of physical handover:
              </p>
            </div>
            <div className="flex items-center gap-2 bg-emerald-100/70 border border-emerald-300 px-4 py-2 rounded-xl">
              <Key className="w-4 h-4 text-emerald-700" />
              <span className="font-mono font-extrabold text-lg text-emerald-900 tracking-wider">
                {recoveryCode}
              </span>
            </div>
          </div>

          {/* Schedule Details if scheduled */}
          {recoveryDate && (
            <div className="text-xs text-emerald-900 bg-emerald-100/50 p-3 rounded-xl border border-emerald-200 mb-4 space-y-1">
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-emerald-700" />
                <span><strong>Date & Time:</strong> {new Date(recoveryDate).toLocaleString()}</span>
              </div>
              {recoveryNotes && (
                <p className="mt-1 pl-6 text-slate-700"><strong>Instructions / Notes:</strong> {recoveryNotes}</p>
              )}
            </div>
          )}

          {actionError && (
            <div className="mb-3 p-3 bg-red-100 border border-red-300 rounded-xl text-red-700 text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{actionError}</span>
            </div>
          )}

          {/* Recovery Actions */}
          {recoveryStatus !== 'COMPLETED' ? (
            <div className="flex flex-wrap gap-3">
              {!recoveryDate && !scheduling && (
                <button
                  type="button"
                  onClick={() => setScheduling(true)}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition-colors"
                >
                  Schedule Handover Meeting
                </button>
              )}

              {!confirming && (
                <button
                  type="button"
                  onClick={() => setConfirming(true)}
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-semibold shadow-sm transition-colors"
                >
                  Confirm Physical Handover
                </button>
              )}
            </div>
          ) : (
            <div className="flex items-center gap-2 text-xs font-bold text-emerald-800 bg-emerald-100/70 p-3 rounded-xl">
              <CheckCircle className="w-4 h-4 text-emerald-600" />
              <span>Item successfully returned and recovered!</span>
            </div>
          )}

          {/* Schedule Form Modal/Inline */}
          {scheduling && (
            <form onSubmit={handleScheduleSubmit} className="mt-4 p-4 bg-white rounded-xl border border-indigo-200 space-y-3">
              <h5 className="font-bold text-slate-800 text-xs uppercase">Schedule Handover Meeting</h5>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Handover Date &amp; Time</label>
                <input
                  type="datetime-local"
                  required
                  value={scheduleData.handoverDate}
                  onChange={(e) => setScheduleData({ ...scheduleData, handoverDate: e.target.value })}
                  className="w-full text-xs p-2 border border-slate-300 rounded-lg focus:ring-1 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Meeting Location</label>
                <input
                  type="text"
                  required
                  placeholder="e.g., Campus Security Desk, Building A"
                  value={scheduleData.handoverLocation}
                  onChange={(e) => setScheduleData({ ...scheduleData, handoverLocation: e.target.value })}
                  className="w-full text-xs p-2 border border-slate-300 rounded-lg focus:ring-1 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Instructions / Notes</label>
                <input
                  type="text"
                  placeholder="Optional meeting details"
                  value={scheduleData.notes}
                  onChange={(e) => setScheduleData({ ...scheduleData, notes: e.target.value })}
                  className="w-full text-xs p-2 border border-slate-300 rounded-lg"
                />
              </div>
              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setScheduling(false)}
                  className="px-3 py-1.5 border border-slate-200 text-slate-600 text-xs rounded-lg hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="px-3 py-1.5 bg-indigo-600 text-white text-xs font-semibold rounded-lg hover:bg-indigo-700 disabled:opacity-50"
                >
                  {loading ? 'Saving...' : 'Save Schedule'}
                </button>
              </div>
            </form>
          )}

          {/* Confirm Form Inline */}
          {confirming && (
            <form onSubmit={handleConfirmSubmit} className="mt-4 p-4 bg-white rounded-xl border border-emerald-200 space-y-3">
              <h5 className="font-bold text-slate-800 text-xs uppercase">Confirm Handover with Code</h5>
              <p className="text-xs text-slate-600">
                Enter the secret recovery verification code to seal the handover and mark this item as RECOVERED.
              </p>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Recovery Code</label>
                <input
                  type="text"
                  required
                  placeholder="RCV-XXXX-XXXX"
                  value={recoveryCodeInput}
                  onChange={(e) => setRecoveryCodeInput(e.target.value)}
                  className="w-full text-xs font-mono p-2 border border-slate-300 rounded-lg focus:ring-1 focus:ring-emerald-500 uppercase"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Handover Notes</label>
                <input
                  type="text"
                  placeholder="e.g. Identity verified via student card, handed over safely"
                  value={handoverNotes}
                  onChange={(e) => setHandoverNotes(e.target.value)}
                  className="w-full text-xs p-2 border border-slate-300 rounded-lg"
                />
              </div>
              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setConfirming(false)}
                  className="px-3 py-1.5 border border-slate-200 text-slate-600 text-xs rounded-lg hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="px-3 py-1.5 bg-emerald-600 text-white text-xs font-semibold rounded-lg hover:bg-emerald-700 disabled:opacity-50"
                >
                  {loading ? 'Confirming...' : 'Confirm Handover & Complete'}
                </button>
              </div>
            </form>
          )}
        </div>
      )}
    </div>
  );
}