import React from "react";
import "./Button.css";

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "ghost";
  size?: "sm" | "md" | "lg";
  fullWidth?: boolean;
  isLoading?: boolean;
  iconLeft?: React.ReactNode;
  iconRight?: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = "primary",
  size = "md",
  fullWidth = false,
  isLoading = false,
  disabled = false,
  iconLeft,
  iconRight,
  className = "",
  type = "button",
  ...props
}) => {
  const classes = [
    "dnvibe-btn",
    `dnvibe-btn--${variant}`,
    `dnvibe-btn--${size}`,
    fullWidth ? "dnvibe-btn--full" : "",
    isLoading || disabled ? "dnvibe-btn--disabled" : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <button
      type={type}
      className={classes}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <span className="dnvibe-btn__spinner" aria-hidden="true" />
      ) : (
        iconLeft && <span className="dnvibe-btn__icon dnvibe-btn__icon--left">{iconLeft}</span>
      )}
      <span>{children}</span>
      {!isLoading && iconRight && (
        <span className="dnvibe-btn__icon dnvibe-btn__icon--right">{iconRight}</span>
      )}
    </button>
  );
};

export default Button;
