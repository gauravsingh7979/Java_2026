import React from 'react';

const statusConfig = {
  // Item status
  REPORTED: { bg: 'bg-blue-100', text: 'text-blue-800', border: 'border-blue-200', label: 'Reported' },
  MATCHED: { bg: 'bg-purple-100', text: 'text-purple-800', border: 'border-purple-200', label: 'Match Found' },
  CLAIM_PENDING: { bg: 'bg-amber-100', text: 'text-amber-800', border: 'border-amber-200', label: 'Claim Pending' },
  RECOVERED: { bg: 'bg-emerald-100', text: 'text-emerald-800', border: 'border-emerald-200', label: 'Recovered' },
  CLOSED: { bg: 'bg-slate-100', text: 'text-slate-700', border: 'border-slate-200', label: 'Closed' },

  // Claim status
  PENDING: { bg: 'bg-amber-100', text: 'text-amber-800', border: 'border-amber-200', label: 'Pending Review' },
  UNDER_REVIEW: { bg: 'bg-sky-100', text: 'text-sky-800', border: 'border-sky-200', label: 'Under Review' },
  APPROVED: { bg: 'bg-emerald-100', text: 'text-emerald-800', border: 'border-emerald-200', label: 'Approved' },
  REJECTED: { bg: 'bg-rose-100', text: 'text-rose-800', border: 'border-rose-200', label: 'Rejected' },
  WITHDRAWN: { bg: 'bg-slate-100', text: 'text-slate-700', border: 'border-slate-200', label: 'Withdrawn' },

  // Match classifications
  STRONG_MATCH: { bg: 'bg-emerald-100', text: 'text-emerald-800', border: 'border-emerald-300', label: 'Strong Match' },
  POSSIBLE_MATCH: { bg: 'bg-amber-100', text: 'text-amber-800', border: 'border-amber-300', label: 'Possible Match' },
  WEAK_MATCH: { bg: 'bg-slate-100', text: 'text-slate-700', border: 'border-slate-300', label: 'Weak Match' },

  // Recovery status
  INITIATED: { bg: 'bg-blue-100', text: 'text-blue-800', border: 'border-blue-200', label: 'Recovery Initiated' },
  HANDOVER_SCHEDULED: { bg: 'bg-indigo-100', text: 'text-indigo-800', border: 'border-indigo-200', label: 'Handover Scheduled' },
  COMPLETED: { bg: 'bg-emerald-100', text: 'text-emerald-800', border: 'border-emerald-200', label: 'Handover Complete' },
};

export default function StatusBadge({ status, className = '' }) {
  if (!status) return null;
  const config = statusConfig[status] || {
    bg: 'bg-slate-100',
    text: 'text-slate-700',
    border: 'border-slate-200',
    label: status.replace(/_/g, ' '),
  };

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${config.bg} ${config.text} ${config.border} ${className}`}
    >
      <span className="w-1.5 h-1.5 mr-1.5 rounded-full bg-current opacity-70"></span>
      {config.label}
    </span>
  );
}