import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { 
  PlusCircle, ShieldCheck, AlertCircle, Upload, CheckCircle2, 
  ArrowLeft, Lock, Eye, EyeOff 
} from 'lucide-react';
import { foundItemService } from '../services/foundItemService';
import { useAuth } from '../context/AuthContext';

export const ReportFoundItemPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [formData, setFormData] = useState({
    title: '',
    category: 'ELECTRONICS',
    brand: '',
    primaryColor: '',
    dateFound: new Date().toISOString().split('T')[0],
    locationFound: '',
    storageLocation: 'Campus Security Office',
    publicDescription: '',
    privateDetails: '',
  });

  const [selectedFiles, setSelectedFiles] = useState([]);
  const [previews, setPreviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    setSelectedFiles(files);

    const newPreviews = files.map((file) => URL.createObjectURL(file));
    setPreviews(newPreviews);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!formData.title.trim()) {
      setError('Please provide a name/title for the found item.');
      return;
    }
    if (!formData.locationFound.trim()) {
      setError('Please specify where you found the item.');
      return;
    }
    if (!formData.publicDescription.trim()) {
      setError('Please provide a public description.');
      return;
    }

    setLoading(true);
    try {
      const createdItem = await foundItemService.create(formData, selectedFiles);
      navigate(`/found-items/${createdItem.id}?created=true`);
    } catch (err) {
      console.error('Failed to report found item:', err);
      setError(
        err.response?.data?.message ||
          'Failed to report found item. Please check required fields and try again.'
      );
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      
      {/* Back Link */}
      <Link
        to="/found-items"
        className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back to Found Items Vault</span>
      </Link>

      {/* Header Banner */}
      <div className="bg-gradient-to-r from-emerald-900 via-emerald-950 to-slate-900 p-8 rounded-3xl text-white shadow-xl relative overflow-hidden">
        <div className="space-y-2 relative z-10">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-bold border border-emerald-400/30">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
            <span>Custodian Safekeeping Registry</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Turn In / Report a Found Item
          </h1>
          <p className="text-xs sm:text-sm text-slate-300 max-w-xl">
            Thank you for helping reunite someone with their lost property. Record public clues for browsing and seal secret identifying marks to verify genuine owners.
          </p>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-xs text-rose-700 flex items-center gap-2">
          <AlertCircle className="w-4 h-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Form Card */}
      <form onSubmit={handleSubmit} className="bg-white rounded-3xl border border-slate-200/90 shadow-sm p-6 sm:p-8 space-y-6">
        
        {/* Section 1: Item Core Details */}
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider pb-2 border-b border-slate-100 flex items-center gap-2">
            <span className="w-6 h-6 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center text-xs">1</span>
            <span>Found Item Information</span>
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            
            {/* Title */}
            <div className="sm:col-span-2">
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Item Title / Short Name <span className="text-rose-500">*</span>
              </label>
              <input
                type="text"
                required
                maxLength={150}
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                placeholder="e.g., Set of 3 Keys with Blue Carabiner"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

            {/* Category */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Category <span className="text-rose-500">*</span>
              </label>
              <select
                name="category"
                value={formData.category}
                onChange={handleInputChange}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none bg-white text-slate-800"
              >
                <option value="ELECTRONICS">Electronics (Phones, Laptops, Earbuds)</option>
                <option value="WALLETS">Wallets &amp; Purses</option>
                <option value="DOCUMENTS">Documents &amp; IDs</option>
                <option value="BAGS">Bags &amp; Backpacks</option>
                <option value="CLOTHING">Clothing &amp; Shoes</option>
                <option value="KEYS">Keys &amp; Keychains</option>
                <option value="ACCESSORIES">Jewelry &amp; Accessories</option>
                <option value="BOOKS">Books &amp; Notebooks</option>
                <option value="OTHERS">Other Property</option>
              </select>
            </div>

            {/* Brand */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Brand / Manufacturer
              </label>
              <input
                type="text"
                maxLength={100}
                name="brand"
                value={formData.brand}
                onChange={handleInputChange}
                placeholder="e.g., Yale, Apple, Casio, Anker"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

            {/* Primary Color */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Primary Color
              </label>
              <input
                type="text"
                maxLength={50}
                name="primaryColor"
                value={formData.primaryColor}
                onChange={handleInputChange}
                placeholder="e.g., Metallic Blue, Silver, Black"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

            {/* Storage Location */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Physical Storage / Custody Location
              </label>
              <input
                type="text"
                maxLength={200}
                name="storageLocation"
                value={formData.storageLocation}
                onChange={handleInputChange}
                placeholder="e.g., Campus Security Office - Safe Locker #4"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

          </div>
        </div>

        {/* Section 2: Date & Discovery Location */}
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider pb-2 border-b border-slate-100 flex items-center gap-2">
            <span className="w-6 h-6 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center text-xs">2</span>
            <span>Where &amp; When Was It Found?</span>
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            
            {/* Date Found */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Date Found <span className="text-rose-500">*</span>
              </label>
              <input
                type="date"
                required
                max={new Date().toISOString().split('T')[0]}
                name="dateFound"
                value={formData.dateFound}
                onChange={handleInputChange}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

            {/* Location Found */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Location Found <span className="text-rose-500">*</span>
              </label>
              <input
                type="text"
                required
                maxLength={200}
                name="locationFound"
                value={formData.locationFound}
                onChange={handleInputChange}
                placeholder="e.g., Outside the Student Recreation Center main entrance benches"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

          </div>
        </div>

        {/* Section 3: Public vs Private Descriptions */}
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider pb-2 border-b border-slate-100 flex items-center gap-2">
            <span className="w-6 h-6 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center text-xs">3</span>
            <span>Public Description vs. Secret Anti-Fraud Clues</span>
          </h3>

          {/* Public Description */}
          <div>
            <div className="flex items-center gap-1.5 mb-1">
              <Eye className="w-3.5 h-3.5 text-slate-500" />
              <label className="text-xs font-bold text-slate-700">
                Public Description (Visible to Everyone) <span className="text-rose-500">*</span>
              </label>
            </div>
            <textarea
              required
              rows={3}
              name="publicDescription"
              value={formData.publicDescription}
              onChange={handleInputChange}
              placeholder="Describe general appearance without giving away private identifying clues (e.g., 'Found a bundle of three keys on a metallic blue carabiner outside the gym')."
              className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
            />
          </div>

          {/* Private Secret Clues */}
          <div className="p-4 bg-amber-50/70 border border-amber-200/80 rounded-2xl space-y-2">
            <div className="flex items-center gap-1.5 text-xs font-bold text-amber-900">
              <Lock className="w-3.5 h-3.5 text-amber-600" />
              <span>Private Details / Secret Clues (SEALED FROM PUBLIC)</span>
            </div>
            <p className="text-[11px] text-amber-800 leading-relaxed">
              These details are encrypted and masked from public searches. Only verified custodians or admins will view them to validate incoming ownership claims.
            </p>
            <textarea
              rows={3}
              name="privateDetails"
              value={formData.privateDetails}
              onChange={handleInputChange}
              placeholder="e.g., 'Key has a red rubber sleeve marked Room 302, student ID in pocket belongs to John Doe, small bike lock key'."
              className="w-full px-3 py-2 text-xs border border-amber-300 rounded-xl focus:ring-2 focus:ring-amber-500 focus:outline-none bg-white text-slate-900"
            />
          </div>

          {/* Photos Upload */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Item Photos (Optional)
            </label>
            <div className="border-2 border-dashed border-slate-300 rounded-2xl p-6 text-center hover:border-emerald-400 transition-colors bg-slate-50/50">
              <input
                type="file"
                multiple
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
                id="found-image-input"
              />
              <label
                htmlFor="found-image-input"
                className="cursor-pointer flex flex-col items-center justify-center gap-2"
              >
                <div className="w-10 h-10 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center">
                  <Upload className="w-5 h-5" />
                </div>
                <span className="text-xs font-semibold text-slate-700">
                  Click to select photos or drag and drop
                </span>
                <span className="text-[10px] text-slate-400">PNG, JPG, or WEBP up to 5MB</span>
              </label>
            </div>

            {previews.length > 0 && (
              <div className="flex gap-2 mt-3 overflow-x-auto pb-2">
                {previews.map((src, i) => (
                  <div key={i} className="relative w-16 h-16 rounded-xl overflow-hidden border border-slate-200 flex-shrink-0">
                    <img src={src} alt="Preview" className="w-full h-full object-cover" />
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>

        {/* Submit Action Area */}
        <div className="pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
          <Link
            to="/found-items"
            className="px-4 py-2 border border-slate-300 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
          >
            Cancel
          </Link>
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold shadow-lg shadow-emerald-600/25 transition-all flex items-center gap-2"
          >
            {loading && <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />}
            <span>Safely Register Found Item</span>
          </button>
        </div>

      </form>

    </div>
  );
};
