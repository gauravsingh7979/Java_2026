import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { PlusCircle, ShieldCheck, Sparkles, AlertCircle, ArrowLeft, Lock } from 'lucide-react';
import { foundItemService } from '../services/foundItemService';
import ImageUploader from '../components/ImageUploader';

const categories = [
  'ELECTRONICS',
  'DOCUMENTS',
  'WALLETS',
  'BAGS',
  'KEYS',
  'CLOTHING',
  'ACCESSORIES',
  'BOOKS',
  'OTHERS',
];

export default function ReportFound() {
  const navigate = useNavigate();
  const [images, setImages] = useState([]);
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      category: 'ELECTRONICS',
      contactPreference: 'IN_APP_ONLY',
      dateFound: new Date().toISOString().split('T')[0],
    },
  });

  const onSubmit = async (data) => {
    setServerError('');
    setLoading(true);
    try {
      const formData = new FormData();
      formData.append(
        'item',
        new Blob([JSON.stringify(data)], { type: 'application/json' })
      );
      images.forEach((file) => {
        formData.append('images', file);
      });

      const res = await foundItemService.create(formData);
      const createdItem = res.data;
      navigate(`/found-items/${createdItem.id}?created=true`);
    } catch (err) {
      setServerError(
        err.response?.data?.message || 'Failed to submit found item report. Please check the fields.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="mb-6">
        <button
          type="button"
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 mb-3"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Back</span>
        </button>
        <div className="flex items-center gap-2">
          <span className="w-3 h-3 rounded-full bg-emerald-500"></span>
          <span className="text-xs font-bold uppercase tracking-wider text-emerald-600">New Report</span>
        </div>
        <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight mt-1">
          Report a Found Item
        </h1>
        <p className="text-slate-500 text-xs mt-1">
          Thank you for being a good citizen! Provide public information to help identify the item, and keep sensitive clues private.
        </p>
      </div>

      {serverError && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-2xl text-red-700 text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 shrink-0" />
          <span>{serverError}</span>
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6 bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-sm">
        {/* Title */}
        <div>
          <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
            Item Name / Title *
          </label>
          <input
            type="text"
            {...register('title', { required: 'Item title is required', minLength: { value: 3, message: 'Minimum 3 characters' } })}
            placeholder="e.g., Apple AirPods Pro in White Case"
            className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
          />
          {errors.title && <p className="text-red-500 text-xs mt-1">{errors.title.message}</p>}
        </div>

        {/* Category & Date */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
              Category *
            </label>
            <select
              {...register('category', { required: 'Category is required' })}
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
            >
              {categories.map((c) => (
                <option key={c} value={c}>{c.replace(/_/g, ' ')}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
              Date Found *
            </label>
            <input
              type="date"
              {...register('dateFound', { required: 'Date found is required' })}
              max={new Date().toISOString().split('T')[0]}
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
            {errors.dateFound && <p className="text-red-500 text-xs mt-1">{errors.dateFound.message}</p>}
          </div>
        </div>

        {/* Brand & Color */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
              Brand / Make
            </label>
            <input
              type="text"
              {...register('brand')}
              placeholder="e.g., Apple, Samsung, Sony"
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
              Primary Color
            </label>
            <input
              type="text"
              {...register('color')}
              placeholder="e.g., White, Silver, Black"
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>
        </div>

        {/* Location Found */}
        <div>
          <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
            Location Found *
          </label>
          <input
            type="text"
            {...register('locationFound', { required: 'Location found is required' })}
            placeholder="e.g., Student Union Cafe, North Dining Hall Table #8"
            className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
          />
          {errors.locationFound && <p className="text-red-500 text-xs mt-1">{errors.locationFound.message}</p>}
        </div>

        {/* Public Description */}
        <div>
          <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
            Public Description (Visible to everyone) *
          </label>
          <textarea
            rows="3"
            {...register('description', { required: 'Public description is required' })}
            placeholder="General description: found under the bench, charging case in good condition..."
            className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
          ></textarea>
          {errors.description && <p className="text-red-500 text-xs mt-1">{errors.description.message}</p>}
        </div>

        {/* SENSITIVE PRIVATE DETAILS (MASKED FROM PUBLIC) */}
        <div className="p-4 rounded-2xl bg-amber-50/70 border border-amber-200 space-y-2">
          <div className="flex items-center gap-2">
            <Lock className="w-4 h-4 text-amber-600" />
            <label className="text-xs font-bold uppercase tracking-wider text-amber-900">
              Private Identifying Details (Confidential)
            </label>
          </div>
          <p className="text-[11px] text-amber-800">
            <strong>Security Rule:</strong> These details are <em>never displayed publicly</em>. They are securely saved for admin claim verification only, ensuring claimants must prove ownership. (e.g. Serial numbers, specific ID card numbers inside, exact cash amount, or private stickers).
          </p>
          <textarea
            rows="3"
            {...register('privateDetails')}
            placeholder="e.g. Serial # H6DF92K10, student card with ID # 984321 inside, $45 cash in wallet..."
            className="w-full px-4 py-2.5 rounded-xl border border-amber-300 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500 bg-white"
          ></textarea>
        </div>

        {/* Contact Preference */}
        <div>
          <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
            Contact Preference
          </label>
          <select
            {...register('contactPreference')}
            className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
          >
            <option value="IN_APP_ONLY">In-App Notifications &amp; Handover Only (Recommended)</option>
            <option value="EMAIL">Email &amp; In-App</option>
            <option value="PHONE">Phone &amp; In-App</option>
          </select>
        </div>

        {/* Photos Upload */}
        <div>
          <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-2">
            Upload Item Photos (Do not photograph sensitive serial/ID numbers)
          </label>
          <ImageUploader images={images} setImages={setImages} />
        </div>

        {/* Submit Button */}
        <div className="pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="px-5 py-2.5 rounded-xl border border-slate-300 text-xs font-bold text-slate-700 hover:bg-slate-50 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs rounded-xl shadow-lg shadow-emerald-600/30 transition-all disabled:opacity-50"
          >
            <Sparkles className="w-4 h-4" />
            <span>{loading ? 'Submitting & Matching...' : 'Submit Report & Run Matcher'}</span>
          </button>
        </div>
      </form>
    </div>
  );
}