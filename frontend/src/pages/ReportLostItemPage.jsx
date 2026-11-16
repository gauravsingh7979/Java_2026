import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { 
  PlusCircle, Sparkles, AlertCircle, Upload, CheckCircle2, 
  ArrowLeft, Image as ImageIcon, ShieldAlert 
} from 'lucide-react';
import { lostItemService } from '../services/lostItemService';
import { useAuth } from '../context/AuthContext';

export const ReportLostItemPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [formData, setFormData] = useState({
    title: '',
    category: 'ELECTRONICS',
    brand: '',
    primaryColor: '',
    dateLost: new Date().toISOString().split('T')[0],
    locationLost: '',
    description: '',
    distinctiveCharacteristics: '',
    contactPreference: 'IN_APP',
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
      setError('Please provide a title for the lost item.');
      return;
    }
    if (!formData.locationLost.trim()) {
      setError('Please specify where you believe you lost the item.');
      return;
    }
    if (!formData.description.trim()) {
      setError('Please provide a description of the item.');
      return;
    }

    setLoading(true);
    try {
      const createdItem = await lostItemService.create(formData, selectedFiles);
      // Navigate to the newly created report details to see matching engine results
      navigate(`/lost-items/${createdItem.id}?created=true`);
    } catch (err) {
      console.error('Failed to create lost report:', err);
      setError(
        err.response?.data?.message ||
          'Failed to submit lost item report. Please check required fields and try again.'
      );
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      
      {/* Back Link */}
      <Link
        to="/lost-items"
        className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back to Lost Directory</span>
      </Link>

      {/* Header Banner */}
      <div className="bg-gradient-to-r from-rose-900 via-rose-950 to-slate-900 p-8 rounded-3xl text-white shadow-xl relative overflow-hidden">
        <div className="space-y-2 relative z-10">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-rose-500/20 text-rose-300 text-xs font-bold border border-rose-400/30">
            <Sparkles className="w-3.5 h-3.5 text-rose-400" />
            <span>Instant Heuristic Matching Activated</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Report a Lost Belonging
          </h1>
          <p className="text-xs sm:text-sm text-slate-300 max-w-xl">
            Provide as many descriptive details as possible. Once saved, our engine scans all current and incoming found records to find candidate matches.
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
        
        {/* Section 1: Basic Identifiers */}
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider pb-2 border-b border-slate-100 flex items-center gap-2">
            <span className="w-6 h-6 rounded-lg bg-rose-50 text-rose-600 flex items-center justify-center text-xs">1</span>
            <span>Item Details</span>
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            
            {/* Title */}
            <div className="sm:col-span-2">
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Item Title / Headline <span className="text-rose-500">*</span>
              </label>
              <input
                type="text"
                required
                maxLength={150}
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                placeholder="e.g., Black Leather Tommy Hilfiger Bifold Wallet"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
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
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none bg-white text-slate-800"
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
                placeholder="e.g., Apple, Tommy Hilfiger, Sony, Samsonite"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
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
                placeholder="e.g., Black, Navy Blue, Silver"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>

            {/* Contact Preference */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Contact Preference <span className="text-rose-500">*</span>
              </label>
              <select
                name="contactPreference"
                value={formData.contactPreference}
                onChange={handleInputChange}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none bg-white text-slate-800"
              >
                <option value="IN_APP">In-App Notifications (Recommended)</option>
                <option value="EMAIL">Email Alert</option>
                <option value="PHONE">Phone Call / SMS</option>
              </select>
            </div>

          </div>
        </div>

        {/* Section 2: Time & Location */}
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider pb-2 border-b border-slate-100 flex items-center gap-2">
            <span className="w-6 h-6 rounded-lg bg-rose-50 text-rose-600 flex items-center justify-center text-xs">2</span>
            <span>When &amp; Where Was It Lost?</span>
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            
            {/* Date Lost */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Date Lost <span className="text-rose-500">*</span>
              </label>
              <input
                type="date"
                required
                max={new Date().toISOString().split('T')[0]}
                name="dateLost"
                value={formData.dateLost}
                onChange={handleInputChange}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>

            {/* Location Lost */}
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Estimated Location Lost <span className="text-rose-500">*</span>
              </label>
              <input
                type="text"
                required
                maxLength={200}
                name="locationLost"
                value={formData.locationLost}
                onChange={handleInputChange}
                placeholder="e.g., University Main Library 2nd Floor study cubicles"
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>

          </div>
        </div>

        {/* Section 3: Description & Characteristics */}
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider pb-2 border-b border-slate-100 flex items-center gap-2">
            <span className="w-6 h-6 rounded-lg bg-rose-50 text-rose-600 flex items-center justify-center text-xs">3</span>
            <span>Detailed Descriptions &amp; Identifiers</span>
          </h3>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              General Description <span className="text-rose-500">*</span>
            </label>
            <textarea
              required
              rows={3}
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Describe what happened and outline visible features (model, markings, general contents, condition)."
              className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Distinctive Characteristics / Secret Identifiers
            </label>
            <textarea
              rows={2}
              name="distinctiveCharacteristics"
              value={formData.distinctiveCharacteristics}
              onChange={handleInputChange}
              placeholder="Unique identifiers that prove your ownership: engraved initials, stickers, small scratches, specific cards inside."
              className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
            <span className="text-[10px] text-slate-400 mt-1 block">
              These details help our algorithm compute similarity scores against found item reports.
            </span>
          </div>

          {/* Photo Upload */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Upload Photos (Optional)
            </label>
            <div className="border-2 border-dashed border-slate-300 rounded-2xl p-6 text-center hover:border-rose-400 transition-colors bg-slate-50/50">
              <input
                type="file"
                multiple
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
                id="lost-image-input"
              />
              <label
                htmlFor="lost-image-input"
                className="cursor-pointer flex flex-col items-center justify-center gap-2"
              >
                <div className="w-10 h-10 rounded-full bg-rose-50 text-rose-600 flex items-center justify-center">
                  <Upload className="w-5 h-5" />
                </div>
                <span className="text-xs font-semibold text-slate-700">
                  Click to select images or drag and drop
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

        {/* Submit Area */}
        <div className="pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
          <Link
            to="/lost-items"
            className="px-4 py-2 border border-slate-300 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
          >
            Cancel
          </Link>
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold shadow-lg shadow-rose-600/25 transition-all flex items-center gap-2"
          >
            {loading && <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />}
            <span>File Report &amp; Trigger AI Match</span>
          </button>
        </div>

      </form>

    </div>
  );
};
