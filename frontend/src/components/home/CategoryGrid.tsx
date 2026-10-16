import React from "react";
import CategoryCard, { CategoryItem } from "./CategoryCard";
import "./CategoryGrid.css";

const CATEGORIES: CategoryItem[] = [
  {
    id: "food",
    title: "Ăn gì Đà Nẵng",
    subtitle: "Đặc sản, quán ngon chuẩn vị",
    countLabel: "140+ quán ngon",
    iconName: "ramen_dining",
    variant: "aqua",
    linkTo: "/places?category=food",
  },
  {
    id: "places",
    title: "Đi đâu cảnh đẹp",
    subtitle: "Điểm đến nổi bật, thắng cảnh",
    countLabel: "28 danh lam thắng cảnh",
    iconName: "landscape",
    variant: "sand",
    linkTo: "/places?category=sightseeing",
  },
  {
    id: "chill",
    title: "Chill & Thư giãn",
    subtitle: "Cà phê, biển, ngắm hoàng hôn",
    countLabel: "52 góc view cực đẹp",
    iconName: "local_cafe",
    variant: "warm",
    linkTo: "/places?category=chill",
  },
  {
    id: "culture",
    title: "Văn hóa & Di sản",
    subtitle: "Di sản, làng nghề, lịch sử",
    countLabel: "19 địa điểm văn hóa",
    iconName: "temple_buddhist",
    variant: "white",
    linkTo: "/places?category=culture",
  },
  {
    id: "nightlife",
    title: "Đà Nẵng Về Đêm",
    subtitle: "Chợ đêm, giải trí, du thuyền",
    countLabel: "35 điểm hẹn rực rỡ",
    iconName: "nightlife",
    variant: "night",
    linkTo: "/places?category=nightlife",
  },
  {
    id: "near-me",
    title: "Khám Phá Gần Tôi",
    subtitle: "Khám phá xung quanh bán kính 2km",
    countLabel: "Mở radar địa phương",
    iconName: "near_me",
    variant: "radar",
    linkTo: "/places?view=map&nearMe=true",
  },
];

export const CategoryGrid: React.FC = () => {
  return (
    <section className="vista-cat-section dnvibe-container">
      {/* SECTION HEADER */}
      <div className="vista-cat-section__header">
        <div>
          <span className="vista-cat-section__eyebrow">CẢM HỨNG BẤT TẬN</span>
          <h2 className="vista-cat-section__title">Hôm nay bạn muốn trải nghiệm gì?</h2>
        </div>
        <p className="vista-cat-section__desc">
          Các danh mục được thiết kế theo nhịp sống người dân bản địa, từ món ăn bình dân đến những góc ngắm hoàng hôn đắt giá nhất.
        </p>
      </div>

      {/* 6 CATEGORY BENTO CARDS GRID */}
      <div className="vista-cat-grid">
        {CATEGORIES.map((cat) => (
          <CategoryCard key={cat.id} category={cat} />
        ))}
      </div>
    </section>
  );
};

export default CategoryGrid;
