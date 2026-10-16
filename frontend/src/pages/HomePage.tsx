import React from "react";
import HeroBanner from "../components/home/HeroBanner";
import CategoryGrid from "../components/home/CategoryGrid";
import LocalDiscoverySection from "../components/home/LocalDiscoverySection";
import SmartItinerarySection from "../components/home/SmartItinerarySection";
import CommunityBanner from "../components/home/CommunityBanner";
import { PlannerValues } from "../components/home/PlannerBar";
import "./HomePage.css";

export const HomePage: React.FC = () => {
  const handlePlanSubmit = (values: PlannerValues) => {
    // Mock user interaction - in Phase 2 no backend call is made
    console.log("VISTA Smart Planner requested with:", values);
  };

  return (
    <div className="vista-homepage">
      {/* 1. HERO BANNER + INTEGRATED SMART PLANNER BAR */}
      <HeroBanner onPlanSubmit={handlePlanSubmit} />

      {/* 2. CATEGORY ROW (6 BENTO CATEGORIES) */}
      <CategoryGrid />

      {/* 3. LOCAL DISCOVERY SECTION (3 PLACE CARDS) */}
      <LocalDiscoverySection />

      {/* 4. SMART ITINERARY SECTION (LIGHT AQUA TIMELINE + LAUNCHPAD) */}
      <SmartItinerarySection />

      {/* 5. COMMUNITY TRUST & VIBE BANNER */}
      <CommunityBanner />
    </div>
  );
};

export default HomePage;
