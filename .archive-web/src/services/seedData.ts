import { db } from '../lib/firebase';
import { collection, getDocs, addDoc, Timestamp, doc, setDoc } from 'firebase/firestore';

const sampleArtists = [
  {
    name: "Subrahmanya Dharbe",
    category: "Bhagavatha",
    bio: "Renowned Bhagavata with over 40 years of experience in various Melas.",
    veshas: ["Hero", "Narrator"],
    imageUrl: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=400"
  },
  {
    name: "Ganapathi Bhat",
    category: "Actor",
    bio: "Master of comedy and serious roles, known for his versatile performance in coastal Karnataka.",
    veshas: ["Hasyagara", "Rakshasa"],
    imageUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=400"
  },
  {
    name: "Raghavendra Hegde",
    category: "Maddalegara",
    bio: "Exceptional percussionist specializing in Maddale for Talamaddale sessions.",
    veshas: ["Percussion"],
    imageUrl: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=400"
  }
];

const sampleClips = [
  {
    title: "Mahishamardini Kalaga - Opening Song",
    prasanga: "Devi Mahatme",
    artists: ["Subrahmanya Dharbe"],
    audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
    duration: 372,
    createdAt: Timestamp.now()
  },
  {
    title: "Karna Arjuna Dialogue - Segment 1",
    prasanga: "Karna Parva",
    artists: ["Ganapathi Bhat", "Raghavendra Hegde"],
    audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
    duration: 420,
    createdAt: Timestamp.now()
  }
];

export async function seedInitialData() {
  const artistsSnap = await getDocs(collection(db, 'artists'));
  if (artistsSnap.empty) {
    console.log("Seeding artists...");
    for (const artist of sampleArtists) {
      await addDoc(collection(db, 'artists'), artist);
    }
  }

  const clipsSnap = await getDocs(collection(db, 'audio_clips'));
  if (clipsSnap.empty) {
    console.log("Seeding audio clips...");
    for (const clip of sampleClips) {
      await addDoc(collection(db, 'audio_clips'), clip);
    }
  }

  // Seed initial shows for "Tonight" and "Itinerary"
  const showsSnap = await getDocs(collection(db, 'shows'));
  if (showsSnap.empty) {
    console.log("Seeding shows...");
    const tonight = new Date();
    tonight.setHours(19, 30, 0, 0);

    const tomorrow = new Date();
    tomorrow.setDate(tonight.getDate() + 1);
    tomorrow.setHours(20, 0, 0, 0);

    const nextWeek = new Date();
    nextWeek.setDate(tonight.getDate() + 7);
    nextWeek.setHours(19, 0, 0, 0);

    const sampleShows = [
      {
        melaName: "Saligrama Mela",
        date: Timestamp.fromDate(tonight),
        location: "Koteshwara",
        address: "Sri Kotilingeshwara Temple",
        description: "Grand performance of Sudhanva Kalaga."
      },
      {
        melaName: "Mandarthi Mela",
        date: Timestamp.fromDate(tomorrow),
        location: "Mandarthi",
        address: "Mela Ground",
        description: "Special Devi Mahatme performance."
      },
      {
        melaName: "Perdoor Mela",
        date: Timestamp.fromDate(nextWeek),
        location: "Udupi",
        address: "Rajangana",
        description: "Classical Prasanga presentation."
      }
    ];

    for (const show of sampleShows) {
      await addDoc(collection(db, 'shows'), show);
    }
  }
}
