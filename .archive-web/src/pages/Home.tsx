import React, { useState, useEffect } from 'react';
import { db, handleFirestoreError, OperationType } from '../lib/firebase';
import { collection, query, orderBy, onSnapshot, Timestamp, where } from 'firebase/firestore';
import { Show } from '../types';
import { Calendar, MapPin, Clock, Info, ExternalLink } from 'lucide-react';
import { motion } from 'motion/react';

export default function Home() {
  const [shows, setShows] = useState<Show[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Shows for the next 30 days
    const now = new Date();
    const thirtyDaysLater = new Date();
    thirtyDaysLater.setDate(now.getDate() + 30);

    const q = query(
      collection(db, 'shows'),
      where('date', '>=', Timestamp.fromDate(now)),
      where('date', '<=', Timestamp.fromDate(thirtyDaysLater)),
      orderBy('date', 'asc')
    );

    const unsubscribe = onSnapshot(q, 
      (snapshot) => {
        const showList = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() } as Show));
        setShows(showList);
        setLoading(false);
      },
      (error) => {
        handleFirestoreError(error, OperationType.LIST, 'shows');
        setLoading(false);
      }
    );

    return () => unsubscribe();
  }, []);

  const tonightShows = shows.filter(s => {
    const showDate = s.date.toDate();
    const today = new Date();
    return showDate.getDate() === today.getDate() && 
           showDate.getMonth() === today.getMonth() && 
           showDate.getFullYear() === today.getFullYear();
  });

  return (
    <div className="space-y-12">
      {/* Hero Section */}
      <section className="relative rounded-3xl overflow-hidden bg-blue-900 text-white p-8 md:p-12 shadow-2xl">
        <div className="absolute top-0 right-0 w-1/3 h-full bg-gradient-to-l from-blue-500/20 to-transparent pointer-events-none" />
        <div className="max-w-2xl relative z-10">
          <h1 className="text-4xl md:text-5xl font-bold mb-4 tracking-tight">The Digital Stage of Coastal Art</h1>
          <p className="text-blue-100 text-lg mb-8 leading-relaxed">
            Real-time performance tracking, artist encyclopedia, and auditory heritage preservation. 
            Experience Yakshagana like never before.
          </p>
          <div className="flex flex-wrap gap-4">
            <div className="flex items-center gap-2 bg-blue-800/50 px-4 py-2 rounded-full border border-blue-400/30">
              <span className="w-2 h-2 bg-green-400 rounded-full animate-pulse" />
              <span className="text-sm font-medium">{tonightShows.length} Shows Tonight</span>
            </div>
            <div className="flex items-center gap-2 bg-blue-800/50 px-4 py-2 rounded-full border border-blue-400/30">
              <Calendar size={16} className="text-blue-300" />
              <span className="text-sm font-medium">30-Day Itinerary Active</span>
            </div>
          </div>
        </div>
      </section>

      {/* Tonight's Shows Highlight */}
      <section>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-slate-800 flex items-center gap-2">
            <div className="w-2 h-8 bg-blue-600 rounded-full" />
            Tonight's Performances
          </h2>
          <span className="text-sm text-slate-500 font-medium">
            {new Date().toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' })}
          </span>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[1, 2, 3].map(i => <div key={i} className="h-48 bg-slate-200 animate-pulse rounded-2xl" />)}
          </div>
        ) : tonightShows.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {tonightShows.map((show) => (
              <ShowCard key={show.id} show={show} highlight />
            ))}
          </div>
        ) : (
          <div className="bg-white border-2 border-dashed border-slate-200 rounded-2xl p-12 text-center">
            <p className="text-slate-400 font-medium italic">No performances scheduled for tonight.</p>
          </div>
        )}
      </section>

      {/* 30-Day Itinerary */}
      <section>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-slate-800 flex items-center gap-2">
            <div className="w-2 h-8 bg-slate-400 rounded-full" />
            Full Itinerary (Next 30 Days)
          </h2>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-100">
                  <th className="px-6 py-4 font-semibold text-slate-600">Date & Time</th>
                  <th className="px-6 py-4 font-semibold text-slate-600">Troupe (Mela)</th>
                  <th className="px-6 py-4 font-semibold text-slate-600">Location</th>
                  <th className="px-6 py-4 font-semibold text-slate-600">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {shows.map((show) => (
                  <tr key={show.id} className="hover:bg-slate-50/50 transition-colors group">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2 text-slate-700 font-medium">
                        <Clock size={14} className="text-blue-500" />
                        {show.date.toDate().toLocaleDateString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="font-semibold text-blue-900">{show.melaName}</span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-1.5 text-slate-600">
                        <MapPin size={14} className="text-red-400" />
                        {show.location}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                       <button className="text-blue-600 font-medium text-sm flex items-center gap-1 hover:underline">
                         Details <ExternalLink size={12} />
                       </button>
                    </td>
                  </tr>
                ))}
                {shows.length === 0 && !loading && (
                  <tr>
                    <td colSpan={4} className="px-6 py-12 text-center text-slate-400 italic">
                      Check back later for updated schedules.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  );
}

interface ShowCardProps {
  show: Show;
  highlight?: boolean;
}

const ShowCard: React.FC<ShowCardProps> = ({ show, highlight }) => {
  return (
    <motion.div
      whileHover={{ y: -4 }}
      className={`relative p-6 rounded-2xl shadow-sm border overflow-hidden ${
        highlight ? 'bg-white border-blue-100' : 'bg-slate-50 border-slate-200'
      }`}
    >
      {highlight && (
        <div className="absolute top-0 right-0 bg-blue-600 text-white text-[10px] uppercase tracking-widest font-bold px-3 py-1 rounded-bl-xl shadow-sm">
          Live Now
        </div>
      )}
      <h3 className="text-xl font-bold text-blue-900 mb-2">{show.melaName}</h3>
      <div className="space-y-2 mb-4">
        <div className="flex items-center gap-2 text-slate-600 text-sm">
          <MapPin size={16} className="text-red-500 shrink-0" />
          <span className="font-medium">{show.location}</span>
        </div>
        {show.address && <p className="text-slate-500 text-xs pl-6">{show.address}</p>}
        <div className="flex items-center gap-2 text-slate-600 text-sm">
          <Clock size={16} className="text-blue-500 shrink-0" />
          <span>{show.date.toDate().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
        </div>
      </div>
      {show.description && (
        <div className="pt-4 border-t border-slate-100">
          <p className="text-slate-500 text-xs italic line-clamp-2">"{show.description}"</p>
        </div>
      )}
    </motion.div>
  );
}
