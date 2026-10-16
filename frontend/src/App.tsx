import { Link, Route, Routes } from "react-router-dom";
import MainLayout from "./components/layout/MainLayout";
import Button from "./components/common/Button";
import HomePage from "./pages/HomePage";
import DiscoverPage from "./pages/DiscoverPage";
import PlaceDetailPage from "./pages/PlaceDetailPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import SavedPage from "./pages/SavedPage";
import ProtectedRoute from "./components/auth/ProtectedRoute";
import { AuthProvider } from "./context/AuthContext";
import { FavoriteProvider } from "./context/FavoriteContext";

function PlaceholderPage({ title }: { title: string }) {
  return (
    <div className="placeholder">
      <p className="eyebrow">VISTA · DA NANG DISCOVERY</p>
      <h1>{title}</h1>
      <p>Màn hình đang được triển khai bám sát bộ mockup Coastal Sunlight Modernism.</p>
      <Link to="/" style={{ textDecoration: "none" }}>
        <Button variant="secondary" size="sm">
          ← Về trang chủ
        </Button>
      </Link>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <FavoriteProvider>
        <Routes>
          <Route element={<MainLayout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/places" element={<DiscoverPage />} />
            <Route path="/places/:idOrSlug" element={<PlaceDetailPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route
              path="/saved"
              element={
                <ProtectedRoute>
                  <SavedPage />
                </ProtectedRoute>
              }
            />
            <Route path="/routes" element={<PlaceholderPage title="Tuyến khám phá" />} />
            <Route path="/trips" element={<PlaceholderPage title="Chuyến đi của tôi" />} />
            <Route path="/community" element={<PlaceholderPage title="Cộng đồng" />} />
            <Route path="*" element={<PlaceholderPage title="Không tìm thấy trang" />} />
          </Route>
        </Routes>
      </FavoriteProvider>
    </AuthProvider>
  );
}
