import React from "react";
import { Link } from "react-router-dom";

export interface CategoryItem {
  id: string;
  title: string;
  subtitle: string;
  countLabel: string;
  iconName: string;
  variant: "aqua" | "sand" | "warm" | "white" | "night" | "radar";
  linkTo: string;
}

export interface CategoryCardProps {
  category: CategoryItem;
}

export const CategoryCard: React.FC<CategoryCardProps> = ({ category }) => {
  return (
    <Link
      to={category.linkTo}
      className={`vista-cat-card vista-cat-card--${category.variant}`}
      title={`Khám phá danh mục ${category.title}`}
    >
      <div className="vista-cat-card__top">
        <div className="vista-cat-card__icon-wrapper">
          <span className="material-symbols-outlined vista-cat-card__icon">
            {category.iconName}
          </span>
        </div>
        <span className="material-symbols-outlined vista-cat-card__arrow">
          {category.variant === "radar" ? "my_location" : "arrow_outward"}
        </span>
      </div>

      <div className="vista-cat-card__body">
        <h3 className="vista-cat-card__title">{category.title}</h3>
        <p className="vista-cat-card__subtitle">{category.subtitle}</p>
      </div>

      <div className="vista-cat-card__footer">
        <span>{category.countLabel}</span>
        {category.variant === "radar" && (
          <span className="material-symbols-outlined text-xs">chevron_right</span>
        )}
      </div>
    </Link>
  );
};

export default CategoryCard;
