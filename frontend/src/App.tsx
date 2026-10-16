import { Link, Route, Routes } from "react-router-dom";
import MainLayout from "./components/layout/MainLayout";
import Button from "./components/common/Button";
import HomePage from "./pages/HomePage";
import DiscoverPage from "./pages/DiscoverPage";

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
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/places" element={<DiscoverPage />} />
        <Route path="/routes" element={<PlaceholderPage title="Tuyến khám phá" />} />
        <Route path="/trips" element={<PlaceholderPage title="Chuyến đi của tôi" />} />
        <Route path="/community" element={<PlaceholderPage title="Cộng đồng" />} />
        <Route path="/saved" element={<PlaceholderPage title="Địa điểm đã lưu" />} />
        <Route path="/login" element={<PlaceholderPage title="Đăng nhập VISTA" />} />
        <Route path="*" element={<PlaceholderPage title="Không tìm thấy trang" />} />
      </Route>
    </Routes>
  );
}
