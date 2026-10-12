import { Link, Route, Routes } from "react-router-dom";

function HomePage() {
  return (
    <main className="site-shell">
      <header className="header">
        <Link to="/" className="wordmark">CITYPULSE<span>®</span></Link>
        <nav aria-label="Điều hướng chính">
          <Link to="/places">Địa điểm</Link>
          <Link to="/routes">Tuyến khám phá</Link>
          <Link to="/trips">Chuyến đi</Link>
          <Link to="/community">Cộng đồng</Link>
        </nav>
        <Link to="/login" className="header-cta">Đăng nhập ↗</Link>
      </header>
      <section className="hero">
        <p className="eyebrow">DA NANG · LOCAL DISCOVERY PLATFORM</p>
        <h1>Một thành phố.<br /><em>Ngàn cách khám phá.</em></h1>
        <p className="intro">Tìm những nơi đáng nhớ, lắng nghe câu chuyện địa phương và cùng bạn bè tạo nên hành trình của riêng mình.</p>
        <div className="actions">
          <Link className="btn btn-dark" to="/places">Khám phá Đà Nẵng <span>↗</span></Link>
          <Link className="btn btn-light" to="/trips">Lên kế hoạch chuyến đi</Link>
        </div>
        <div className="hero-bottom"><span className="live-dot" /> MADE FOR CURIOUS TRAVELERS <span>16°03′N 108°13′E</span></div>
      </section>
      <section className="feature-list">
        <article><span>01 — DISCOVER</span><h2>Đi xa hơn những điểm quen.</h2><p>Khám phá địa điểm, bộ sưu tập và câu chuyện phía sau từng góc phố.</p><Link to="/places">Tìm địa điểm ↗</Link></article>
        <article><span>02 — PLAN</span><h2>Lịch trình mang dấu ấn riêng.</h2><p>Sắp xếp điểm dừng theo ngày, xem quãng đường và thời gian dự kiến.</p><Link to="/trips">Tạo chuyến đi ↗</Link></article>
        <article><span>03 — TOGETHER</span><h2>Hành trình vui hơn khi có hội bạn.</h2><p>Mời bạn bè cùng xem, đề xuất địa điểm và thống nhất kế hoạch.</p><Link to="/trips">Lập kế hoạch cùng nhau ↗</Link></article>
      </section>
      <footer><span>CITYPULSE®</span><span>MADE FOR DA NANG · 2026</span></footer>
    </main>
  );
}

function PlaceholderPage({ title }: { title: string }) {
  return <main className="placeholder"><Link to="/" className="wordmark">CITYPULSE®</Link><p className="eyebrow">YOUR NEXT CHAPTER STARTS HERE</p><h1>{title}</h1><p>Màn hình khởi tạo — sẽ được triển khai theo đặc tả CITYPULSE v2.3.</p><Link to="/">← Về trang chủ</Link></main>;
}

export default function App() {
  return <Routes>
    <Route path="/" element={<HomePage />} />
    <Route path="/places" element={<PlaceholderPage title="Khám phá địa điểm" />} />
    <Route path="/routes" element={<PlaceholderPage title="Tuyến khám phá" />} />
    <Route path="/trips" element={<PlaceholderPage title="Chuyến đi của tôi" />} />
    <Route path="/community" element={<PlaceholderPage title="Cộng đồng" />} />
    <Route path="/login" element={<PlaceholderPage title="Đăng nhập CITYPULSE" />} />
    <Route path="*" element={<PlaceholderPage title="Không tìm thấy trang" />} />
  </Routes>;
}
