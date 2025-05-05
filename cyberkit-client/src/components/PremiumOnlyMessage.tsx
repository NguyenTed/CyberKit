import { Link } from "react-router-dom";
import { FaLock } from "react-icons/fa";

const PremiumOnlyMessage = () => {
  return (
    <div className="max-w-2xl mx-auto mt-20 px-6 py-10 bg-yellow-50 border border-yellow-300 rounded-xl shadow-lg text-center">
      <div className="flex justify-center mb-4">
        <div className="bg-yellow-100 text-yellow-600 rounded-full p-4">
          <FaLock className="text-3xl" />
        </div>
      </div>
      <h2 className="text-3xl font-extrabold text-yellow-800 mb-3">
        Premium Tool Access
      </h2>
      <p className="text-yellow-700 mb-6">
        This tool is reserved for premium users. Upgrade your account to unlock
        all features and tools designed to enhance your productivity.
      </p>
      <Link
        to="/pricing"
        className="inline-block bg-yellow-500 hover:bg-yellow-600 text-white font-semibold px-6 py-3 rounded-lg transition-all"
      >
        Upgrade to Premium
      </Link>
    </div>
  );
};

export default PremiumOnlyMessage;
