import React, { useState, useEffect } from 'react';
import { db, handleFirestoreError, OperationType } from '../lib/firebase';
import { collection, query, onSnapshot, orderBy } from 'firebase/firestore';
import { Artist } from '../types';
import { Search, Filter, User, BookOpen, Music, Image as ImageIcon } from 'lucide-react';
import { motion } from 'motion/react';

export default function ArtistDirectory() {
  const [artists, setArtists] = useState<Artist[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeCategory, setActiveCategory] = useState<string>('All');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const q = query(collection(db, 'artists'), orderBy('name', 'asc'));
    const unsubscribe = onSnapshot(q, 
      (snapshot) => {
        setArtists(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() } as Artist)));
        setLoading(false);
      },
      (error) => {
        handleFirestoreError(error, OperationType.LIST, 'artists');
        setLoading(false);
      }
    );
    return () => unsubscribe();
  }, []);

  const filteredArtists = artists.filter(artist => {
    const matchesSearch = artist.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          artist.category.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = activeCategory === 'All' || artist.category === activeCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="space-y-8">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 bg-white p-8 rounded-3xl shadow-sm border border-slate-100">
        <div className="max-w-xl">
          <h1 className="text-3xl font-bold text-slate-800 mb-2">Artist Encyclopedia</h1>
          <p className="text-slate-500 font-medium">
            Discover the legends and rising stars of Yakshagana. From powerhouse Bhagavatas to master actors.
          </p>
        </div>
        <div className="flex flex-col sm:flex-row items-center gap-3 w-full md:w-auto">
          <div className="relative w-full sm:w-64">
             <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
             <input
               type="text"
               placeholder="Search artists..."
               className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
               value={searchTerm}
               onChange={(e) => setSearchTerm(e.target.value)}
             />
          </div>
          <div className="flex bg-slate-100 p-1 rounded-xl">
            {['All', 'Bhagavatha', 'Actor', 'Maddalegara'].map(cat => (
              <button
                key={cat}
                onClick={() => setActiveCategory(cat)}
                className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-all ${
                  activeCategory === cat ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'
                }`}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>
      </header>

      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {[1, 2, 3, 4].map(i => <div key={i} className="h-64 bg-slate-200 animate-pulse rounded-2xl" />)}
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {filteredArtists.map(artist => (
            <ArtistCard key={artist.id} artist={artist} />
          ))}
          {filteredArtists.length === 0 && (
            <div className="col-span-full py-20 text-center">
              <User size={48} className="mx-auto text-slate-200 mb-4" />
              <p className="text-slate-400 font-medium italic">No artists found matching your criteria.</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

interface ArtistCardProps {
  artist: Artist;
}

const ArtistCard: React.FC<ArtistCardProps> = ({ artist }) => {
  const getIcon = () => {
    switch (artist.category) {
      case 'Bhagavatha': return <Music size={14} />;
      case 'Actor': return <User size={14} />;
      default: return <BookOpen size={14} />;
    }
  };

  return (
    <motion.div
      layout
      whileHover={{ y: -5 }}
      className="bg-white rounded-2xl overflow-hidden shadow-sm border border-slate-100 group"
    >
      <div className="aspect-[4/5] bg-slate-100 relative overflow-hidden">
        {artist.imageUrl ? (
          <img 
            src={artist.imageUrl} 
            alt={artist.name} 
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
            referrerPolicy="no-referrer"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-slate-300">
            <User size={48} />
          </div>
        )}
        <div className="absolute top-3 left-3 flex gap-2">
          <span className="flex items-center gap-1 bg-white/90 backdrop-blur-sm text-blue-900 text-[10px] font-bold px-2 py-1 rounded-full shadow-sm uppercase tracking-wider">
            {getIcon()} {artist.category}
          </span>
        </div>
      </div>
      <div className="p-5">
        <h3 className="font-bold text-slate-800 text-lg mb-1">{artist.name}</h3>
        <p className="text-slate-500 text-xs line-clamp-2 mb-4 h-8">{artist.bio || 'Legendary performance history in the coastal art scene.'}</p>
        
        {artist.veshas && artist.veshas.length > 0 && (
          <div className="flex flex-wrap gap-1.5 mb-4">
            {artist.veshas.slice(0, 3).map(v => (
              <span key={v} className="bg-slate-100 text-slate-600 text-[10px] px-2 py-0.5 rounded font-medium">#{v}</span>
            ))}
          </div>
        )}

        <button className="w-full py-2 bg-blue-50 text-blue-600 rounded-xl text-sm font-semibold hover:bg-blue-600 hover:text-white transition-all">
          View Profile
        </button>
      </div>
    </motion.div>
  );
}
