"use client";

import { useAppSelector } from "@/lib/hooks/redux";
import type { RootState } from "@/lib/store";
import Image from "next/image";
import Link from "next/link";

const CartBtn = () => {
  const { cart } = useAppSelector((state: RootState) => state.carts);

  return (
    <Link
      href="/cart"
      className="relative p-1 transition-all duration-200 hover:opacity-80 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 rounded-md"
      aria-label={`Shopping cart with ${cart?.totalQuantities || 0} items`}
    >
      <Image
        priority
        src="/icons/cart.svg"
        height={100}
        width={100}
        alt="Shopping cart"
        className="max-w-[22px] max-h-[22px] md:max-w-[24px] md:max-h-[24px]"
      />
      {cart && cart.totalQuantities > 0 && (
        <span className="bg-red-500 text-white rounded-full min-w-[18px] h-[18px] px-1 text-xs font-medium absolute -top-2 -right-1 flex items-center justify-center border-2 border-white shadow-sm animate-pulse">
          {cart.totalQuantities > 99 ? "99+" : cart.totalQuantities}
        </span>
      )}
    </Link>
  );
};

export default CartBtn;
