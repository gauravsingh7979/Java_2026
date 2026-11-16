import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import {
  MapPin,
  Calendar,
  Tag,
  ShieldCheck,
  Sparkles,
  ArrowLeft,
  AlertCircle,
  Lock,
  User,
  CheckCircle2,
  FileCheck,
} from 'lucide-react';
import { lostItemService } from '../services/lostItemService';
import { foundItemService } from '../services/foundItemService';
import { matchService } from '../services/matchService';
import { claimService } from '../services/claimService';
import { useAuth } from '../context/AuthContext';
import StatusBadge from '../components/StatusBadge';
import MatchCard from '../components/MatchCard';

export default function ItemDetails() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { user, isAuthenticated, isAdmin } = useAuth();

  const isLostRoute = location.pathname.includes('/lost-items');
  const [item, setItem] = useState(null);
  const [matches, setMatches] = useState([]);
  const [selectedImage, setSelectedImage] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Claim submission state
  const [showClaimModal, setShowClaimModal] = useState(false);
  const [claimLoading, setClaimLoading] = useState(false);
  const [claimSuccess, setClaimSuccess] = useState(false);
  const [claimError, setClaimError] = useState('');

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors: claimErrors },
  } = useForm();

  const fetchItemAndMatches = async () => {
    setLoading(true);
    try {
      if (isLostRoute) {
        const itemData = await lostItemService.getById(id);
        setItem(itemData);
        setSelectedImage(itemData.primaryImageUrl || itemData.images?.[0]?.imageUrl || null);

        // Fetch matches if current user is owner or admin
        if (isAuthenticated) {
          try {
            const matchesData = await matchService.getLostItemMatches(id);
            setMatches(matchesData || []);
          } catch (mErr) {
            // Not owner or matches forbidden
          }
        }
      } else {
        const itemData = await foundItemService.getById(id);
        setItem(itemData);
        setSelectedImage(itemData.primaryImageUrl || itemData.images?.[0]?.imageUrl || null);

        if (isAuthenticated) {
          try {
            const matchesData = await matchService.getFoundItemMatches(id);
            setMatches(matchesData || []);
          } catch (mErr) {
            // ignore
          }
        }
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Item not found or could not be loaded.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItemAndMatches();
  }, [id, isLostRoute, isAuthenticated]);

  const onClaimSubmit = async (data) => {
    setClaimError('');
    setClaimLoading(true);
    try {
      await claimService.submitClaim({
        foundItemId: item.id,
        explanation: data.explanation,
        distinctiveCharacteristics: data.distinctiveCharacteristics,
        supportingEvidence: data.supportingEvidence || '',
      });
      setClaimSuccess(true);
      reset();
      setTimeout(() => {
        setShowClaimModal(false);
        navigate('/claims');
      }, 1500);
    } catch (err) {
      setClaimError(err.response?.data?.message || 'Failed to submit ownership claim.');
    } finally {
      setClaimLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-16 text-center text-slate-400">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-brand-600 mx-auto mb-3"></div>
        <p className="text-sm">Loading item details...</p>
      </div>
    );
  }

  if (error || !item) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-16 text-center">
        <AlertCircle className="w-12 h-12 text-rose-500 mx-auto mb-3" />
        <h2 className="text-xl font-bold text-slate-900 mb-1">Item Not Found</h2>
        <p className="text-xs text-slate-500 mb-4">{error || 'This report does not exist or has been removed.'}</p>
        <Link
          to={isLostRoute ? '/lost-items' : '/found-items'}
          className="inline-block px-4 py-2 bg-brand-600 text-white rounded-xl text-xs font-bold"
        >
          Back to Directory
        </Link>
      </div>
    );
  }

  const isOwner = user && user.id === item.userId;
  const canClaim = !isLostRoute && isAuthenticated && !isOwner && item.status !== 'RECOVERED' && item.status !== 'CLOSED';
  const dateValue = isLostRoute ? item.dateLost : item.dateFound;
  const locationValue = isLostRoute ? item.locationLost : item.locationFound;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Back button */}
      <button
        onClick={() => navigate(-1)}
        className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800"
      >
        <ArrowLeft className="w-3.5 h-3.5" />
        <span>Back to listings</span>
      </button>

      {/* Main Item Detail Card */}
      <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-2">
          {/* Left: Images */}
          <div className="p-6 bg-slate-50 border-r border-slate-100 flex flex-col justify-between">
            <div className="aspect-square rounded-2xl overflow-hidden bg-slate-200 border border-slate-200 mb-4 flex items-center justify-center">
              {selectedImage ? (
                <img
                  src={selectedImage}
                  alt={item.title}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="text-center text-slate-400 p-6">
                  <Tag className="w-16 h-16 mx-auto mb-2 opacity-30" />
                  <p className="text-xs font-medium">No photo provided</p>
                </div>
              )}
            </div>

            {/* Image Thumbnails */}
            {item.images && item.images.length > 1 && (
              <div className="flex gap-2 overflow-x-auto pb-1">
                {item.images.map((img) => (
                  <button
                    key={img.id}
                    onClick={() => setSelectedImage(img.imageUrl)}
                    className={`w-14 h-14 rounded-lg overflow-hidden border-2 shrink-0 ${
                      selectedImage === img.imageUrl
                        ? 'border-brand-600 ring-2 ring-brand-500/30'
                        : 'border-slate-200 opacity-70 hover:opacity-100'
                    }`}
                  >
                    <img src={img.imageUrl} alt="" className="w-full h-full object-cover" />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Right: Info */}
          <div className="p-6 sm:p-8 flex flex-col justify-between space-y-6">
            <div>
              <div className="flex items-center justify-between gap-2 mb-2">
                <div className="flex items-center gap-2">
                  <span
                    className={`px-2.5 py-0.5 rounded-md text-xs font-bold uppercase tracking-wider ${
                      isLostRoute
                        ? 'bg-rose-100 text-rose-800 border border-rose-200'
                        : 'bg-emerald-100 text-emerald-800 border border-emerald-200'
                    }`}
                  >
                    {isLostRoute ? 'Lost Report' : 'Found Report'}
                  </span>
                  <StatusBadge status={item.status} />
                </div>
                <span className="text-xs text-slate-400 font-mono">#{item.id}</span>
              </div>

              <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 mt-2">
                {item.title}
              </h1>

              {/* Attributes badges */}
              <div className="flex flex-wrap items-center gap-2 mt-3">
                <span className="px-3 py-1 rounded-lg bg-slate-100 text-slate-700 text-xs font-semibold">
                  🏷️ {item.category?.replace(/_/g, ' ')}
                </span>
                {item.brand && (
                  <span className="px-3 py-1 rounded-lg bg-slate-100 text-slate-700 text-xs font-semibold">
                    Make: {item.brand}
                  </span>
                )}
                {item.color && (
                  <span className="px-3 py-1 rounded-lg bg-slate-100 text-slate-700 text-xs font-semibold">
                    Color: {item.color}
                  </span>
                )}
              </div>

              {/* Description */}
              <div className="mt-5 space-y-1">
                <span className="text-xs font-bold uppercase tracking-wider text-slate-500 block">
                  Description
                </span>
                <p className="text-sm text-slate-700 leading-relaxed whitespace-pre-line">
                  {item.description}
                </p>
              </div>

              {/* Distinctive characteristics for lost items */}
              {isLostRoute && item.distinctiveCharacteristics && (
                <div className="mt-4 p-3 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-700">
                  <span className="font-bold text-slate-900 block mb-1">
                    Distinctive Characteristics:
                  </span>
                  {item.distinctiveCharacteristics}
                </div>
              )}

              {/* PRIVATE DETAILS (VISIBLE ONLY TO FINDER OR ADMIN) */}
              {!isLostRoute && item.privateDetails && (
                <div className="mt-4 p-4 rounded-2xl bg-amber-50 border border-amber-300 text-xs space-y-1">
                  <div className="flex items-center gap-1.5 font-bold text-amber-900">
                    <Lock className="w-4 h-4 text-amber-600" />
                    <span>Confidential Item Clues (Visible only to you &amp; Admins)</span>
                  </div>
                  <p className="text-amber-800">{item.privateDetails}</p>
                </div>
              )}

              {/* Metadata */}
              <div className="mt-6 pt-5 border-t border-slate-100 space-y-2 text-xs text-slate-600">
                <div className="flex items-center gap-2">
                  <MapPin className="w-4 h-4 text-slate-400" />
                  <span><strong>{isLostRoute ? 'Lost at:' : 'Found at:'}</strong> {locationValue}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Calendar className="w-4 h-4 text-slate-400" />
                  <span><strong>Date:</strong> {dateValue}</span>
                </div>
                {item.contactPreference && (
                  <div className="flex items-center gap-2">
                    <User className="w-4 h-4 text-slate-400" />
                    <span><strong>Contact Preference:</strong> {item.contactPreference.replace(/_/g, ' ')}</span>
                  </div>
                )}
              </div>
            </div>

            {/* Action Bar */}
            <div className="pt-4 border-t border-slate-100 flex flex-wrap items-center gap-3">
              {canClaim && (
                <button
                  type="button"
                  onClick={() => setShowClaimModal(true)}
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold shadow-md shadow-emerald-600/30 transition-colors"
                >
                  <ShieldCheck className="w-4 h-4" />
                  <span>This is Mine! Claim Ownership</span>
                </button>
              )}

              {!isAuthenticated && !isLostRoute && (
                <Link
                  to="/login"
                  className="w-full sm:w-auto text-center px-6 py-3 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold"
                >
                  Sign in to Claim Item
                </Link>
              )}

              {isOwner && (
                <div className="text-xs text-slate-500 font-medium">
                  You are the author of this report.
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* SMART MATCHES SECTION */}
      {matches.length > 0 && (
        <div className="space-y-4">
          <div className="flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-purple-600" />
            <h2 className="text-xl font-bold text-slate-900">
              Potential Smart Matches ({matches.length})
            </h2>
          </div>
          <p className="text-xs text-slate-500">
            Our weighted heuristic engine detected these compatible reports. Review match confidence and reasons.
          </p>

          <div className="grid grid-cols-1 gap-4">
            {matches.map((m) => (
              <MatchCard key={m.id} match={m} userRole={user?.role} />
            ))}
          </div>
        </div>
      )}

      {/* CLAIM SUBMISSION MODAL */}
      {showClaimModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 sm:p-8 space-y-5 shadow-2xl animate-in fade-in">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-emerald-600" />
                <h3 className="font-extrabold text-slate-900 text-lg">
                  Submit Ownership Claim
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setShowClaimModal(false)}
                className="text-slate-400 hover:text-slate-600 text-lg font-bold"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-600">
              To prevent fraudulent claims, describe unpublicized distinctive characteristics or provide evidence (e.g., matching receipts, wallpaper details, student ID numbers). Admins review all claims before releasing handover codes.
            </p>

            {claimError && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-xs flex items-center gap-2">
                <AlertCircle className="w-4 h-4 shrink-0" />
                <span>{claimError}</span>
              </div>
            )}

            {claimSuccess ? (
              <div className="p-6 bg-emerald-50 rounded-2xl text-center space-y-2">
                <CheckCircle2 className="w-10 h-10 text-emerald-600 mx-auto" />
                <h4 className="font-bold text-emerald-900 text-sm">
                  Claim Submitted Successfully!
                </h4>
                <p className="text-xs text-emerald-700">
                  Redirecting to your claims tracker...
                </p>
              </div>
            ) : (
              <form onSubmit={handleSubmit(onClaimSubmit)} className="space-y-4">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
                    Explanation of Ownership *
                  </label>
                  <textarea
                    rows="3"
                    {...register('explanation', { required: 'Please explain how this item belongs to you' })}
                    placeholder="When did you lose it? What was inside? Why are you certain it is yours?"
                    className="w-full text-xs p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  ></textarea>
                  {claimErrors.explanation && (
                    <p className="text-red-500 text-[11px] mt-1">{claimErrors.explanation.message}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
                    Distinctive Characteristics *
                  </label>
                  <textarea
                    rows="2"
                    {...register('distinctiveCharacteristics', { required: 'Please specify distinctive characteristics' })}
                    placeholder="Private clues: engravings, passwords/passcode hint, cards inside, scratches..."
                    className="w-full text-xs p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  ></textarea>
                  {claimErrors.distinctiveCharacteristics && (
                    <p className="text-red-500 text-[11px] mt-1">{claimErrors.distinctiveCharacteristics.message}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
                    Supporting Evidence (Optional link/details)
                  </label>
                  <input
                    type="text"
                    {...register('supportingEvidence')}
                    placeholder="e.g., Purchase invoice number, photo proof link, IMEI last 4 digits"
                    className="w-full text-xs p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div className="pt-2 flex justify-end gap-2">
                  <button
                    type="button"
                    onClick={() => setShowClaimModal(false)}
                    className="px-4 py-2 border border-slate-200 text-slate-700 text-xs font-bold rounded-xl hover:bg-slate-50"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={claimLoading}
                    className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-md transition-colors disabled:opacity-50"
                  >
                    {claimLoading ? 'Submitting Claim...' : 'Submit Claim'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
}