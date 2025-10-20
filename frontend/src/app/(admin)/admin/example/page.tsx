import { AdminGuard } from "@/components/admin/AdminGuard";
import { getUserRole } from "@/lib/auth-utils";

/**
 * Example admin page that demonstrates role-based access control
 */
export default async function AdminExamplePage() {
  const userRole = await getUserRole();

  return (
    <AdminGuard>
      <div className="container mx-auto p-6">
        <h1 className="text-2xl font-bold mb-4">Admin Dashboard</h1>
        <p className="text-gray-600 mb-4">
          Welcome to the admin panel! Your role: <strong>{userRole}</strong>
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-xl font-semibold mb-2">User Management</h2>
            <p className="text-gray-600 mb-4">
              Manage users, roles, and permissions
            </p>
            <button className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
              Manage Users
            </button>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-xl font-semibold mb-2">System Settings</h2>
            <p className="text-gray-600 mb-4">Configure system-wide settings</p>
            <button className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
              Settings
            </button>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-xl font-semibold mb-2">Analytics</h2>
            <p className="text-gray-600 mb-4">
              View system analytics and reports
            </p>
            <button className="bg-purple-500 text-white px-4 py-2 rounded hover:bg-purple-600">
              View Analytics
            </button>
          </div>
        </div>
      </div>
    </AdminGuard>
  );
}
