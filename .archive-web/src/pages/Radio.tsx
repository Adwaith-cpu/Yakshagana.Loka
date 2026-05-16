import React, { useState, useEffect, useRef } from 'react';
import { db, handleFirestoreError, OperationType } from '../lib/firebase';
import { collection, query, onSnapshot, orderBy } from 'firebase/firestore';
import { AudioClip } from '../types';
import { Play, Pause, SkipBack, SkipForward, Volume2, Radio as RadioIcon, Music, Clock } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

export default function TalamaddaleRadio() {
  const [clips, setClips] = useState<AudioClip[]>([]);
  const [currentClipIndex, setCurrentClipIndex] = useState<number>(-1);
  const [isPlaying, setIsPlaying] = useState(false);
  const [loading, setLoading] = useState(true);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    const q = query(collection(db, 'audio_clips'), orderBy('createdAt', 'desc'));
    const unsubscribe = onSnapshot(q, 
      (snapshot) => {
        const clipList = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() } as AudioClip));
        setClips(clipList);
        setLoading(false);
      },
      (error) => {
        handleFirestoreError(error, OperationType.LIST, 'audio_clips');
        setLoading(false);
      }
    );
    return () => unsubscribe();
  }, []);

  const currentClip = currentClipIndex >= 0 ? clips[currentClipIndex] : null;

  const togglePlay = () => {
    if (!audioRef.current || !currentClip) return;
    if (isPlaying) {
      audioRef.current.pause();
    } else {
      audioRef.current.play();
    }
    setIsPlaying(!isPlaying);
  };

  const playClip = (index: number) => {
    setCurrentClipIndex(index);
    setIsPlaying(true);
    // Audio source update is handled by useEffect on currentClipIndex or index
  };

  const nextClip = () => {
    if (clips.length === 0) return;
    const nextIdx = (currentClipIndex + 1) % clips.length;
    setCurrentClipIndex(nextIdx);
    setIsPlaying(true);
  };

  const prevClip = () => {
    if (clips.length === 0) return;
    const prevIdx = (currentClipIndex - 1 + clips.length) % clips.length;
    setCurrentClipIndex(prevIdx);
    setIsPlaying(true);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      {/* Player Display */}
      <section className="bg-gradient-to-br from-blue-900 to-blue-800 rounded-3xl p-8 md:p-12 shadow-2xl text-white">
        <div className="flex flex-col md:flex-row items-center gap-8">
          {/* Visualizer Area */}
          <div className="relative w-48 h-48 rounded-2xl bg-blue-700/50 flex items-center justify-center overflow-hidden shrink-0 border border-blue-400/20">
            <AnimatePresence mode="wait">
              {isPlaying ? (
                <motion.div 
                  initial={{ scale: 0.8, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  exit={{ scale: 0.8, opacity: 0 }}
                  className="flex items-center gap-1.5"
                >
                  {[1, 2, 3, 4, 5].map(i => (
                    <motion.div
                      key={i}
                      animate={{ height: [20, 40, 60, 30, 50, 20] }}
                      transition={{ duration: 1, repeat: Infinity, delay: i * 0.1 }}
                      className="w-1.5 bg-blue-300 rounded-full"
                    />
                  ))}
                </motion.div>
              ) : (
                <RadioIcon size={64} className="text-blue-400/50" />
              )}
            </AnimatePresence>
          </div>

          <div className="flex-grow text-center md:text-left">
            <div className="mb-6">
              <span className="text-[10px] font-bold uppercase tracking-[0.2em] text-blue-400 mb-2 block">Now Broadcasting</span>
              <h1 className="text-3xl font-bold mb-1">{currentClip?.title || 'Select a Clip'}</h1>
              <p className="text-blue-300 font-medium">{currentClip?.prasanga || 'Curated Talamaddale Radio'}</p>
              {currentClip?.artists && (
                <div className="mt-2 text-sm text-blue-400 flex items-center justify-center md:justify-start gap-2">
                  <Music size={14} />
                  <span>{currentClip.artists.join(', ')}</span>
                </div>
              )}
            </div>

            <div className="flex items-center justify-center md:justify-start gap-6">
              <button onClick={prevClip} className="p-2 hover:bg-white/10 rounded-full transition-colors text-blue-300">
                <SkipBack size={24} />
              </button>
              <button 
                onClick={togglePlay}
                disabled={!currentClip}
                className="w-16 h-16 flex items-center justify-center bg-white text-blue-900 rounded-full shadow-xl hover:scale-105 active:scale-95 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isPlaying ? <Pause size={32} fill="currentColor" /> : <Play size={32} fill="currentColor" className="ml-1" />}
              </button>
              <button onClick={nextClip} className="p-2 hover:bg-white/10 rounded-full transition-colors text-blue-300">
                <SkipForward size={24} />
              </button>
            </div>

            <audio
              ref={audioRef}
              src={currentClip?.audioUrl}
              onEnded={nextClip}
              onPlay={() => setIsPlaying(true)}
              onPause={() => setIsPlaying(false)}
              className="hidden"
            />
          </div>
        </div>
      </section>

      {/* Playlist */}
      <section className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100">
        <h2 className="text-lg font-bold text-slate-800 mb-6 flex items-center gap-2 px-2">
          <Clock size={18} className="text-blue-600" />
          Recent Recordings
        </h2>
        <div className="space-y-2">
          {clips.map((clip, idx) => (
            <button
              key={clip.id}
              onClick={() => playClip(idx)}
              className={`w-full flex items-center gap-4 p-4 rounded-2xl transition-all ${
                currentClipIndex === idx ? 'bg-blue-50 border border-blue-100' : 'hover:bg-slate-50'
              }`}
            >
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 ${
                currentClipIndex === idx ? 'bg-blue-600 text-white shadow-lg' : 'bg-slate-100 text-slate-400'
              }`}>
                {currentClipIndex === idx && isPlaying ? (
                   <span className="w-1.5 h-4 bg-white rounded-full animate-bounce" />
                ) : (
                   <Play size={18} fill={currentClipIndex === idx ? 'currentColor' : 'none'} />
                )}
              </div>
              <div className="flex-grow text-left">
                <h4 className={`font-bold text-sm ${currentClipIndex === idx ? 'text-blue-900' : 'text-slate-700'}`}>{clip.title}</h4>
                <p className="text-xs text-slate-400 font-medium">{clip.prasanga}</p>
              </div>
              <div className="text-xs font-mono text-slate-400 hidden sm:block">
                {clip.duration ? `${Math.floor(clip.duration / 60)}:${(clip.duration % 60).toString().padStart(2, '0')}` : '--:--'}
              </div>
            </button>
          ))}
          {clips.length === 0 && !loading && (
             <div className="p-12 text-center text-slate-400 italic">
               The radio is currently quiet.
             </div>
          )}
        </div>
      </section>
    </div>
  );
}
