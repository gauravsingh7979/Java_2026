import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate, useSearchParams } from 'react-router-dom';
import { 
  ArrowLeft, MapPin, Calendar, Sparkles, Tag, ShieldAlert, 
  Trash2, User, Mail, Phone, CheckCircle2, AlertCircle, Share2
} from 'lucide-react';
import { lostItemService } from '../services/lostItemService';
import { matchService } from '../services/matchService';
import { MatchScoreCard } from '../components/MatchScoreCard';
import { ClaimModal } from '../components/ClaimModal';
import { useAuth } from '../context/AuthContext';

export const LostItemDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const isNewlyCreated = searchParams.get('created') === 'true';

  const { user, isAuthenticated, isAdmin } = useAuth();

  const [item, setItem] = useState(null);
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMatches, setLoadingMatches] = useState(false);
  const [error, setError] = useState(null);

  const [selectedClaimItem, setSelectedClaimItem] = useState(null);
  const [claimModalOpen, setClaimModalOpen] = useState(false);
  const [activeImageIndex, setActiveImageIndex] = useState(0);

  const fetchDetails = async () => {
    setLoading(true);
    try {
      const data = await lostItemService.getById(id);
      setItem(data);
    } catch (err) {
      console.error('Failed to load lost item:', err);
      setError('Item not found or failed to load.');
    } finally {
      setLoading(false);
    }
  };

  const fetchMatches = async () => {
    if (!isAuthenticated) return;
    setLoadingMatches(true);
    try {
      const res = await matchService.getLostItemMatches(id);
      setMatches(res || []);
    } catch (err) {
      console.error('Failed to load item matches:', err);
    } finally {
      setLoadingMatches(false);
    }
  };

  useEffect(() => {
    fetchDetails();
  }, [id]);

  useEffect(() => {
    if (item && isAuthenticated) {
      fetchMatches();
    }
  }, [item, isAuthenticated]);

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to cancel this lost item report?')) return;
    try {
      await lostItemService.delete(id);
      navigate('/lost-items');
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to delete report.');
    }
  };

  const handleClaimCandidate = (foundItem) => {
    setSelectedClaimItem(foundItem);
    setClaimModalOpen(true);
  };

  const handleDismissMatch = (matchId) => {
    setMatches((prev) => prev.filter((m) => m.id !== matchId));
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-rose-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !item) {
    return (
      <div className="max-w-2xl mx-auto py-16 px-4 text-center">
        <AlertCircle className="w-12 h-12 text-rose-500 mx-auto mb-3" />
        <h2 className="text-xl font-bold text-slate-900 mb-2">{error || 'Report Not Found'}</h2>
        <Link to="/lost-items" className="text-xs font-bold text-indigo-600 hover:underline">
          &larr; Back to Lost Items Directory
        </Link>
      </div>
    );
  }

  const isOwner = user?.id === item.userId;
  const canManage = isOwner || isAdmin;

  const images = item.images || [];
  const currentImage = images[activeImageIndex]?.imageUrl;
  const currentImageSrc = currentImage?.startsWith('http')
    ? currentImage
    : currentImage
    ? `http://localhost:8080${currentImage}`
    : null;

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      
      {/* Top Breadcrumb & Actions */}
      <div className="flex items-center justify-between">
        <Link
          to="/lost-items"
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Lost Directory</span>
        </Link>

        {canManage && (
          <button
            onClick={handleDelete}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-rose-600 hover:bg-rose-50 rounded-xl border border-rose-200 transition-colors"
          >
            <Trash2 className="w-3.5 h-3.5" />
            <span>Cancel Report</span>
          </button>
        )}
      </div>

      {isNewlyCreated && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-center gap-3 text-xs text-emerald-800">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
          <div>
            <strong>Report registered successfully!</strong> Tracking number #{item.trackingNumber}. Our smart matching engine has been triggered to check for matches.
          </div>
        </div>
      )}

      {/* Main Details Card */}
      <div className="bg-white rounded-3xl border border-slate-200/90 shadow-sm overflow-hidden grid grid-cols-1 md:grid-cols-12 gap-0">
        
        {/* Left Column: Image Gallery */}
        <div className="md:col-span-5 bg-slate-900 p-6 flex flex-col justify-between">
          <div className="relative aspect-square rounded-2xl overflow-hidden bg-slate-800 flex items-center justify-center">
            {currentImageSrc ? (
              <img
                src={currentImageSrc}
                alt={item.title}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="text-center text-slate-400 p-6">
                <Tag className="w-16 h-16 mx-auto stroke-1 text-slate-600 mb-2" />
                <span className="text-xs">No photo provided with report</span>
              </div>
            )}

            <div className="absolute top-3 left-3 flex gap-2">
              <span className="px-2.5 py-1 rounded-full text-[11px] font-bold uppercase bg-rose-600 text-white shadow">
                LOST
              </span>
              <span className="px-2.5 py-1 rounded-full text-[11px] font-bold uppercase bg-slate-900/80 text-white backdrop-blur-sm">
                {item.status}
              </span>
            </div>
          </div>

          {/* Thumbnail list */}
          {images.length > 1 && (
            <div className="flex gap-2 mt-4 overflow-x-auto pb-1">
              {images.map((img, idx) => (
                <button
                  key={img.id || idx}
                  onClick={() => setActiveImageIndex(idx)}
                  className={`w-14 h-14 rounded-xl overflow-hidden border-2 transition-all flex-shrink-0 ${
                    activeImageIndex === idx ? 'border-rose-500 scale-105' : 'border-transparent opacity-60'
                  }`}
                >
                  <img
                    src={img.imageUrl?.startsWith('http') ? img.imageUrl : `http://localhost:8080${img.imageUrl}`}
                    alt="thumb"
                    className="w-full h-full object-cover"
                  />
                </button>
              ))}
            </div>
          )}

          <div className="mt-4 pt-4 border-t border-slate-800 text-[11px] text-slate-400 flex justify-between items-center">
            <span>Tracking Ref:</span>
            <span className="font-mono text-white font-bold">{item.trackingNumber}</span>
          </div>
        </div>

        {/* Right Column: Item Information */}
        <div className="md:col-span-7 p-6 sm:p-8 flex flex-col justify-between space-y-6">
          <div className="space-y-4">
            
            {/* Badges */}
            <div className="flex items-center gap-2 flex-wrap">
              <span className="px-3 py-1 rounded-lg text-xs font-bold uppercase bg-slate-100 text-slate-700">
                {item.category}
              </span>
              {item.brand && (
                <span className="px-3 py-1 rounded-lg text-xs font-medium bg-slate-100 text-slate-700">
                  Brand: {item.brand}
                </span>
              )}
              {item.primaryColor && (
                <span className="px-3 py-1 rounded-lg text-xs font-medium bg-slate-100 text-slate-700">
                  Color: {item.primaryColor}
                </span>
              )}
            </div>

            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900">{item.title}</h1>

            {/* Time & Place metadata */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 p-4 bg-slate-50 rounded-2xl border border-slate-200/80 text-xs">
              <div className="flex items-center gap-2">
                <MapPin className="w-4 h-4 text-rose-500 flex-shrink-0" />
                <div>
                  <span className="text-slate-400 block text-[10px]">Location Lost:</span>
                  <span className="font-bold text-slate-800">{item.locationLost}</span>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-rose-500 flex-shrink-0" />
                <div>
                  <span className="text-slate-400 block text-[10px]">Date Lost:</span>
                  <span className="font-bold text-slate-800">
                    {new Date(item.dateLost).toLocaleDateString('en-US', {
                      dateStyle: 'long',
                    })}
                  </span>
                </div>
              </div>
            </div>

            {/* Description */}
            <div className="space-y-1">
              <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider">Description</h4>
              <p className="text-xs sm:text-sm text-slate-600 leading-relaxed whitespace-pre-wrap">
                {item.description}
              </p>
            </div>

            {/* Distinctive Identifiers */}
            {item.distinctiveCharacteristics && (
              <div className="space-y-1 p-3.5 bg-rose-50/50 rounded-2xl border border-rose-100">
                <h4 className="text-xs font-bold text-rose-900 flex items-center gap-1.5">
                  <ShieldAlert className="w-3.5 h-3.5 text-rose-600" />
                  <span>Distinctive Characteristics</span>
                </h4>
                <p className="text-xs text-rose-800/90 whitespace-pre-wrap">
                  {item.distinctiveCharacteristics}
                </p>
              </div>
            )}

            {/* Reporter Info */}
            <div className="pt-2 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <div className="flex items-center gap-2">
                <div className="w-7 h-7 rounded-full bg-slate-200 flex items-center justify-center font-bold text-slate-700 text-xs">
                  {item.reporterName ? item.reporterName[0] : 'U'}
                </div>
                <span>Reported by <strong>{item.reporterName || 'Community Member'}</strong></span>
              </div>
              <span>Preference: {item.contactPreference}</span>
            </div>

          </div>
        </div>

      </div>

      {/* Heuristic Matches Section */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-xl bg-indigo-600 text-white flex items-center justify-center">
              <Sparkles className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-900">AI Matching Engine Candidates</h2>
              <p className="text-xs text-slate-500">
                Potential found item candidates scored by category, date, and semantic proximity.
              </p>
            </div>
          </div>
          <span className="px-3 py-1 bg-indigo-50 text-indigo-700 font-bold text-xs rounded-full border border-indigo-200">
            {matches.length} {matches.length === 1 ? 'Candidate' : 'Candidates'}
          </span>
        </div>

        {!isAuthenticated ? (
          <div className="bg-white rounded-3xl border border-slate-200 p-8 text-center space-y-3">
            <Sparkles className="w-8 h-8 text-indigo-500 mx-auto" />
            <h3 className="font-bold text-slate-900 text-sm">Sign in to View AI Match Recommendations</h3>
            <p className="text-xs text-slate-500 max-w-sm mx-auto">
              Our algorithm continuously cross-references newly found items. Sign in to view confident match proposals.
            </p>
            <Link
              to="/login"
              className="inline-block px-5 py-2 bg-indigo-600 text-white font-bold rounded-xl text-xs"
            >
              Sign In Now
            </Link>
          </div>
        ) : loadingMatches ? (
          <div className="p-8 text-center text-xs text-slate-500">Scanning matching items...</div>
        ) : matches.length === 0 ? (
          <div className="bg-white rounded-3xl border border-slate-200 p-8 text-center space-y-2">
            <CheckCircle2 className="w-8 h-8 text-slate-300 mx-auto" />
            <h3 className="font-bold text-slate-800 text-sm">No Potential Matches Discovered Yet</h3>
            <p className="text-xs text-slate-500 max-w-sm mx-auto">
              When a matching item is turned into the custody vault, you will receive an in-app and email alert immediately.
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {matches.map((m) => (
              <MatchScoreCard
                key={m.id}
                match={m}
                onDismiss={handleDismissMatch}
                onClaim={handleClaimCandidate}
              />
            ))}
          </div>
        )}
      </div>

      {/* Claim Modal */}
      {selectedClaimItem && (
        <ClaimModal
          foundItem={selectedClaimItem}
          isOpen={claimModalOpen}
          onClose={() => setClaimModalOpen(false)}
          onClaimSubmitted={() => {
            fetchMatches();
          }}
        />
      )}

    </div>
  );
};
