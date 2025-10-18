import { stripe } from "@/lib/stripe";
import { NextResponse } from "next/server";

export async function POST() {
  try {
    const origin = process.env.BETTER_AUTH_URL || "http://localhost:3000";

    // Create Checkout Sessions from body params.
    const session = await stripe.checkout.sessions.create({
      line_items: [
        {
          // Provide the exact Price ID (for example, price_1234) of the product you want to sell
          price: "{{PRICE_ID}}",
          quantity: 1,
        },
      ],
      mode: "payment",
      success_url: `${origin}/success?session_id={CHECKOUT_SESSION_ID}`,
      cancel_url: `${origin}/?canceled=true`,
    });

    if (!session.url) {
      return NextResponse.json(
        { error: "Failed to create checkout session: missing URL." },
        { status: 500 }
      );
    }

    return NextResponse.redirect(session.url, 303);
  } catch (err) {
    let errorMessage = "An unexpected error occurred.";
    let statusCode = 500;

    if (typeof err === "object" && err !== null) {
      if (
        "message" in err &&
        typeof (err as { message?: unknown }).message === "string"
      ) {
        errorMessage = (err as { message: string }).message;
      }
      if (
        "statusCode" in err &&
        typeof (err as { statusCode?: unknown }).statusCode === "number"
      ) {
        statusCode = (err as { statusCode: number }).statusCode;
      }
    }

    return NextResponse.json({ error: errorMessage }, { status: statusCode });
  }
}
