import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { KeyRound, AlertCircle, CheckCircle, ArrowLeft } from 'lucide-react';
import { authService } from '../services/authService';

export default function ForgotPassword() {
  const [successMsg, setSuccessMsg] = useState('');
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const onSubmit = async (data) => {
    setServerError('');
    setSuccessMsg('');
    setLoading(true);
    try {
      const res = await authService.forgotPassword(data);
      setSuccessMsg(res.message || 'Password reset email sent. Please check your inbox.');
    } catch (err) {
      setServerError(err.response?.data?.message || 'Failed to process request. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-6 bg-white p-8 rounded-3xl border border-slate-200 shadow-xl">
        <div className="text-center">
          <div className="w-12 h-12 rounded-2xl bg-amber-500 flex items-center justify-center text-white mx-auto shadow-md shadow-amber-500/20 mb-3">
            <KeyRound className="w-6 h-6" />
          </div>
          <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            Reset your password
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Enter your registered email and we'll generate a password reset link
          </p>
        </div>

        {serverError && (
          <div className="p-3.5 bg-red-50 border border-red-200 rounded-2xl text-red-700 text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{serverError}</span>
          </div>
        )}

        {successMsg && (
          <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl text-emerald-800 text-xs space-y-2">
            <div className="flex items-center gap-2 font-bold">
              <CheckCircle className="w-4 h-4 text-emerald-600" />
              <span>Reset Email Dispatched</span>
            </div>
            <p>{successMsg}</p>
            <p className="text-[11px] text-emerald-700">
              For testing in development: You can also use the Reset Password page directly with your reset token.
            </p>
            <Link
              to="/reset-password"
              className="inline-block mt-2 font-bold underline text-emerald-900"
            >
              Go to Enter Reset Token →
            </Link>
          </div>
        )}

        {!successMsg && (
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Your Account Email
              </label>
              <input
                type="email"
                {...register('email', { required: 'Email is required' })}
                placeholder="user@university.edu"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
              {errors.email && (
                <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>
              )}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-brand-600 hover:bg-brand-700 text-white font-bold text-sm rounded-xl shadow-lg shadow-brand-500/30 transition-all disabled:opacity-50"
            >
              {loading ? 'Sending Instructions...' : 'Send Reset Link'}
            </button>
          </form>
        )}

        <div className="text-center">
          <Link
            to="/login"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-600 hover:text-slate-900"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Back to Login</span>
          </Link>
        </div>
      </div>
    </div>
  );
}