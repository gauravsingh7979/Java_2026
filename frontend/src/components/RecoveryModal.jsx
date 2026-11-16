import React, { useState } from 'react';
import { X, KeyRound, CheckCircle2, Calendar, MapPin, ShieldCheck, AlertCircle } from 'lucide-react';
import { recoveryService } from '../services/recoveryService';
import { useAuth } from '../context/AuthContext';

export const RecoveryModal = ({ recovery, isOpen, onClose, onUpdated }) => {
  const { user, isAdmin } = useAuth();
  const [scheduledAt, setScheduledAt] = useState(
    recovery?.handoverScheduledAt
      ? new Date(recovery.handoverScheduledAt).toISOString().slice(0, 16)
      : ''
  );
  const [scheduleNotes, setScheduleNotes] = useState(recovery?.handoverNotes || '');
  const [inputCode, setInputCode] = useState('');
  const [confirmNotes, setConfirmNotes] = useState('');

  const [loadingSchedule, setLoadingSchedule] = useState(false);
  const [loadingConfirm, setLoadingConfirm] = useState(false);
  const [error, setError] = useState(null);
  const [successMsg, setSuccessMsg] = useState(null);

  if (!isOpen || !recovery) return null;

  const isClaimant = user?.id === recovery.claimant?.id;
  const isFinder = user?.id === recovery.finder?.id || isAdmin;
  const isCompleted = recovery.status === 'COMPLETED';

  const handleSchedule = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);

    if (!scheduledAt) {
      setError('Please select a date and time for the handover.');
      return;
    }

    setLoadingSchedule(true);
    try {
      await recoveryService.scheduleHandover(recovery.id, {
        scheduledAt: new Date(scheduledAt).toISOString(),
        handoverNotes: scheduleNotes,
      });
      setSuccessMsg('Handover appointment successfully scheduled!');
      if (onUpdated) onUpdated();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to schedule handover.');
    } finally {
      setLoadingSchedule(false);
    }
  };

  const handleConfirm = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);

    if (!inputCode.trim()) {
      setError('Please enter the claimant verification code.');
      return;
    }

    setLoadingConfirm(true);
    try {
      await recoveryService.confirmHandover(recovery.id, {
        recoveryCode: inputCode.trim(),
        handoverNotes: confirmNotes,
      });
      setSuccessMsg('Handover verified successfully! Item marked as RECOVERED.');
      if (onUpdated) onUpdated();
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid recovery code or confirmation failed.');
    } finally {
      setLoadingConfirm(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-3xl max-w-xl w-full shadow-2xl border border-slate-100 overflow-hidden my-8 animate-in fade-in zoom-in-95 duration-150">
        
        {/* Header */}
        <div className="px-6 py-5 bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/20 border border-emerald-400/30 flex items-center justify-center text-emerald-400">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <div>
              <h3 className="font-bold text-base">Secure Item Handover</h3>
              <p className="text-xs text-slate-300">Transaction ID: #{recovery.id}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-full text-slate-400 hover:text-white hover:bg-white/10"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-5">
          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 flex items-center gap-2">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {successMsg && (
            <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-700 flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 flex-shrink-0" />
              <span>{successMsg}</span>
            </div>
          )}

          {/* Item & Parties Card */}
          <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200/80 space-y-2 text-xs">
            <div className="flex justify-between items-center">
              <span className="font-bold text-slate-900 text-sm">{recovery.item?.title}</span>
              <span
                className={`px-2.5 py-0.5 rounded-full font-bold uppercase tracking-wider text-[10px] ${
                  recovery.status === 'COMPLETED'
                    ? 'bg-emerald-100 text-emerald-800'
                    : 'bg-indigo-100 text-indigo-800'
                }`}
              >
                {recovery.status?.replace('_', ' ')}
              </span>
            </div>
            <div className="grid grid-cols-2 gap-2 pt-2 border-t border-slate-200 text-slate-600">
              <div>
                <span className="text-slate-400 block text-[10px]">Verified Claimant:</span>
                <span className="font-semibold text-slate-800">
                  {recovery.claimant?.firstName} {recovery.claimant?.lastName} ({recovery.claimant?.email})
                </span>
              </div>
              <div>
                <span className="text-slate-400 block text-[10px]">Item Custodian / Finder:</span>
                <span className="font-semibold text-slate-800">
                  {recovery.finder?.firstName} {recovery.finder?.lastName} ({recovery.finder?.email})
                </span>
              </div>
            </div>
          </div>

          {/* Verification Code Box (Visible to claimant and admin) */}
          {recovery.recoveryCode && (
            <div className="p-4 bg-gradient-to-br from-indigo-50 to-blue-50 border border-indigo-200 rounded-2xl text-center">
              <span className="text-xs font-bold text-indigo-900 block mb-1">
                SECRET VERIFICATION PASSCODE
              </span>
              <p className="text-2xl font-mono font-black tracking-widest text-indigo-700 my-1 selection:bg-indigo-200">
                {recovery.recoveryCode}
              </p>
              <p className="text-[11px] text-indigo-900/80">
                {isClaimant
                  ? 'Keep this passcode safe. Read or present it to the custodian when receiving your item.'
                  : 'Claimant will present this code in-person to verify receipt.'}
              </p>
            </div>
          )}

          {/* Scheduled Info / Scheduling Form */}
          {!isCompleted && (
            <div className="border border-slate-200 rounded-2xl p-4 space-y-3">
              <div className="flex items-center gap-2 text-xs font-bold text-slate-900">
                <Calendar className="w-4 h-4 text-indigo-600" />
                <span>Handover Schedule</span>
              </div>

              {recovery.handoverScheduledAt ? (
                <div className="bg-indigo-50/50 p-3 rounded-xl border border-indigo-100 text-xs text-indigo-900 space-y-1">
                  <p className="font-semibold">
                    Scheduled For:{' '}
                    {new Date(recovery.handoverScheduledAt).toLocaleString('en-US', {
                      dateStyle: 'medium',
                      timeStyle: 'short',
                    })}
                  </p>
                  {recovery.handoverNotes && (
                    <p className="text-slate-600 text-[11px]">Notes: {recovery.handoverNotes}</p>
                  )}
                </div>
              ) : (
                <p className="text-xs text-slate-500">No meeting scheduled yet.</p>
              )}

              <form onSubmit={handleSchedule} className="pt-2 space-y-3">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="block text-[11px] font-semibold text-slate-700 mb-1">
                      Meeting Date &amp; Time
                    </label>
                    <input
                      type="datetime-local"
                      required
                      value={scheduledAt}
                      onChange={(e) => setScheduledAt(e.target.value)}
                      className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] font-semibold text-slate-700 mb-1">
                      Location / Notes
                    </label>
                    <input
                      type="text"
                      value={scheduleNotes}
                      onChange={(e) => setScheduleNotes(e.target.value)}
                      placeholder="e.g., Campus Security Office, Desk 2"
                      className="w-full px-3 py-1.5 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500"
                    />
                  </div>
                </div>

                <div className="flex justify-end">
                  <button
                    type="submit"
                    disabled={loadingSchedule}
                    className="px-4 py-2 bg-slate-800 hover:bg-slate-900 text-white text-xs font-semibold rounded-xl transition-colors"
                  >
                    {loadingSchedule ? 'Saving...' : 'Set / Update Schedule'}
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Handover Confirmation (For Finder / Admin) */}
          {!isCompleted && isFinder && (
            <div className="border-2 border-emerald-500/30 bg-emerald-50/20 rounded-2xl p-4 space-y-3">
              <div className="flex items-center gap-2 text-xs font-bold text-emerald-900">
                <KeyRound className="w-4 h-4 text-emerald-600" />
                <span>Verify &amp; Finalize Handover</span>
              </div>
              <p className="text-xs text-slate-600">
                Enter the passcode given to you by the claimant to verify their identity and close the case.
              </p>

              <form onSubmit={handleConfirm} className="space-y-3">
                <div className="flex gap-3">
                  <input
                    type="text"
                    required
                    placeholder="Enter Passcode (e.g., RC-1234)"
                    value={inputCode}
                    onChange={(e) => setInputCode(e.target.value)}
                    className="flex-1 px-3 py-2 text-xs font-mono font-bold tracking-wider uppercase border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                  <button
                    type="submit"
                    disabled={loadingConfirm}
                    className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-md transition-colors"
                  >
                    {loadingConfirm ? 'Verifying...' : 'Confirm Handover'}
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Completed State */}
          {isCompleted && (
            <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl text-center space-y-1">
              <CheckCircle2 className="w-8 h-8 text-emerald-600 mx-auto" />
              <h5 className="font-bold text-emerald-900 text-sm">Item Successfully Recovered!</h5>
              <p className="text-xs text-emerald-700">
                Handover completed on{' '}
                {recovery.completedAt ? new Date(recovery.completedAt).toLocaleString() : 'N/A'}.
              </p>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-100 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 rounded-xl text-xs font-semibold transition-colors"
          >
            Close
          </button>
        </div>

      </div>
    </div>
  );
};
