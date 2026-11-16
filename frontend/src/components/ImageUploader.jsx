import React, { useState } from 'react';
import { UploadCloud, X, Image as ImageIcon } from 'lucide-react';

export default function ImageUploader({ images, setImages, maxFiles = 5 }) {
  const [dragActive, setDragActive] = useState(false);

  const handleFiles = (files) => {
    const fileArray = Array.from(files);
    const validFiles = fileArray.filter((file) => {
      const isValid = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type);
      const isUnderLimit = file.size <= 5 * 1024 * 1024;
      return isValid && isUnderLimit;
    });

    const combined = [...images, ...validFiles].slice(0, maxFiles);
    setImages(combined);
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      handleFiles(e.dataTransfer.files);
    }
  };

  const handleRemove = (index) => {
    const updated = images.filter((_, i) => i !== index);
    setImages(updated);
  };

  return (
    <div className="space-y-4">
      <div
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
        className={`border-2 border-dashed rounded-xl p-6 text-center cursor-pointer transition-colors ${
          dragActive
            ? 'border-brand-500 bg-brand-50'
            : 'border-slate-300 hover:border-slate-400 bg-slate-50/50'
        }`}
      >
        <input
          type="file"
          id="file-upload"
          multiple
          accept="image/jpeg,image/png,image/webp"
          className="hidden"
          onChange={(e) => handleFiles(e.target.files)}
        />
        <label htmlFor="file-upload" className="cursor-pointer block">
          <UploadCloud className="mx-auto h-10 w-10 text-slate-400 mb-2" />
          <p className="text-sm font-semibold text-slate-700">
            Click to upload or drag and drop photos
          </p>
          <p className="text-xs text-slate-500 mt-1">
            PNG, JPG, or WEBP up to 5MB each (max {maxFiles} images)
          </p>
        </label>
      </div>

      {images.length > 0 && (
        <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-5 gap-3">
          {images.map((file, idx) => {
            const previewUrl = URL.createObjectURL(file);
            return (
              <div key={idx} className="relative group rounded-lg overflow-hidden border border-slate-200 aspect-square bg-slate-100">
                <img
                  src={previewUrl}
                  alt={`Upload preview ${idx + 1}`}
                  className="w-full h-full object-cover"
                />
                <button
                  type="button"
                  onClick={() => handleRemove(idx)}
                  className="absolute top-1 right-1 p-1 bg-red-600/80 hover:bg-red-700 text-white rounded-full transition-opacity opacity-90 group-hover:opacity-100"
                  title="Remove image"
                >
                  <X className="h-3.5 w-3.5" />
                </button>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}