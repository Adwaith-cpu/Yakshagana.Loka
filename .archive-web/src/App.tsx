import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import ArtistDirectory from './pages/ArtistDirectory';
import TalamaddaleRadio from './pages/Radio';
import ManagerPortal from './pages/ManagerPortal';
import { motion, AnimatePresence } from 'motion/react';

export default function App() {
  return (
    <Router>
      <div className="min-h-screen bg-slate-50 flex flex-col">
        <Navbar />
        <main className="flex-grow container mx-auto px-4 py-8">
          <AnimatePresence mode="wait">
            <Routes>
              <Route path="/" element={<PageWrapper><Home /></PageWrapper>} />
              <Route path="/artists" element={<PageWrapper><ArtistDirectory /></PageWrapper>} />
              <Route path="/radio" element={<PageWrapper><TalamaddaleRadio /></PageWrapper>} />
              <Route path="/manager" element={<PageWrapper><ManagerPortal /></PageWrapper>} />
            </Routes>
          </AnimatePresence>
        </main>
        <footer className="bg-white border-t border-slate-200 py-6 text-center text-slate-500 text-sm">
          <p>© {new Date().getFullYear()} Yakshagana-Loka. Preserving the Coastal Heritage.</p>
        </footer>
      </div>
    </Router>
  );
}

function PageWrapper({ children }: { children: React.ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -10 }}
      transition={{ duration: 0.2 }}
    >
      {children}
    </motion.div>
  );
}
