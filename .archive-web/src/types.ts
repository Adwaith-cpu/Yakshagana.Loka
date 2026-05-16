import { Timestamp } from 'firebase/firestore';

export interface Artist {
  id: string;
  name: string;
  category: 'Bhagavatha' | 'Actor' | 'Maddalegara';
  bio?: string;
  veshas?: string[];
  imageUrl?: string;
}

export interface Show {
  id: string;
  melaName: string;
  date: Timestamp;
  location: string;
  address?: string;
  coordinates?: {
    lat: number;
    lng: number;
  };
  description?: string;
}

export interface AudioClip {
  id: string;
  title: string;
  prasanga?: string;
  artists?: string[];
  audioUrl: string;
  duration?: number;
  createdAt: Timestamp;
}

export interface UserProfile {
  id: string;
  email: string;
  role: 'admin' | 'manager' | 'user';
}
