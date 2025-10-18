import "server-only";

import Stripe from "stripe";
import { assertValue } from "./utils";

export const stripe = new Stripe(
  assertValue(process.env.STRIPE_SECRET_KEY, "Missing STRIPE_SECRET_KEY")
);
