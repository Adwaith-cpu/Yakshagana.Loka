import React, { useState, useEffect } from 'react';
import { db, auth, handleFirestoreError, OperationType } from '../lib/firebase';
import { collection, addDoc, onSnapshot, query, where, Timestamp } from 'firebase/firestore';
import { useAuthState } from 'react-firebase-hooks/auth';
import { Plus, Trash2, Calendar, MapPin, Music, UserPlus, AlertCircle, LayoutDashboard, Database } from 'lucide-react';
import { motion } from 'motion/react';

export default function ManagerPortal() {
  const [user] = useAuthState(auth);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  
  // Form states
  const [activeTab, setActiveTab] = useState<'shows' | 'artists' | 'audio'>('shows');
  const [showForm, setShowForm] = useState({
    melaName: '',
    date: '',
    time: '19:30',
    location: '',
    address: '',
    description: ''
  });

  useEffect(() => {
    if (!user) {
      setLoading(false);
      return;
    }

    const q = query(collection(db, 'users'), where('email', '==', user.email));
    const unsubscribe = onSnapshot(q, (snapshot) => {
      if (!snapshot.empty) {
        setUserRole(snapshot.docs[0].data().role);
      } else {
        // Fallback for initial admin
        if (user.email === 'adwaithbaburaj30@gmail.com') {
          setUserRole('admin');
        } else {
          setUserRole('user');
        }
      }
      setLoading(false);
    });

    return () => unsubscribe();
  }, [user]);

  const handleAddShow = async (e: React.FormEvent) => {
    e.preventDefault();
    if (userRole !== 'admin' && userRole !== 'manager') {
      alert('Only managers can add shows.');
      return;
    }

    try {
      const showDateTime = new Date(`${showForm.date}T${showForm.time}`);
      await addDoc(collection(db, 'shows'), {
        melaName: showForm.melaName,
        date: Timestamp.fromDate(showDateTime),
        location: showForm.location,
        address: showForm.address,
        description: showForm.description
      });
      alert('Show added successfully!');
      setShowForm({ melaName: '', date: '', time: '19:30', location: '', address: '', description: '' });
    } catch (error) {
      handleFirestoreError(error, OperationType.CREATE, 'shows');
    }
  };

  if (!user) {
    return (
      <div className="max-w-md mx-auto text-center py-20">
        <div className="bg-blue-50 p-6 rounded-3xl mb-6">
          <AlertCircle size={48} className="mx-auto text-blue-600 mb-4" />
          <h2 className="text-xl font-bold text-blue-900 mb-2">Restricted Access</h2>
          <p className="text-blue-700 text-sm">Please sign in with a manager or admin account to access the control center.</p>
        </div>
      </div>
    );
  }

  if (loading) return <div className="text-center py-20 text-slate-400">Verifying credentials...</div>;

  if (userRole === 'user') {
    return (
      <div className="max-w-md mx-auto text-center py-20">
        <div className="bg-red-50 p-6 rounded-3xl mb-6">
          <AlertCircle size={48} className="mx-auto text-red-600 mb-4" />
          <h2 className="text-xl font-bold text-red-900 mb-2">Insufficient Permissions</h2>
          <p className="text-red-700 text-sm">Your account role is "user". Manager status is required for this portal.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex flex-col md:flex-row gap-8">
        {/* Sidebar Nav */}
        <aside className="w-full md:w-64 space-y-2">
          <h2 className="text-xs font-bold text-slate-400 uppercase tracking-widest px-4 mb-4">Command Center</h2>
          <TabButton 
            active={activeTab === 'shows'} 
            onClick={() => setActiveTab('shows')} 
            icon={<Calendar size={18} />} 
            label="Tour Schedule" 
          />
          <TabButton 
            active={activeTab === 'artists'} 
            onClick={() => setActiveTab('artists')} 
            icon={<UserPlus size={18} />} 
            label="Artist Directory" 
          />
          <TabButton 
            active={activeTab === 'audio'} 
            onClick={() => setActiveTab('audio')} 
            icon={<Music size={18} />} 
            label="Radio Clips" 
          />
          {userRole === 'admin' && (
             <div className="pt-8 space-y-2">
               <h2 className="text-xs font-bold text-slate-400 uppercase tracking-widest px-4 mb-4">Admin Only</h2>
               <TabButton 
                 active={false} 
                 onClick={() => {}} 
                 icon={<Database size={18} />} 
                 label="System Logs" 
               />
             </div>
          )}
        </aside>

        {/* Content Area */}
        <main className="flex-grow bg-white rounded-3xl shadow-sm border border-slate-100 p-8">
          {activeTab === 'shows' && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h3 className="text-xl font-bold text-slate-800 flex items-center gap-2">
                  <LayoutDashboard size={20} className="text-blue-600" />
                  Update Tour Schedule
                </h3>
              </div>

              <form onSubmit={handleAddShow} className="space-y-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="space-y-1.5">
                    <label className="text-xs font-bold text-slate-500 uppercase">Mela Name</label>
                    <input
                      required
                      type="text"
                      className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                      placeholder="e.g. Saligrama Mela"
                      value={showForm.melaName}
                      onChange={e => setShowForm({ ...showForm, melaName: e.target.value })}
                    />
                  </div>
                  <div className="space-y-1.5">
                    <label className="text-xs font-bold text-slate-500 uppercase">Geographic Location</label>
                    <input
                      required
                      type="text"
                      className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                      placeholder="e.g. Kundapura"
                      value={showForm.location}
                      onChange={e => setShowForm({ ...showForm, location: e.target.value })}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="space-y-1.5">
                    <label className="text-xs font-bold text-slate-500 uppercase">Show Date</label>
                    <input
                      required
                      type="date"
                      className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                      value={showForm.date}
                      onChange={e => setShowForm({ ...showForm, date: e.target.value })}
                    />
                  </div>
                  <div className="space-y-1.5">
                    <label className="text-xs font-bold text-slate-500 uppercase">Start Time</label>
                    <input
                      required
                      type="time"
                      className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                      value={showForm.time}
                      onChange={e => setShowForm({ ...showForm, time: e.target.value })}
                    />
                  </div>
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-bold text-slate-500 uppercase">Venue Address</label>
                  <input
                    type="text"
                    className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                    placeholder="e.g. Sri Krishna Temple Complex"
                    value={showForm.address}
                    onChange={e => setShowForm({ ...showForm, address: e.target.value })}
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-bold text-slate-500 uppercase">Prasanga / Description</label>
                  <textarea
                    rows={3}
                    className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                    placeholder="e.g. Mahishamardini Kalaga by leading artists..."
                    value={showForm.description}
                    onChange={e => setShowForm({ ...showForm, description: e.target.value })}
                  />
                </div>

                <button
                  type="submit"
                  className="w-full bg-blue-600 text-white font-bold py-3 rounded-2xl shadow-lg hover:bg-blue-700 active:scale-[0.98] transition-all flex items-center justify-center gap-2"
                >
                  <Plus size={20} />
                  Publish to Live Tracker
                </button>
              </form>
            </div>
          )}

          {activeTab === 'artists' && (
            <div className="text-center py-12">
              <UserPlus size={48} className="mx-auto text-slate-200 mb-4" />
              <h3 className="text-lg font-bold text-slate-700 mb-2">Artist Management</h3>
              <p className="text-slate-500 text-sm">Artist profile editing is coming in the next update.</p>
            </div>
          )}

          {activeTab === 'audio' && (
            <div className="text-center py-12">
              <Music size={48} className="mx-auto text-slate-200 mb-4" />
              <h3 className="text-lg font-bold text-slate-700 mb-2">Radio Studio</h3>
              <p className="text-slate-500 text-sm">Audio upload integration is currently in beta.</p>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}

interface TabButtonProps {
  active: boolean;
  onClick: () => void;
  icon: React.ReactNode;
  label: string;
}

const TabButton: React.FC<TabButtonProps> = ({ active, onClick, icon, label }) => {
  return (
    <button
      onClick={onClick}
      className={`w-full flex items-center gap-3 px-4 py-3 rounded-2xl text-sm font-semibold transition-all ${
        active 
          ? 'bg-blue-600 text-white shadow-md' 
          : 'text-slate-500 hover:bg-slate-100 hover:text-slate-800'
      }`}
    >
      {icon}
      {label}
    </button>
  );
}
