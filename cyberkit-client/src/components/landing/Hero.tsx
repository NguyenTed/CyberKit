import { motion } from "framer-motion";

type HeroProps = {
  title: string;
  subtitle?: string;
  buttonText?: string;
  buttonLink?: string;
  imageSrc?: string;
};

const Hero: React.FC<HeroProps> = ({
  title,
  subtitle = "Elevate your experience with us bro.",
  buttonText = "Get Started",
  buttonLink = "#",
  imageSrc = "https://source.unsplash.com/featured/?technology",
}) => {
  return (
    <section className="relative flex flex-col md:flex-row items-center justify-between px-6 mt-4 md:px-12 lg:px-20 py-20 bg-gradient-to-br from-blue-500 to-purple-600 text-white">
      {/* Left Content */}
      <div className="max-w-2xl text-center md:text-left">
        <motion.h1
          className="text-4xl md:text-6xl font-bold leading-tight"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
        >
          {title}
        </motion.h1>
        <motion.p
          className="mt-4 text-lg md:text-xl opacity-90 text-justify"
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.7 }}
        >
          {subtitle}
        </motion.p>
        <motion.a
          href={buttonLink}
          className="mt-6 inline-block px-6 py-3 bg-white text-blue-600 font-semibold rounded-full shadow-md hover:bg-gray-200 transition"
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5, duration: 0.7 }}
        >
          {buttonText}
        </motion.a>
      </div>

      {/* Right Image */}
      <motion.div
        className="mt-10 md:mt-0 md:ml-12"
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 0.6, duration: 0.7 }}
      >
        <img
          src={imageSrc}
          alt="Hero"
          className="w-full max-w-md md:max-w-lg rounded-xl shadow-lg"
        />
      </motion.div>
    </section>
  );
};

export default Hero;
