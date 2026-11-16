import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { X, ShieldAlert, CheckCircle2, Lock, Sparkles, HelpCircle } from 'lucide-react';
import { claimService } from '../services/claimService';
import { useAuth } from '../context/AuthContext';

export const ClaimModal = ({ foundItem, isOpen, onClose, onClaimSubmitted }) => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [ownershipExplanation, setOwnershipExplanation] = useState('');
  const [distinctiveCharacteristics, setDistinctiveCharacteristics] = useState('');
  const [supportingDocumentUrl, setSupportingDocumentUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  if (!isOpen) return null;

  if (!isAuthenticated) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
        <div className="bg-white rounded-2xl max-w-md w-full p-6 text-center shadow-2xl">
          <ShieldAlert className="w-12 h-12 text-amber-500 mx-auto mb-3" />
          <h3 className="text-lg font-bold text-slate-900 mb-2">Authentication Required</h3>
          <p className="text-sm text-slate-600 mb-6">
            You must be signed in to submit an ownership claim for this found item.
          </p>
          <div className="flex gap-3 justify-center">
            <button
              onClick={onClose}
              className="px-4 py-2 border border-slate-300 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100"
            >
              Cancel
            </button>
            <button
              onClick={() => {
                onClose();
                navigate('/login');
              }}
              className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold"
            >
              Sign In Now
            </button>
          </div>
        </div>
      </div>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!ownershipExplanation.trim() || !distinctiveCharacteristics.trim()) {
      setError('Please provide both ownership explanation and secret identifying characteristics.');
      return;
    }

    setLoading(true);
    try {
      await claimService.submitClaim({
        foundItemId: foundItem.id,
        ownershipExplanation: ownershipExplanation.trim(),
        distinctiveCharacteristics: distinctiveCharacteristics.trim(),
        supportingDocumentUrl: supportingDocumentUrl.trim() || null,
      });
      setSuccess(true);
      if (onClaimSubmitted) onClaimSubmitted();
    } catch (err) {
      console.error('Failed to submit claim:', err);
      setError(
        err.response?.data?.message ||
          'Failed to submit ownership claim. Please verify all details and try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-3xl max-w-lg w-full shadow-2xl border border-slate-100 overflow-hidden animate-in fade-in zoom-in-95 duration-150 my-8">
        
        {/* Header */}
        <div className="px-6 py-5 bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-indigo-500/20 border border-indigo-400/30 flex items-center justify-center text-indigo-300">
              <Lock className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-base text-white">File Ownership Claim</h3>
              <p className="text-xs text-indigo-200">Item Ref: {foundItem?.trackingNumber}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-full text-slate-400 hover:text-white hover:bg-white/10 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6">
          {success ? (
            <div className="text-center py-6">
              <div className="w-16 h-16 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle2 className="w-8 h-8" />
              </div>
              <h4 className="text-lg font-bold text-slate-900 mb-2">Claim Submitted Successfully!</h4>
              <p className="text-xs text-slate-600 mb-6 leading-relaxed max-w-md mx-auto">
                Your claim has been submitted to the administration team. Once verified against the finder's sealed notes, you will receive a secure Handover Recovery Code.
              </p>
              <div className="flex justify-center gap-3">
                <button
                  onClick={onClose}
                  className="px-4 py-2 border border-slate-200 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-50"
                >
                  Close
                </button>
                <button
                  onClick={() => {
                    onClose();
                    navigate('/portal?tab=claims');
                  }}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold"
                >
                  View My Claims
                </button>
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4">
              
              {/* Item Info Card */}
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200/80 flex items-center justify-between text-xs">
                <div>
                  <span className="font-semibold text-slate-900 block">{foundItem?.title}</span>
                  <span className="text-slate-500">Category: {foundItem?.category}</span>
                </div>
                <span className="text-[11px] font-mono px-2 py-1 bg-white border border-slate-200 rounded text-slate-700">
                  {foundItem?.trackingNumber}
                </span>
              </div>

              {/* Anti-fraud Info box */}
              <div className="p-3.5 bg-amber-50/80 border border-amber-200/80 rounded-xl flex items-start gap-3 text-xs text-amber-900">
                <ShieldAlert className="w-4 h-4 text-amber-600 flex-shrink-0 mt-0.5" />
                <p className="leading-relaxed">
                  <strong>Verification Notice:</strong> The finder submitted private details sealed in our vault. Please describe unique features (scratches, contents, lock screen, card names) that verify you as the true owner.
                </p>
              </div>

              {error && (
                <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700">
                  {error}
                </div>
              )}

              {/* Ownership Explanation */}
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  1. Explanation of Ownership <span className="text-rose-500">*</span>
                </label>
                <textarea
                  required
                  rows={3}
                  value={ownershipExplanation}
                  onChange={(e) => setOwnershipExplanation(e.target.value)}
                  placeholder="Explain when, where, and how you lost this item (e.g., 'Lost my black wallet on the 2nd floor library while studying for finals on Tuesday afternoon')."
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              {/* Secret Clues / Distinctive Characteristics */}
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  2. Distinctive Characteristics &amp; Secret Clues <span className="text-rose-500">*</span>
                </label>
                <textarea
                  required
                  rows={3}
                  value={distinctiveCharacteristics}
                  onChange={(e) => setDistinctiveCharacteristics(e.target.value)}
                  placeholder="Detail non-public clues (e.g., 'Student ID name John Doe #25BCS80034, 2 twenty dollar bills, blue metro pass inside zipper')."
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              {/* Supporting Document / Proof */}
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  3. Proof of Purchase or ID Link (Optional)
                </label>
                <input
                  type="text"
                  value={supportingDocumentUrl}
                  onChange={(e) => setSupportingDocumentUrl(e.target.value)}
                  placeholder="URL to photo, purchase invoice, serial number photo, or cloud link"
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              {/* Buttons */}
              <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={onClose}
                  disabled={loading}
                  className="px-4 py-2 border border-slate-300 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold shadow-md shadow-indigo-600/25 transition-all flex items-center gap-2"
                >
                  {loading && <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />}
                  <span>Submit Ownership Claim</span>
                </button>
              </div>
            </form>
          )}
        </div>

      </div>
    </div>
  );
};
