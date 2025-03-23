const NavBar: React.FC = () => {
  return (
    <nav className="w-full h-16 bg-white shadow-md px-6 z-40">
      <div className="max-w-6xl mx-auto flex items-center justify-between h-full">
        {/* Left Side */}
        <div className="flex items-center space-x-8 h-full">
          <img
            src="../../logo-no-background.png"
            className="h-6 w-auto cursor-pointer object-contain"
            alt="Logo"
          />
        </div>

        {/* Right Side */}
        <div className="flex space-x-4 h-full">
          {/* Add your right-side buttons or links here */}
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
