import React from 'react';
import { Link } from 'react-router-dom';
import { Search, Heart, Shield, Sparkles } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-slate-900 text-slate-400 text-sm mt-auto border-t border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          {/* Brand */}
          <div className="space-y-4 md:col-span-1">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center text-white">
                <Search className="w-4 h-4 stroke-[2.5]" />
              </div>
              <span className="font-extrabold text-lg text-white">
                Find<span className="text-brand-400">It</span>
              </span>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed">
              Lost something? Find it faster. Centralized platform for campus &amp; community lost and found with smart heuristic matching and secure ownership verification.
            </p>
          </div>

          {/* Quick links */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-200 mb-3">
              Explore
            </h4>
            <ul className="space-y-2 text-xs">
              <li>
                <Link to="/lost-items" className="hover:text-white transition-colors">
                  Lost Items Directory
                </Link>
              </li>
              <li>
                <Link to="/found-items" className="hover:text-white transition-colors">
                  Found Items Directory
                </Link>
              </li>
              <li>
                <Link to="/report-lost" className="hover:text-white transition-colors">
                  Submit Lost Report
                </Link>
              </li>
              <li>
                <Link to="/report-found" className="hover:text-white transition-colors">
                  Submit Found Report
                </Link>
              </li>
            </ul>
          </div>

          {/* Smart Features */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-200 mb-3">
              Platform Features
            </h4>
            <ul className="space-y-2 text-xs">
              <li className="flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5 text-brand-400" />
                <span>Weighted Heuristic Matcher</span>
              </li>
              <li className="flex items-center gap-1.5">
                <Shield className="w-3.5 h-3.5 text-emerald-400" />
                <span>Masked Sensitive Clues</span>
              </li>
              <li className="flex items-center gap-1.5">
                <Shield className="w-3.5 h-3.5 text-indigo-400" />
                <span>Handover Verification Codes</span>
              </li>
            </ul>
          </div>

          {/* Support & Legal */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-200 mb-3">
              Default Test Accounts
            </h4>
            <div className="bg-slate-800/80 p-3 rounded-xl border border-slate-700/60 text-xs space-y-1">
              <p><span className="text-slate-200 font-semibold">Admin:</span> admin@findit.org</p>
              <p><span className="text-slate-200 font-semibold">Password:</span> Admin@123</p>
              <p className="pt-1 text-[11px] text-slate-400">User: john.doe@university.edu (User@1234)</p>
            </div>
          </div>
        </div>

        <div className="pt-8 border-t border-slate-800 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-4">
          <p>© {new Date().getFullYear()} FindIt Platform. All rights reserved.</p>
          <div className="flex items-center gap-4">
            <span>Powered by Spring Boot 3 &amp; React</span>
          </div>
        </div>
      </div>
    </footer>
  );
}