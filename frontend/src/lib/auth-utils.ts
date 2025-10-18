import { headers } from "next/headers";

//todo move into middleware

/**
 * Get the current user's role from middleware headers
 * This should be used in server components or API routes
 */
export async function getUserRole(): Promise<string | null> {
  try {
    const headersList = await headers();
    return headersList.get("x-user-role");
  } catch {
    return null;
  }
}

/**
 * Check if the current user has a specific role
 */
export async function hasRole(role: string): Promise<boolean> {
  const userRole = await getUserRole();
  return userRole === role;
}

/**
 * Check if the current user is an admin
 */
export async function isAdmin(): Promise<boolean> {
  return hasRole("admin");
}

/**
 * Check if the current user has any of the specified roles
 */
export async function hasAnyRole(roles: string[]): Promise<boolean> {
  const userRole = await getUserRole();
  return userRole ? roles.includes(userRole) : false;
}
