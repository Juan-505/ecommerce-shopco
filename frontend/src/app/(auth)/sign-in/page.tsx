import LoginForm from "@/components/login-form";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import Link from "next/link";

export default function SignInPage() {
  return (
    <main className="grid min-h-screen w-full grid-cols-1 lg:grid-cols-2">
      <section className="hidden flex-col justify-between bg-black p-10 text-white lg:flex">
        <div className="text-xl font-semibold">
          <Link href="/">Axyl</Link>
        </div>
        <div className="max-w-md space-y-4">
          <h1 className="text-4xl leading-tight font-medium xl:text-5xl">
            Elevate your everyday style
          </h1>
          <p>Premium brands, fast shipping, and easy returns.</p>
        </div>
        <div className="text-muted-foreground text-xs">
          © 2025 ShopCo. All rights reserved.
        </div>
      </section>

      <section className="relative flex items-center justify-center bg-gradient-to-br from-indigo-500/15 via-purple-500/15 to-orange-500/15 p-6 lg:p-10 dark:from-indigo-400/10 dark:via-purple-400/10 dark:to-orange-400/10">
        <div className="w-full max-w-md">
          <Card>
            <CardHeader>
              <CardTitle className="text-2xl">Sign in</CardTitle>
              <CardDescription>
                Welcome back. Please sign in to continue.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <LoginForm />
            </CardContent>
          </Card>
        </div>
      </section>
    </main>
  );
}
