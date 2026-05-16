import React from 'react';
import { Link } from 'react-router-dom';
import { auth, loginWithGoogle, logout } from '../lib/firebase';
import { useAuthState } from 'react-firebase-hooks/auth';
import { Map, Users, Radio as RadioIcon, Settings, LogIn, LogOut } from 'lucide-react';
import { motion } from 'motion/react';

export default function Navbar() {
  const [user] = useAuthState(auth);

  return (
    <nav className="bg-blue-900 text-white shadow-lg sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex items-center gap-2 font-bold text-xl tracking-tight">
              <span className="bg-white text-blue-900 p-1 rounded">YL</span>
              <span>Yakshagana-Loka</span>
            </Link>
          </div>
          <div className="hidden md:block">
            <div className="ml-10 flex items-baseline space-x-4">
              <NavLink to="/" icon={<Map size={18} />} label="Tracker" />
              <NavLink to="/artists" icon={<Users size={18} />} label="Artists" />
              <NavLink to="/radio" icon={<RadioIcon size={18} />} label="Radio" />
              {user && <NavLink to="/manager" icon={<Settings size={18} />} label="Manager" />}
            </div>
          </div>
          <div className="flex items-center gap-4">
            {user ? (
              <div className="flex items-center gap-3">
                <span className="text-sm hidden sm:inline text-blue-200">{user.displayName || user.email}</span>
                <button
                  onClick={logout}
                  className="flex items-center gap-1 bg-blue-800 hover:bg-blue-700 px-3 py-1.5 rounded-md text-sm transition-colors"
                >
                  <LogOut size={16} />
                  <span className="hidden sm:inline">Logout</span>
                </button>
              </div>
            ) : (
              <button
                onClick={loginWithGoogle}
                className="flex items-center gap-1 bg-white text-blue-900 hover:bg-blue-50 px-4 py-2 rounded-md text-sm font-medium transition-colors"
              >
                <LogIn size={18} />
                <span>Join Stage</span>
              </button>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

interface NavLinkProps {
  to: string;
  icon: React.ReactNode;
  label: string;
}

const NavLink: React.FC<NavLinkProps> = ({ to, icon, label }) => {
  return (
    <Link
      to={to}
      className="flex items-center gap-1.5 px-3 py-2 rounded-md text-sm font-medium hover:bg-blue-800 transition-colors"
    >
      {icon}
      {label}
    </Link>
  );
}
