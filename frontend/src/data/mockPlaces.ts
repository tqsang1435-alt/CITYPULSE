export interface PlaceItem {
  id: string;
  name: string;
  district: string;
  categoryTag: string;
  rating: number;
  reviewCount: string;
  price: string;
  bestTime: string;
  crowdLevel: string;
  crowdLevelType: "ideal" | "moderate" | "crowded";
  imageUrl: string;
  description: string;
  isFavorite?: boolean;
}

export interface DiscoverPlaceItem {
  id: string;
  name: string;
  district: string;
  rating: number;
  reviewCount: string;
  tags: string[];
  price: string;
  bestTime: string;
  crowdLevel: string;
  crowdLevelType: "ideal" | "moderate" | "crowded";
  imageUrl: string;
  isFavorite?: boolean;
  isFeatured?: boolean;
  mapCoords: { x: number; y: number }; // percentage 0 - 100
  shortSummary?: string;
  category: "beach" | "sightseeing" | "culture" | "food";
  iconName?: string;
}

export const MOCK_LOCAL_PLACES: PlaceItem[] = [
  {
    id: "my-khe-beach",
    name: "Bãi biển Mỹ Khê",
    district: "Quận Sơn Trà",
    categoryTag: "Biển & Thể thao nước",
    rating: 4.8,
    reviewCount: "1.2k",
    price: "Miễn phí",
    bestTime: "Sáng – Hoàng hôn (05:30 - 08:30)",
    crowdLevel: "Đông vừa (Lý tưởng)",
    crowdLevelType: "ideal",
    imageUrl: "/images/my-khe.png",
    description:
      "Được Forbes vinh danh là một trong những bãi biển quyến rũ nhất hành tinh với bờ cát trắng thoai thoải, nước trong và làn sóng êm ả quanh năm.",
    isFavorite: false,
  },
  {
    id: "ba-na-hills",
    name: "Bà Nà Hills & Cầu Vàng",
    district: "Huyện Hòa Vang",
    categoryTag: "Danh thắng Núi Chúa",
    rating: 4.7,
    reviewCount: "2.1k",
    price: "700.000 – 950.000đ",
    bestTime: "Sáng – Chiều (08:00 - 15:00)",
    crowdLevel: "Đông (Nên đi sớm)",
    crowdLevelType: "crowded",
    imageUrl: "/images/ba-na-hills.png",
    description:
      "Đôi bàn tay Phật khổng lồ nâng đỡ Cầu Vàng giữa lưng chừng mây bạt ngàn, khí hậu 4 mùa trong 1 ngày và quần thể kiến trúc làng Pháp cổ kính.",
    isFavorite: true,
  },
  {
    id: "cho-han",
    name: "Chợ Hàn Đà Nẵng",
    district: "Quận Hải Châu",
    categoryTag: "Ẩm thực & Đặc sản",
    rating: 4.6,
    reviewCount: "900",
    price: "30.000 – 80.000đ",
    bestTime: "Sáng – Tối (07:00 - 19:00)",
    crowdLevel: "Đông vừa",
    crowdLevelType: "moderate",
    imageUrl: "/images/cho-han.jpg",
    description:
      "Chợ truyền thống sầm uất bậc nhất trung tâm thành phố với bạt ngàn món ngon dân dã: chả bò, mắm nêm dì Cẩn, bánh khô mè và hoa quả nhiệt đới tươi rói.",
    isFavorite: false,
  },
];

export const MOCK_DISCOVER_PLACES: DiscoverPlaceItem[] = [
  {
    id: "my-khe-beach",
    name: "Bãi biển Mỹ Khê",
    district: "Quận Sơn Trà",
    rating: 4.8,
    reviewCount: "1.3k",
    tags: ["Biển đẹp", "Tắm biển", "Ngắm hoàng hôn", "Chụp ảnh"],
    price: "Miễn phí",
    bestTime: "Tháng 3 – 8",
    crowdLevel: "Đông vừa",
    crowdLevelType: "moderate",
    imageUrl: "/images/my-khe.png",
    isFavorite: false,
    isFeatured: true,
    mapCoords: { x: 70, y: 47 },
    shortSummary: "Bãi cát trắng mịn trải dài, làn nước trong xanh lý tưởng để tắm biển và ngắm bình minh.",
    category: "beach",
    iconName: "beach_access",
  },
  {
    id: "ba-na-hills",
    name: "Bà Nà Hills & Cầu Vàng",
    district: "Huyện Hòa Vang",
    rating: 4.8,
    reviewCount: "2.8k",
    tags: ["Cáp treo", "Cảnh đẹp", "Check-in", "Thiên nhiên"],
    price: "700.000 – 950.000đ",
    bestTime: "Tháng 2 – 8",
    crowdLevel: "Đông",
    crowdLevelType: "crowded",
    imageUrl: "/images/ba-na-hills.png",
    isFavorite: false,
    isFeatured: false,
    mapCoords: { x: 15, y: 28 },
    shortSummary: "Tuyến cáp treo đạt nhiều kỷ lục thế giới và Cầu Vàng nâng đỡ bởi bàn tay rêu phong.",
    category: "sightseeing",
    iconName: "landscape",
  },
  {
    id: "cau-rong",
    name: "Cầu Rồng",
    district: "Quận Hải Châu",
    rating: 4.9,
    reviewCount: "3.1k",
    tags: ["Biểu tượng Đà Nẵng", "Xem phun lửa", "Chụp ảnh", "Về đêm"],
    price: "Miễn phí",
    bestTime: "Tháng 3 – 8",
    crowdLevel: "Đông",
    crowdLevelType: "crowded",
    imageUrl: "/images/cau-rong.png",
    isFavorite: false,
    isFeatured: false,
    mapCoords: { x: 44, y: 45 },
    shortSummary: "Cây cầu hình rồng bắc qua sông Hàn, phun lửa và phun nước vào 21:00 cuối tuần.",
    category: "culture",
    iconName: "wb_sunny",
  },
  {
    id: "cho-han",
    name: "Chợ Hàn",
    district: "Quận Hải Châu",
    rating: 4.6,
    reviewCount: "1.9k",
    tags: ["Ẩm thực", "Đặc sản", "Mua sắm", "Trải nghiệm địa phương"],
    price: "30.000 – 80.000đ",
    bestTime: "Quanh năm",
    crowdLevel: "Đông",
    crowdLevelType: "crowded",
    imageUrl: "/images/cho-han.jpg",
    isFavorite: false,
    isFeatured: false,
    mapCoords: { x: 41, y: 33 },
    shortSummary: "Khu chợ thương mại truyền thống nhộn nhịp với bạt ngàn hải sản khô và món ăn vặt miền Trung.",
    category: "food",
    iconName: "storefront",
  },
];

export interface MapLandmark {
  id: string;
  name: string;
  sublabel?: string;
  x: number;
  y: number;
  iconType: "landmark" | "bridge" | "mountain" | "airport" | "boat" | "cablecar" | "forest";
}

export const MAP_LANDMARKS: MapLandmark[] = [
  { id: "sontra-peninsula", name: "Bán đảo Sơn Trà & Chùa Linh Ứng", x: 75, y: 15, iconType: "forest" },
  { id: "thuan-phuoc", name: "Cầu Thuận Phước", x: 42, y: 22, iconType: "bridge" },
  { id: "airport-dad", name: "Sân bay Đà Nẵng", x: 29, y: 56, iconType: "airport" },
  { id: "ngu-hanh-son", name: "Ngũ Hành Sơn", x: 74, y: 77, iconType: "mountain" },
];

export interface ItineraryStop {
  step: number;
  timeSlot: string;
  duration: string;
  title: string;
  description: string;
  transitInfo: string;
}

export const MOCK_ITINERARY_STOPS: ItineraryStop[] = [
  {
    step: 1,
    timeSlot: "06:00 - 08:00",
    duration: "2 tiếng",
    title: "Đón bình minh tại Bãi biển Mỹ Khê & tắm biển",
    description:
      "Không khí biển sớm trong trẻo, bơi lội hoặc chạy bộ ngắm ngư dân kéo lưới và ngắm ánh mặt trời rực rỡ ló dạng trên biển Đông.",
    transitInfo: "Di chuyển 10 phút (3.2 km)",
  },
  {
    step: 2,
    timeSlot: "08:30 - 10:00",
    duration: "1.5 tiếng",
    title: "Thưởng thức Mì Quảng ếch Bếp Trang",
    description:
      "Mì Quảng ếch om niêu đất nóng hổi đậm đà, ăn kèm bánh tráng mè nướng giòn rụm và rau sống Trà Quế ngát hương.",
    transitInfo: "Di chuyển 20 phút (7.8 km)",
  },
  {
    step: 3,
    timeSlot: "14:00 - 17:00",
    duration: "3 tiếng",
    title: "Khám phá Bán đảo Sơn Trà & Chùa Linh Ứng",
    description:
      "Chiêm bái tượng Phật Bà Quan Âm cao 67m hướng ra biển cả, đón gió đại ngàn và phóng tầm mắt ngắm trọn vẹn bờ vịnh Đà Nẵng.",
    transitInfo: "Di chuyển 15 phút (6.1 km)",
  },
  {
    step: 4,
    timeSlot: "19:30 - 21:30",
    duration: "2 tiếng",
    title: "Ngắm Cầu Rồng phun lửa & dạo Chợ đêm Sơn Trà",
    description:
      "Thưởng thức màn trình diễn rồng phun lửa & phun nước rực sáng sông Hàn cuối tuần, sau đó hòa mình vào chợ đêm nhộn nhịp ăn hải sản nướng.",
    transitInfo: "Kết thúc hành trình trọn vẹn",
  },
];
