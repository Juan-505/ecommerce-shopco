import { Button } from "@/components/ui/button";
import { auth } from "@/lib/auth";
import { headers } from "next/headers";
import Image from "next/image";
import Link from "next/link";

const TopBanner = async () => {
  const session = await auth.api.getSession({
    headers: await headers(),
  });

  const isLoggedIn = !!session?.user.id;
  const userName = session?.user.name;

  return (
    <div className="bg-black p-2 text-center text-white sm:px-4 xl:px-0">
      <div className="relative mx-auto max-w-frame">
        {isLoggedIn ? (
          <p className="text-xs sm:text-sm">
            Welcome back, <span className="font-medium">{userName}</span>! Check
            out our latest collection.{" "}
            <Link href="/shop" className="font-medium underline">
              Shop Now
            </Link>
          </p>
        ) : (
          <p className="text-xs sm:text-sm">
            Sign up and get 20% off to your first order.{" "}
            <Link href="/sign-up" className="font-medium underline">
              Sign Up Now
            </Link>
          </p>
        )}
        <Button
          variant="ghost"
          className="absolute right-0 top-1/2 hidden h-fit w-fit -translate-y-1/2 p-1 hover:bg-transparent sm:flex"
          size="icon"
          type="button"
          aria-label="close banner"
        >
          <Image
            priority
            src="/icons/times.svg"
            height={13}
            width={13}
            alt="close banner"
          />
        </Button>
      </div>
    </div>
  );
};

export default TopBanner;
